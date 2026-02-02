package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.*;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.repository.EmployeeOTPRepository;
import com.optimaNet.auth.service.EmailService;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.EmployeeOTPService;
import com.optimaNet.auth.service.UserService;
import com.optimaNet.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeOTPServiceImpl implements EmployeeOTPService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeOTPRepository otpRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public void generateOTP(Long employeeId, OTPPurpose purpose) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not found with id : " + employeeId));

        otpRequestRepository.deleteAllByEmployeeIdAndPurpose(employeeId, purpose);

        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        String plainOtp = String.valueOf(otp);

        String otpHash = passwordEncoder.encode(plainOtp);

        EmployeeOTPRequest request = new EmployeeOTPRequest();
        request.setEmployee(employee);
        request.setOtpHash(otpHash);
        request.setPurpose(purpose);
        request.setAttemptCount(0);
        request.setBlockedUntil(null);

        otpRequestRepository.save(request);
        emailService.sendOtp(employee.getEmail(), "OptimaNet | OTP Verification", String.valueOf(otp));
    }

    @Override
    public boolean verifyOTP(
            Long employeeId,
            String otp,
            OTPPurpose purpose
    ) throws OTPInvalidException, EmployeeNotFoundException,
            OTPBlockedException, OTPExpiredException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Employee not found with id : " + employeeId));

        EmployeeOTPRequest otpRequest =
                otpRequestRepository
                        .findTopByEmployeeIdAndPurposeOrderByCreatedAtDesc(
                                employeeId, purpose)
                        .orElseThrow(() -> new OTPInvalidException("OTP not found"));

        if (otpRequest.getBlockedUntil() != null &&
                otpRequest.getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new OTPBlockedException("OTP is temporarily blocked");
        }

        if (otpRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OTPExpiredException("OTP has expired");
        }

        if (!passwordEncoder.matches(otp, otpRequest.getOtpHash())) {
            otpRequest.setAttemptCount(otpRequest.getAttemptCount() + 1);

            if (otpRequest.getAttemptCount() >= otpRequest.getMaxAttempts()) {
                otpRequest.setBlockedUntil(LocalDateTime.now().plusMinutes(15));
            }

            otpRequestRepository.save(otpRequest);
            throw new OTPInvalidException("Invalid OTP");
        }

        // 🔐 Domain-safe mutation
        if (purpose ==  OTPPurpose.ACCOUNT_ACTIVATION) {
            employee.setStatus(EmployeeStatus.ACTIVE);
            employeeRepository.save(employee);
        }

        otpRequestRepository.delete(otpRequest);
        return true;
    }

    @Override
    public void resendOTP(Long employeeId, OTPPurpose purpose) throws OTPBlockedException, EmployeeNotFoundException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not found with id : " + employeeId));

        EmployeeOTPRequest lastOtp = otpRequestRepository
                .findTopByEmployeeIdAndPurposeOrderByCreatedAtDesc(employeeId, purpose)
                .orElse(null);

        if (lastOtp != null) {

            if (lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new OTPBlockedException("Please wait before requesting a new OTP.");
            }

            if (lastOtp.getResendCount() >= 3) {
                employee.setStatus(EmployeeStatus.SECURITY_SUSPENDED);
                employeeRepository.save(employee);
                throw new OTPBlockedException("OTP resend limit exceeded");
            }

            lastOtp.setResendCount(lastOtp.getResendCount() + 1);
            otpRequestRepository.save(lastOtp);
        }

        generateOTP(employeeId, purpose);
    }


    @Override
    public Page<EmployeeOTPRequest> getAllOtpRequestsByEmployeeId(Long employeeId, Pageable pageable) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not found with id : " + employeeId));

        return otpRequestRepository.findAllByEmployeeId(employeeId, pageable);
    }

    @Override
    public Page<EmployeeOTPRequest> getAllOtpRequests(Pageable pageable) {
        return otpRequestRepository.findAll(pageable);
    }
}
