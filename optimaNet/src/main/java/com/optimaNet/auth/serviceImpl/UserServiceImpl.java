package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.service.UserIdentityService;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserIdentityService identityService;

    @Override
    public User createInitiatedUser() {
        User user = new User();
        user.setUserStatus(UserStatus.INITIATED);
        return userRepository.save(user);
    }


    @Override
    public void activateUser(Long userId) throws UserNotFoundException {
        User user = getUserById(userId);
            user.setBlocked(false);
            user.setUserStatus(UserStatus.ACTIVE);
            userRepository.save(user);
    }

    @Override
    public void blockUser(Long userId) throws UserNotFoundException, UserIdentityNotFoundException {
        User user = getUserById(userId);
        user.setUserStatus(UserStatus.BLOCKED);
        user.setBlocked(true);
        identityService.rejectIdentity(userId);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        return  userRepository.findById(userId)
                .orElseThrow( () -> new UserNotFoundException("User is not found with id : " + userId));

    }
}
