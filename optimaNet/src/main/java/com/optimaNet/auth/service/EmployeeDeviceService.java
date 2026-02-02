package com.optimaNet.auth.service;

import com.optimaNet.auth.dto.DeviceInfo;
import com.optimaNet.auth.entity.EmployeeDevice;
import com.optimaNet.exception.DeviceBlockedException;
import com.optimaNet.exception.EmployeeNotFoundException;
import com.optimaNet.exception.UserDeviceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeDeviceService {
    EmployeeDevice registerOrUpdateDevice(
            Long employeeId,
            DeviceInfo deviceInfo,
            String ipAddress
    ) throws EmployeeNotFoundException, DeviceBlockedException;

    void markDeviceTrusted(Long deviceId) throws UserDeviceNotFoundException;

    void blockDevice(Long deviceId, String reason) throws UserDeviceNotFoundException;

    boolean isDeviceBlocked(Long deviceId) throws UserDeviceNotFoundException;

    Page<EmployeeDevice> getAllEmployeeDevices(Pageable pageable);

    Page<EmployeeDevice> getAllEmployeeDevicesByEmployeeId(Long employeeId, Pageable pageable) throws EmployeeNotFoundException;

}
