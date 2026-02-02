
package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.EmployeeRequest;
import com.optimaNet.auth.dto.EmployeeResponse;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.EmployeeQueryService;
import com.optimaNet.exception.DuplicateResourceException;
import com.optimaNet.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeQueryServiceImpl implements EmployeeQueryService {
    private final EmployeeRepository employeeRepository;

    @Override
    public Long createEmployee(EmployeeRequest request) throws DuplicateResourceException {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Employee employee = new Employee();
        employee.setStatus(EmployeeStatus.INACTIVE);
        employee.setEmail(request.getEmail());
        employee.setFullName(request.getFullName());
        employee.setMobileNumber(request.getMobileNumber());
        employee.setRole(request.getRole());

        employee.setEmployeeCode(generateEmployeeCode());

        employeeRepository.save(employee);

        return employee.getId();
    }

    @Override
    public void updateStatus(Long employeeId, EmployeeStatus employeeStatus) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId ));

        employee.setStatus(employeeStatus);
        employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeByIdAndRole(Long employeeId, Role role) throws EmployeeNotFoundException {
        return employeeRepository.findByIdAndRole(employeeId, role)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId + " for role" + role));
    }

    @Override
    public Page<EmployeeResponse> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getEmployeeCode(),
                        employee.getFullName(),
                        employee.getEmail(),
                        employee.getRole(),
                        employee.getStatus()
                ));
    }

    @Override
    public Employee getEmployeeByEmployeeCode(String employeeCode) throws EmployeeNotFoundException {
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with code " + employeeCode));
    }


    private String generateEmployeeCode(){
        long seq = employeeRepository.getNextEmployeeCodeSeq();
        return "OPT-EMP-" + Year.now().getValue() + "-" + String.format("%06d", seq);
    }


}
