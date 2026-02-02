package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.EmployeeRequest;
import com.optimaNet.auth.dto.EmployeeResponse;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.EmployeeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface EmployeeQueryService {
    Long createEmployee(EmployeeRequest request) throws EmployeeNotFoundException, DuplicateResourceException;

    void updateStatus(Long employeeId, EmployeeStatus employeeStatus) throws EmployeeNotFoundException;

    Employee getEmployeeByIdAndRole(Long employeeId, Role role) throws EmployeeNotFoundException;

    Page<EmployeeResponse> getAll(Pageable pageable);

    Employee getEmployeeByEmployeeCode(String EmployeeCode) throws EmployeeNotFoundException;
}
