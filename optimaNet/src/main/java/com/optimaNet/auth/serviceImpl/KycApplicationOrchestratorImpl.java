package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.Applicant;
import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.enums.*;
import com.optimaNet.auth.service.*;
import com.optimaNet.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
@Transactional
public class KycApplicationOrchestratorImpl implements KycApplicationOrchestratorService {

    private final KycApplicationService kycApplicationService;
    private final EmployeeQueryService employeeService;
    private final UserService userService;
    private final KycEmailService kycEmailService;
    private final UserIdentityService identityService;
    private final KycDocumentService documentService;

    @Override
    public Page<KycApplication> getAllKycApplications(Pageable pageable){
        return kycApplicationService.getAllKycApplications(pageable);
    }

    @Override
    public Page<KycApplicationResponse> getKycApplicationsByEmployeeId(Long employeeId, Pageable pageable){
        return kycApplicationService.getKycApplicationsByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<UnassignedApplicationResponse> getKycApplicationsByStatus(KYCStatus status, Pageable pageable) {
        return kycApplicationService.getKycApplicationsByStatus(status, pageable);
    }

    @Override
    public Page<UnassignedApplicationResponse> getUnassignedApplications(KYCStatus status, Pageable pageable) throws AccessDeniedException {
        if (!EnumSet.of(KYCStatus.KYC_PENDING).contains(status)) {
            throw new AccessDeniedException("Invalid status for officer view");
        }
        return kycApplicationService.getKycApplicationsByStatus(status, pageable);
    }


    @Override
    public Long createApplication(Long userId) throws UserNotFoundException, AccessDeniedException, DuplicateResourceException, UserIdentityNotFoundException {
        enforceActiveUser(userId);
        System.out.println("CREATE APPLICATION HIT FOR USER-STATUS ACTIVE");
        UserIdentity identity = enforceUserDetails(userId);
        System.out.println("CREATE APPLICATION HIT FOR USER-IDENTITY-STATUS ACTIVE");

        Long applicationId = kycApplicationService.createApplication(userId);
        System.out.println("CREATE APPLICATION HIT FOR Created");

        kycEmailService.sendKycCreated(identity.getFullName(), identity.getEmail(), "OptimaNet | KYC Validation Application");
        System.out.println("CREATE APPLICATION HIT FOR Email service");

        return applicationId;
    }

    @Override
    public void assignToEmployee(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException, UserIdentityNotFoundException {
        enforceKYCInPending(applicationId);
        enforceActiveEmployee(employeeId);
        kycApplicationService.assignToEmployee(applicationId, employeeId);

    }

    @Override
    public void lockForReview(Long applicationId) throws KycApplicationNotFoundException, AccessDeniedException {
        enforceKYCInPending(applicationId);
        kycApplicationService.lockForReview(applicationId);
    }

    @Override
    public void approve(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException {

        Employee emp = enforceActiveEmployee(employeeId);
        System.out.println("APPROVE APPLICATION HITS EMPLOYEE-STATUS=" + emp.getStatus());

        KycApplication application = enforceInReview(applicationId);
        System.out.println("APPROVE APPLICATION HITS APPLICATION-STATUS=" + application.getStatus());

        enforceAssignment(applicationId, employeeId);
        System.out.println("APPROVE APPLICATION HITS APPLICATION-pairing with Employee =" + application.getEmployee().getId());

        documentService.ensureAllMandatoryDocumentsApproved(applicationId);
        System.out.println("APPROVE APPLICATION HITS APPLICATION-ALL-DOCUMENTS-APPROVED");

        kycApplicationService.approve(applicationId, employeeId);
        System.out.println("APPROVE APPLICATION HITS APPLICATION-APPROVAL");

        UserIdentity identity = enforceUserDetails(application.getUser().getId());
        System.out.println("APPROVE APPLICATION HITS FETCH-USER-DETAILS");

        kycEmailService.sendKycApproved(identity.getFullName(), identity.getEmail(), emp.getEmail(), "OptimaNet | KYC Validation Approval");
        System.out.println("APPROVE APPLICATION HITS APPROVAL EMAIL IS SENT");

    }

    @Override
    public void reject(Long applicationId, Long employeeId, String reason) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException {
        Employee emp = enforceActiveEmployee(employeeId);
        KycApplication application = enforceInReview(applicationId);
        enforceAssignment(applicationId, employeeId);
        kycApplicationService.reject(applicationId, employeeId, reason);
        UserIdentity identity = enforceUserDetails(application.getUser().getId());

        kycEmailService.sendKycRejected(identity.getFullName(), identity.getEmail(), emp.getEmail(), "OptimaNet | KYC Validation Rejection", reason);
    }

    @Override
    public Applicant getApplicantDetails(Long applicationId) throws KycApplicationNotFoundException, UserIdentityNotFoundException {
        KycApplication application = kycApplicationService.getApplicationById(applicationId);
        System.out.println("APPLICANT-DETAILS HIT FOR SERVICE-ORCHESTRATOR");
        return kycApplicationService.getApplicantDetails(applicationId);
    }

    //    ================ ENFORCES =================
    private Employee enforceActiveEmployee(Long employeeId) throws EmployeeNotFoundException, AccessDeniedException {
        Employee employee = employeeService.getEmployeeByIdAndRole(employeeId, Role.KYC_OFFICER);
        if(employee.getStatus() != EmployeeStatus.ACTIVE){
            throw new AccessDeniedException("Inactive admin cannot perform actions");
        }
        return employee;
    }

    private KycApplication enforceInReview(Long applicationId) throws AccessDeniedException, KycApplicationNotFoundException {
        KycApplication application = kycApplicationService.getApplicationById(applicationId);
        if (application.getStatus() != KYCStatus.KYC_IN_REVIEW) {
            throw new AccessDeniedException("KYC application is not under review");
        }
        return application;
    }

    private void enforceAssignment(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException {
        KycApplication application = kycApplicationService.getApplicationById(applicationId);
        Employee employee = employeeService.getEmployeeByIdAndRole(employeeId, Role.KYC_OFFICER);
        if (application.getEmployee() == null ||
                !application.getEmployee().getId().equals(employeeId)) {
            throw new AccessDeniedException("Employee not assigned to this KYC application");
        }
    }

    private void enforceKYCInPending(Long applicationId) throws AccessDeniedException, KycApplicationNotFoundException {
        KycApplication application = kycApplicationService.getApplicationById(applicationId);
        if (application.getStatus() != KYCStatus.KYC_PENDING) {
            throw new AccessDeniedException("KYC application is not in pending state");
        }
    }

    private void enforceActiveUser(Long userId) throws UserNotFoundException, AccessDeniedException {
        User user = userService.getUserById(userId);

        if(user.getUserStatus() != UserStatus.APPROVAL_PENDING){
            throw new AccessDeniedException("Unauthorised users cannot apply for KYC validation");
        }
    }

    private UserIdentity enforceUserDetails(Long userId) throws UserIdentityNotFoundException {
        return identityService.getIdentityByUserId(userId);
    }


}
