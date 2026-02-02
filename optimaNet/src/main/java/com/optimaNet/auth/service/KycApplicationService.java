package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.Applicant;
import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.exception.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KycApplicationService {

    Page<KycApplication> getAllKycApplications(Pageable pageable);

    Page<KycApplicationResponse> getKycApplicationsByEmployeeId(Long employeeId, Pageable pageable);

    Page<UnassignedApplicationResponse> getKycApplicationsByStatus(KYCStatus status, Pageable pageable);

    Long createApplication(Long userId) throws UserNotFoundException, AccessDeniedException,  DuplicateResourceException; //return applicationId

    void assignToEmployee(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException;

    KycApplication lockForReview(Long applicationId) throws KycApplicationNotFoundException;

    void approve(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException;

    void reject(Long applicationId, Long employeeId, String reason) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, AccessDeniedException;

    KycApplication getApplicationById(Long applicationId) throws KycApplicationNotFoundException;

    Applicant getApplicantDetails(Long applicationId) throws KycApplicationNotFoundException, UserIdentityNotFoundException;
}
