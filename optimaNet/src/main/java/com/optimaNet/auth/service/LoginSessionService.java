package com.optimaNet.auth.service;

import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.exception.InvalidKYCDetailsException;
import com.optimaNet.exception.UserIdentityNotFoundException;
import com.optimaNet.exception.UserNotFoundException;

public interface LoginSessionService {

    LoginSession createSession(Long userId) throws UserNotFoundException, UserIdentityNotFoundException, InvalidKYCDetailsException;

    void updateLastActivity(Long sessionId);

    void terminateSessionsActive(Long userId) throws UserNotFoundException;
}
