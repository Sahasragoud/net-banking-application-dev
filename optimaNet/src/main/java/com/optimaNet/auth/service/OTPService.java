package com.optimaNet.auth.service;

import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.auth.entity.OTPRequest;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface OTPService {
    void generateOTP(Long userId, OTPPurpose purpose)
            throws UserNotFoundException, UserIdentityNotFoundException;

    boolean verifyOTP(Long userId, String otp, OTPPurpose purpose)
            throws OTPBlockedException, OTPExpiredException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException;

    void resendOTP(Long userId, OTPPurpose purpose) throws OTPBlockedException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException;

    Page<OTPRequest> getAllOtpRequestsByUserId(Long UserId, Pageable pageable) throws UserNotFoundException;

    Page<OTPRequest> getAllOtpRequests(Pageable pageable);

}
