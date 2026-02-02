package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.EmployeeRequest;
import com.optimaNet.auth.dto.EmployeeResponse;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.EmployeeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeActionOrchestratorService {
    Long createEmployee(EmployeeRequest request) throws EmployeeNotFoundException, AccessDeniedException, DuplicateResourceException;

    void activateEmployee( Long employeeId) throws  AccessDeniedException, EmployeeNotFoundException;

    void suspendEmployee(Long employeeId) throws EmployeeNotFoundException, AccessDeniedException;

    void terminateEmployee( Long employeeId) throws AccessDeniedException, EmployeeNotFoundException;

    Page<EmployeeResponse> getAllEmployees(Pageable pageable) throws AccessDeniedException;

}
