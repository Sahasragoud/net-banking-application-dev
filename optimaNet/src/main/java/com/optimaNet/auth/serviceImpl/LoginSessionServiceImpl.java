package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.LoginSession;
import com.optimaNet.auth.enums.LoginSessionStatus;
import com.optimaNet.auth.repository.LoginSessionRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.auth.service.LoginSessionService;
import com.optimaNet.exception.UserNotFoundException;
import org.hibernate.SessionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.optimaNet.auth.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginSessionServiceImpl implements LoginSessionService {

    private final LoginSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public LoginSessionServiceImpl(LoginSessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LoginSession createSession(Long userId) throws UserNotFoundException  {
       User user =  userRepository.findById(userId).orElseThrow(
               () -> new UserNotFoundException("User not found with id: " + userId)
       );

        LoginSession session = new LoginSession();
        session.setUser(user);
        return sessionRepository.save(session);
    }

    @Override
    public void updateLastActivity(Long sessionId) {
        LoginSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new SessionException("Session not found with id" + sessionId));
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public void terminateSessionsActive(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("No user found with id : " + userId)
        );
        Page<LoginSession> activeSessions= sessionRepository.findAllByUserIdAndIsActiveTrue(userId, Pageable.unpaged());
        for(LoginSession session : activeSessions){
            session.setLoginSessionStatus(LoginSessionStatus.TERMINATED);
            session.setActive(false);
            sessionRepository.save(session);
        }


    }
}
