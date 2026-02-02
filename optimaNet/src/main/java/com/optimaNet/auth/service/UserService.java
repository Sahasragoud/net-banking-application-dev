package com.optimaNet.auth.service;

import com.optimaNet.auth.entity.User;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;

public interface UserService {

    User createInitiatedUser();

    void activateUser(Long userId) throws UserNotFoundException;

    void blockUser(Long userId) throws UserNotFoundException, UserIdentityNotFoundException;

    User getUserById(Long userId) throws UserNotFoundException;

}
