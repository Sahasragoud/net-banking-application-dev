package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.enums.EmployeeStatus;
import com.optimaNet.auth.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmailAndRole(String email, Role role);

    boolean existsByEmail(String email);

    Optional<Employee> findByIdAndRole(Long empId, Role role);

    @Query(value = "SELECT nextval('employee_code_seq')", nativeQuery = true)
    long getNextEmployeeCodeSeq();

     boolean existsByIdAndStatus(Long empId, EmployeeStatus status);

     Optional<Employee> findByEmployeeCode(String employeeCode);
}
