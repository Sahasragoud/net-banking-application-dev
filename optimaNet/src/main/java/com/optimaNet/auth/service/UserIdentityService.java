package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.RegistrationRequest;
import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface UserIdentityService {
    void createUserIdentity(
            Long userId,
            RegistrationRequest request
    ) throws UserNotFoundException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void markEmailVerified(Long userId) throws UserNotFoundException;

    UserIdentity getIdentityByEmail(String email) throws  UserIdentityNotFoundException;

    boolean aadhaarExists(String aadhaarNumber);

    boolean mobileExists(String mobileNumber);

    boolean emailExists(String email);

    void rejectIdentity(Long userId) throws UserIdentityNotFoundException;

    UserIdentity getIdentityByUserId(Long userId) throws UserIdentityNotFoundException;


}
