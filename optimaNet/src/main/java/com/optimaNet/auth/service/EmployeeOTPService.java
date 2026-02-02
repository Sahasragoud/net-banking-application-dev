package com.optimaNet.auth.service;

import com.optimaNet.auth.entity.EmployeeOTPRequest;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.exception.EmployeeNotFoundException;
import com.optimaNet.exception.OTPBlockedException;
import com.optimaNet.exception.OTPExpiredException;
import com.optimaNet.exception.OTPInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeOTPService {
    void generateOTP(Long employeeId, OTPPurpose purpose) throws EmployeeNotFoundException;

    boolean verifyOTP(Long employeeId, String otp, OTPPurpose purpose) throws EmployeeNotFoundException, OTPBlockedException, OTPInvalidException, OTPExpiredException;

    void resendOTP(Long employeeId, OTPPurpose purpose) throws OTPBlockedException, EmployeeNotFoundException;

    Page<EmployeeOTPRequest> getAllOtpRequestsByEmployeeId(Long employeeId, Pageable pageable) throws EmployeeNotFoundException;

    Page<EmployeeOTPRequest> getAllOtpRequests(Pageable pageable);

}
