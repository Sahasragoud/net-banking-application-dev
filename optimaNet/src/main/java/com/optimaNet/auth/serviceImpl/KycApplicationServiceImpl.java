package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.Applicant;
import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.repository.KycApplicationRepository;
import com.optimaNet.auth.repository.UserIdentityRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.CryptoService;
import com.optimaNet.auth.service.KycApplicationService;
import com.optimaNet.auth.service.UserIdentityService;
import com.optimaNet.exception.*;
import com.twilio.rest.microvisor.v1.App;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class KycApplicationServiceImpl implements KycApplicationService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final KycApplicationRepository kycApplicationRepository;
    private final UserIdentityService userIdentityService;
    private final CryptoService cryptoService;

    @Override
    public Page<KycApplication> getAllKycApplications(Pageable pageable) {
        return kycApplicationRepository.findAll(pageable);
    }

    @Override
    public Page<KycApplicationResponse> getKycApplicationsByEmployeeId(Long employeeId, Pageable pageable) {
            Page<KycApplication> page =
                    kycApplicationRepository.findByEmployeeId(employeeId, pageable);

            return page.map(application -> {
                KycApplicationResponse response = new KycApplicationResponse();
                response.setId(application.getId());
                response.setStatus(application.getStatus());
                response.setAssignedAt(application.getAssignedAt());

                // Safe access — IDs do NOT trigger lazy loading
                response.setUserId(application.getUser().getId());
                response.setEmployeeId(application.getEmployee().getId());

                return response;
            });
        }

    @Override
    public Page<UnassignedApplicationResponse> getKycApplicationsByStatus(KYCStatus status, Pageable pageable) {
        Page<KycApplication>  applications = kycApplicationRepository.findByStatus(status, pageable);
        return applications.map( app -> {
                    UnassignedApplicationResponse res = new UnassignedApplicationResponse();
                    res.setApplicationId(app.getId());
                    res.setStatus(app.getStatus());
                    res.setUserId(app.getUser().getId());
                    res.setCreatedAt(app.getCreatedAt());
                    return res;
                });
    }

    @Override
    public Long createApplication(Long userId) throws UserNotFoundException, DuplicateResourceException {
        User user = userRepository.findByIdAndUserStatus(userId, UserStatus.APPROVAL_PENDING)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId+ " for kyc validation"));

        if (kycApplicationRepository.existsByUserIdAndStatusIn(
                userId,
                List.of(KYCStatus.KYC_PENDING, KYCStatus.KYC_IN_REVIEW)
        )) {
            throw new DuplicateResourceException("Active KYC application already exists");
        }

        KycApplication application = new KycApplication();
        application.setUser(user);

        kycApplicationRepository.save(application);
        return application.getId();
    }

    @Override
    public void assignToEmployee(Long applicationId, Long employeeId)
            throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException {

        KycApplication application = kycApplicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new KycApplicationNotFoundException("Application not found with id " + applicationId));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id " + employeeId));

        if (employee.getRole() != Role.KYC_OFFICER) {
            throw new AccessDeniedException("Employee does not have access for KYC validation");
        }

        application.setAssignedAt(LocalDateTime.now());
        application.setEmployee(employee);
        application.setStatus(KYCStatus.KYC_IN_REVIEW);
        kycApplicationRepository.save(application);
    }

    @Override
    public KycApplication lockForReview(Long applicationId) throws KycApplicationNotFoundException {

        return kycApplicationRepository.lockPendingById(applicationId)
                .orElseThrow(() -> new KycApplicationNotFoundException("Application is not in KYC_PENDING state or does not exist"));
    }

    @Override
    public void approve(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException {

        KycApplication application = kycApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new KycApplicationNotFoundException("Application not found with id " + applicationId));

        Employee employee = employeeRepository.findByIdAndRole(employeeId, Role.KYC_OFFICER)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id" + employeeId + " for role " + Role.KYC_OFFICER));


        User user = userRepository.findByIdAndUserStatus(application.getUser().getId(), UserStatus.APPROVAL_PENDING)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + application.getUser().getId() + "holding application with id " + applicationId));

        application.setStatus(KYCStatus.APPROVED);
        application.setDecidedAt(LocalDateTime.now());
        application.setDecision_reason("Documents are verified");

        kycApplicationRepository.save(application);

        user.setUserStatus(UserStatus.ACTIVE);
        user.setBlocked(false);
        user.setCustomerId(generateCustomerCode());

        userRepository.save(user);

    }

    @Override
    public void reject(Long applicationId, Long employeeId, String reason) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException {

        KycApplication application = kycApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new KycApplicationNotFoundException("Application not found with id " + applicationId));

        Employee employee = employeeRepository.findByIdAndRole(employeeId, Role.KYC_OFFICER)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id" + employeeId + " for role " + Role.KYC_OFFICER));

        User user = userRepository.findByIdAndUserStatus(application.getUser().getId(), UserStatus.APPROVAL_PENDING)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + application.getUser().getId() + "holding application with id " + applicationId));


        application.setStatus(KYCStatus.REJECTED);
        application.setDecidedAt(LocalDateTime.now());
        application.setDecision_reason(reason);

        kycApplicationRepository.save(application);

        user.setUserStatus(UserStatus.BLOCKED);
        user.setBlocked(true);

        userRepository.save(user);
    }

    @Override
    public KycApplication getApplicationById(Long applicationId) throws KycApplicationNotFoundException {

        return kycApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new KycApplicationNotFoundException("Application not found with id " + applicationId));
    }

    @Override
    public Applicant getApplicantDetails(Long applicationId) throws KycApplicationNotFoundException, UserIdentityNotFoundException {

        KycApplication application = getApplicationById(applicationId);
        System.out.println("APPLICANT-DETAILS HIT FOR APPLICATION-FETCH");

        UserIdentity identity = userIdentityService.getIdentityByUserId(application.getUser().getId());
        System.out.println("APPLICANT-DETAILS HIT FOR USER-IDENTITY-FETCH");

        Applicant response = new Applicant();

        response.setEmail(identity.getEmail());
        response.setFullName(identity.getFullName());
        response.setMobileNumber(identity.getMobileNumber());
        response.setUserId(identity.getUser().getId());
        System.out.println("APPLICANT-DETAILS HIT FOR DECRYPT-USER-AADHAAR");
        response.setAadhaarNumber(cryptoService.decrypt(identity.getEncryptedAadhaarNumber()));

        return response;
    }

    public String generateCustomerCode(){
        Long seq = userRepository.getNextCustomerID();
        return "OPT-CUST-" + Year.now().getValue() + "-" + String.format("%06d", seq);
    }
}
