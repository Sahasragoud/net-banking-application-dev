package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.EmployeeRequest;
import com.optimaNet.auth.dto.EmployeeResponse;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.EmployeeQueryService;
import com.optimaNet.auth.service.EmployeeActionOrchestratorService;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeActionOrchestratorServiceImpl
        implements EmployeeActionOrchestratorService {

    private final EmployeeQueryService adminService;
    private final EmployeeRepository employeeRepository;

    @Override
    public Long createEmployee(EmployeeRequest request) throws DuplicateResourceException, EmployeeNotFoundException, AccessDeniedException {
        Employee admin = getAuthenticatedAdmin();
        enforceActive(admin);

        return adminService.createEmployee(request);
    }

    @Override
    public void activateEmployee(Long employeeId) throws EmployeeNotFoundException, AccessDeniedException {
        Employee admin = getAuthenticatedAdmin();
        enforceActive(admin);

        adminService.updateStatus(employeeId, EmployeeStatus.ACTIVE);
    }

    @Override
    public void suspendEmployee(Long employeeId) throws EmployeeNotFoundException, AccessDeniedException {
        Employee admin = getAuthenticatedAdmin();
        enforceActive(admin);

        adminService.updateStatus(employeeId, EmployeeStatus.ADMIN_SUSPENDED);
    }

    @Override
    public void terminateEmployee(Long employeeId) throws EmployeeNotFoundException, AccessDeniedException {
        Employee admin = getAuthenticatedAdmin();
        enforceActive(admin);

        adminService.updateStatus(employeeId, EmployeeStatus.TERMINATED);
    }

    @Override
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) throws AccessDeniedException {
        getAuthenticatedAdmin(); // authorization check
        return adminService.getAll(pageable);
    }

    private Employee getAuthenticatedAdmin() throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long adminId = Long.valueOf(auth.getName());

        return employeeRepository.findByIdAndRole(adminId, Role.ADMIN)
                .orElseThrow(() -> new AccessDeniedException("Admin access required"));
    }

    private void enforceActive(Employee admin) throws AccessDeniedException {
        if (admin.getStatus() != EmployeeStatus.ACTIVE) {
            throw new AccessDeniedException("Inactive admin cannot perform actions");
        }
    }
}
