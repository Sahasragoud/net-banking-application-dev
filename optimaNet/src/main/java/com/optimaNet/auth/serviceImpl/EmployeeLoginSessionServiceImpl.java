package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.entity.EmployeeLoginSession;
import com.optimaNet.auth.enums.LoginSessionStatus;
import com.optimaNet.auth.repository.EmployeeLoginSessionRepository;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.EmployeeLoginSessionService;
import com.optimaNet.exception.EmployeeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeLoginSessionServiceImpl implements EmployeeLoginSessionService {

    private final EmployeeLoginSessionRepository sessionRepository;
    private final EmployeeRepository employeeRepository;
    private HttpServletRequest request;

    @Override
    public EmployeeLoginSession createSession(Long employeeId, String clientIp) throws EmployeeNotFoundException {

        Employee employee =  employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found with id: " + employeeId)
        );
        EmployeeLoginSession session = new EmployeeLoginSession();
        session.setEmployee(employee);
        session.setLoginSessionStatus(LoginSessionStatus.OTP_PENDING);
        session.setActive(true);
        session.setLastActivityAt(LocalDateTime.now());
        session.setIp_address(maskIp(clientIp));
        return sessionRepository.save(session);
    }

    @Override
    public void updateLastActivity(Long sessionId) {
        EmployeeLoginSession session = sessionRepository.findByIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new SessionException("Session not found with id" + sessionId));
        session.setLastActivityAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public void terminateSessionsActive(Long employeeId) throws EmployeeNotFoundException {
        Employee employee =  employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found with id: " + employeeId)
        );

        List<EmployeeLoginSession> activeSessions= sessionRepository.findAllByEmployeeIdAndIsActiveTrue(employeeId);
        for(EmployeeLoginSession session : activeSessions){
            session.setLoginSessionStatus(LoginSessionStatus.TERMINATED);
            session.setActive(false);
            session.setLastActivityAt(LocalDateTime.now());
            sessionRepository.save(session);
        }


    }

    public static String maskIp(String ip) {
        if (ip.contains(".")) {
            return ip.replaceAll("(\\d+\\.\\d+\\.)(\\d+\\.\\d+)", "$1xxx.xxx");
        }
        return ip;
    }

}
