package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.auth.enums.UserStatus;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.repository.KycApplicationRepository;
import com.optimaNet.auth.repository.UserRepository;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KycApplicationServiceImplTest {

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  EmployeeRepository employeeRepository;

    @Mock
    private  KycApplicationRepository kycApplicationRepository;

    @InjectMocks
    private KycApplicationServiceImpl service;

    private User user;
    private Employee emp;
    private KycApplication application;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setUserStatus(UserStatus.APPROVAL_PENDING);

        emp = new Employee();
        emp.setId(10L);
        emp.setRole(Role.KYC_OFFICER);

        application = new KycApplication();
        application.setUser(user);
        application.setId(100L);
        application.setStatus(KYCStatus.KYC_PENDING);
    }

    @Test
    void createApplication_success() throws Exception{
        when(userRepository.findByIdAndUserStatus(1L, UserStatus.APPROVAL_PENDING))
                .thenReturn(Optional.of(user));

        when(kycApplicationRepository.existsByUserIdAndStatusIn(
                eq(1L),
                anyList()
        )).thenReturn(false);

        when(kycApplicationRepository.save(any(KycApplication.class)))
                .thenAnswer(invocation -> {
                    KycApplication app = invocation.getArgument(0);
                    app.setId(100L);
                    return app;
                });

        Long applicationId = service.createApplication(1L);

        assertNotNull(applicationId);
        assertEquals((Long) 100l, applicationId);
        verify(kycApplicationRepository).save(any(KycApplication.class));
    }

    @Test
    void createApplication_userNotInApprovalPending_throwException(){

        //user exists in approval pending state
        when(userRepository.findByIdAndUserStatus(
                1L,
                UserStatus.APPROVAL_PENDING
        )).thenReturn(Optional.empty());

        //expect exception
        assertThrows(
                UserNotFoundException.class,
                () -> service.createApplication(user.getId())
        );

        verify(kycApplicationRepository, never()).save(any());
    }
    @Test
    void createApplication_duplicateActiveApplication_throwException(){

        //user exists and is eligible
        when(userRepository.findByIdAndUserStatus(
                1L ,
                UserStatus.APPROVAL_PENDING
        )).thenReturn(Optional.of(user));

        //duplicate application exists
        when(kycApplicationRepository.existsByUserIdAndStatusIn(
                eq(1L),
                anyList()
        )).thenReturn(true);

        //expect exception
        assertThrows(
                DuplicateResourceException.class,
                () -> service.createApplication(1L)
        );

        //ensure no save happened
        verify(kycApplicationRepository, never()).save(any());
    }

    @Test
    void assignToEmployee_Success() throws Exception {

        when(kycApplicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        when(employeeRepository.findById(10L))
                .thenReturn(Optional.of(emp)); // THIS WAS MISSING

        emp.setRole(Role.KYC_OFFICER);

        service.assignToEmployee(100L, 10L);

        assertNotNull(application.getAssignedAt());
        assertEquals(emp, application.getEmployee());

        verify(kycApplicationRepository).save(application);
    }

    @Test
    void approveApplication_success() throws Exception {

        when(kycApplicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        when(employeeRepository.findByIdAndRole(10L, Role.KYC_OFFICER))
                .thenReturn(Optional.of(emp));

        when(userRepository.findByIdAndUserStatus(1L, UserStatus.APPROVAL_PENDING))
                .thenReturn(Optional.of(user));

        when(userRepository.getNextCustomerID())
                .thenReturn(1L);

        service.approve(100L, 10L);

        assertEquals(KYCStatus.APPROVED, application.getStatus());
        assertEquals(UserStatus.ACTIVE, user.getUserStatus());
        assertNotNull(user.getCustomerId());

        verify(userRepository).save(user);
        verify(kycApplicationRepository).save(application);
    }


    @Test
    void rejectApplication_success() throws Exception{
        when(kycApplicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        when(employeeRepository.findByIdAndRole(10L, Role.KYC_OFFICER))
                .thenReturn(Optional.of(emp));

        when(userRepository.findByIdAndUserStatus(1L, UserStatus.APPROVAL_PENDING))
                .thenReturn(Optional.of(user));

        service.reject(100L, 10L, "Documents are not valid");

        assertEquals(KYCStatus.REJECTED, application.getStatus());
        assertEquals(UserStatus.BLOCKED, user.getUserStatus());
        assertNull(user.getCustomerId(), application.getDecision_reason());

        verify(userRepository).save(user);
        verify(kycApplicationRepository).save(application);
    }

    @Test
    void assignToEmployee_nonKycOfficer_throwAccessDenied() {

        emp.setRole(Role.SUPERVISOR); // non-KYC role

        when(kycApplicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        when(employeeRepository.findById(10L))
                .thenReturn(Optional.of(emp));

        assertThrows(
                AccessDeniedException.class,
                () -> service.assignToEmployee(100L, 10L)
        );

        verify(kycApplicationRepository, never()).save(any());
    }



}
