package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.RegistrationRequest;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.service.CryptoService;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import com.optimaNet.auth.repository.UserIdentityRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.UserIdentityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserIdentityServiceImpl implements UserIdentityService {

    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final CryptoService cryptoService;

    @Override
    public void createUserIdentity(Long userId, RegistrationRequest request) throws UserNotFoundException{
        User user = userRepository.findById(userId)
                 .orElseThrow( () -> new UserNotFoundException("User is not found with id : " + userId));

        UserIdentity identity = new UserIdentity();
        identity.setUser(user);
        identity.setFullName(request.getFullName());
        identity.setHashedAadhaarNumber(DigestUtils.sha256Hex(request.getAadhaarNumber()));
        identity.setEncryptedAadhaarNumber(cryptoService.encrypt(request.getAadhaarNumber()));
        String maskedAadhaar = "XXXX-XXXX-" + request.getAadhaarNumber().substring(request.getAadhaarNumber().length()-4);
        identity.setMaskedAadhaarNumber(maskedAadhaar);
        identity.setMobileNumber(request.getMobileNumber());
        identity.setEmail(request.getEmailAddress());
        identity.setEmailVerified(false);
        identity.setVerificationStatus(KYCStatus.KYC_PENDING);
        userIdentityRepository.save(identity);

        user.setUserStatus(UserStatus.OTP_PENDING);
        userRepository.save(user);
    }

    @Override
    public void markEmailVerified(Long userId) throws UserNotFoundException {
        UserIdentity userIdentity = userIdentityRepository.findByUserId(userId)
                .orElseThrow( () -> new UserNotFoundException("User is not found with id : " + userId));

        userIdentity.setEmailVerified(true);
        userIdentityRepository.save(userIdentity);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id :"  + userId));
    }

    @Override
    public UserIdentity getIdentityByEmail(String email) throws UserIdentityNotFoundException {
        return userIdentityRepository.findByEmail(email)
                .orElseThrow(() -> new UserIdentityNotFoundException("User is not found with email : " + email));
    }

    @Override
    public boolean aadhaarExists(String aadhaarNumber) {
        String encryptedAadhaar = DigestUtils.sha256Hex(aadhaarNumber);
        return userIdentityRepository.existsByEncryptedAadhaarNumber(encryptedAadhaar);
    }

    @Override
    public boolean mobileExists(String mobileNumber) {
        return userIdentityRepository.existsByMobileNumber(mobileNumber);
    }

    @Override
    public boolean emailExists(String email) {
        return userIdentityRepository.existsByEmail(email);
    }

    @Override
    public void rejectIdentity(Long userId) throws UserIdentityNotFoundException {
         UserIdentity identity = userIdentityRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdentityNotFoundException("User is not found with id : " + userId));
         identity.setVerificationStatus(KYCStatus.REJECTED);
         userIdentityRepository.save(identity);
    }

    public UserIdentity getIdentityByUserId(Long userId) throws UserIdentityNotFoundException {
        return  userIdentityRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdentityNotFoundException("User is not found with id : " + userId));

    }
}
