package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.Applicant;
import com.optimaNet.auth.dto.KycApplicationResponse;
import com.optimaNet.auth.dto.UnassignedApplicationResponse;
import com.optimaNet.auth.entity.KycApplication;
import com.optimaNet.auth.enums.KYCStatus;
import com.optimaNet.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface KycApplicationOrchestratorService {

    Page<KycApplication> getAllKycApplications(Pageable pageable);

    Page<KycApplicationResponse> getKycApplicationsByEmployeeId(Long employeeId, Pageable pageable);

    Page<UnassignedApplicationResponse> getKycApplicationsByStatus(KYCStatus status, Pageable pageable);

    Page<UnassignedApplicationResponse> getUnassignedApplications(KYCStatus status, Pageable pageable) throws AccessDeniedException;

    Long createApplication(Long userId) throws UserNotFoundException, AccessDeniedException, DuplicateResourceException, UserIdentityNotFoundException; //return applicationId

    void assignToEmployee(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, AccessDeniedException,  UserIdentityNotFoundException;

    void lockForReview(Long applicationId) throws KycApplicationNotFoundException, AccessDeniedException;

    void approve(Long applicationId, Long employeeId) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException;

    void reject(Long applicationId, Long employeeId, String reason) throws KycApplicationNotFoundException, EmployeeNotFoundException, UserNotFoundException, UserIdentityNotFoundException, AccessDeniedException;

    Applicant getApplicantDetails(Long applicationId) throws KycApplicationNotFoundException, UserIdentityNotFoundException;

}
