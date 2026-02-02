package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.EmployeeDevice;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDeviceRepository extends JpaRepository<EmployeeDevice,Long> {
    EmployeeDevice findByEmployeeIdAndDeviceId(Long employeeId, String deviceId);

    Optional<EmployeeDevice> findById(Long deviceId);

    Page<EmployeeDevice> findAll(Pageable pageable);

    Page<EmployeeDevice> findAllByEmployeeId(Long employeeId, Pageable pageable);
}
