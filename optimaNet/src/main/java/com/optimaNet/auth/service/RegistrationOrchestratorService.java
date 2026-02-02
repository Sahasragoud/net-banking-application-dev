package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.RegistrationRequest;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.enums.OTPPurpose;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.exception.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface RegistrationOrchestratorService {
    User startRegistration(RegistrationRequest request) throws InvalidKYCDetailsException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    String verifyRegistrationOTP(Long userId, String otp) throws OTPExpiredException, OTPBlockedException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException;

    void resendRegistrationOTP(Long userId, OTPPurpose purpose, UserStatus status) throws OTPBlockedException, OTPInvalidException, UserNotFoundException, UserIdentityNotFoundException;

}
