package com.optimaNet.auth.repository;

import com.optimaNet.auth.dto.EmployeeLoginSessionDto;
import com.optimaNet.auth.entity.EmployeeLoginSession;
import com.optimaNet.auth.entity.LoginSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeLoginSessionRepository extends JpaRepository<EmployeeLoginSession, Long> {

    Page<EmployeeLoginSession> findAllByEmployeeIdAndIsActiveTrue(Long employeeId, Pageable pageable);

    @Query("""
        select new com.optimaNet.auth.dto.EmployeeLoginSessionDto(
            s.id,
            s.employee.id,
            s.ip_address,
            s.loginAt,
            s.lastActivityAt,
            s.isActive,
            s.loginSessionStatus
        )
        from EmployeeLoginSession s
        """)
    Page<EmployeeLoginSessionDto> findAllProjected(Pageable pageable);

    Optional<EmployeeLoginSession> findByIdAndIsActiveTrue(Long sessionId);

    List<EmployeeLoginSession> findAllByEmployeeIdAndIsActiveTrue(Long employeeId);

}
