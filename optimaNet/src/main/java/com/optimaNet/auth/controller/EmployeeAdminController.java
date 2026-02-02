package com.optimaNet.auth.controller;

import com.optimaNet.auth.dto.EmployeeRequest;
import com.optimaNet.auth.dto.EmployeeResponse;
import com.optimaNet.auth.service.EmployeeActionOrchestratorService;
import com.optimaNet.exception.AccessDeniedException;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/employees")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeAdminController {

    private final EmployeeActionOrchestratorService orchestratorService;

    @PostMapping("/create-employee")
    public ResponseEntity<Map<String,Long>> createEmployee(
            @RequestBody EmployeeRequest request
    ) throws DuplicateResourceException, EmployeeNotFoundException, AccessDeniedException {

        Long employeeId = orchestratorService.createEmployee(request);
        return ResponseEntity.accepted().body(Map.of("employeeId", employeeId));
    }

    @PostMapping("/{empId}/activate")
    public ResponseEntity<String> activateEmployee(
            @PathVariable Long empId
    ) throws  EmployeeNotFoundException, AccessDeniedException {
        orchestratorService.activateEmployee( empId);
        return ResponseEntity.ok("Employee has been activated by successfully: " );
    }

    @PostMapping("/{empId}/suspend")
    public ResponseEntity<String> suspendEmployee(
            @PathVariable Long empId
    ) throws  EmployeeNotFoundException, AccessDeniedException {
        orchestratorService.suspendEmployee(empId);
        return ResponseEntity.ok("Employee has been suspended successfully: " );
    }

    @PostMapping("/{empId}/terminate")
    public ResponseEntity<String> terminateEmployee(
            @PathVariable Long empId
    ) throws  EmployeeNotFoundException, AccessDeniedException {
        orchestratorService.terminateEmployee(empId);
        return ResponseEntity.ok("Employee has been terminated successfully" );
    }

    @GetMapping
    public Page<EmployeeResponse> getAllEmployees(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ) throws AccessDeniedException {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sortBy = Sort.by(direction, sortField);
        return orchestratorService.getAllEmployees(
                PageRequest.of(page, size, sortBy)
        );
    }
}
