package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.DeviceInfo;
import com.optimaNet.auth.entity.Employee;
import com.optimaNet.auth.entity.EmployeeDevice;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.auth.repository.EmployeeDeviceRepository;
import com.optimaNet.auth.repository.EmployeeRepository;
import com.optimaNet.auth.service.EmployeeDeviceService;
import com.optimaNet.auth.service.EmployeeQueryService;
import com.optimaNet.exception.DeviceBlockedException;
import com.optimaNet.exception.EmployeeNotFoundException;
import com.optimaNet.exception.UserDeviceNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeDeviceServiceImpl implements EmployeeDeviceService {
    private final EmployeeDeviceRepository deviceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDevice registerOrUpdateDevice(Long employeeId, DeviceInfo deviceInfo, String ipAddress) throws EmployeeNotFoundException, DeviceBlockedException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with code: " + employeeId)
                );

        EmployeeDevice device = deviceRepository.findByEmployeeIdAndDeviceId(employeeId, deviceInfo.getDeviceId()) ;

        if(device == null){
            // register new device
            device = new EmployeeDevice();
            device.setEmployee(employee);
            device.setDeviceId(deviceInfo.getDeviceId());
            device.setDeviceFingerprint(deviceInfo.getDeviceFingerprint());
            device.setDeviceType(deviceInfo.getDeviceType());
            device.setDeviceModel(deviceInfo.getDeviceModel());
            device.setOsVersion(deviceInfo.getOsVersion());
            device.setAppVersion(deviceInfo.getAppVersion());
            device.setFirstSeenIp(ipAddress);
            device.setLastSeenIp(ipAddress);
            device.setTrusted(false);
        }
        else{

            if(device.isBlocked()){
                throw new DeviceBlockedException("Device is Blocked");
            }
            // update device
            if(deviceInfo.getDeviceFingerprint() != null) device.setDeviceFingerprint(deviceInfo.getDeviceFingerprint());
            device.setLastSeenIp(ipAddress);
            if(deviceInfo.getDeviceModel() != null) device.setDeviceModel(deviceInfo.getDeviceModel());
            if(deviceInfo.getAppVersion() != null) device.setAppVersion(deviceInfo.getAppVersion());
            if(deviceInfo.getOsVersion() != null) device.setOsVersion(deviceInfo.getOsVersion());
        }
        return deviceRepository.save(device);
    }

    @Override
    public void markDeviceTrusted(Long deviceId) throws UserDeviceNotFoundException {
        EmployeeDevice device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id" + deviceId));

        device.setTrusted(true);
        deviceRepository.save(device);
    }

    @Override
    public void blockDevice(Long deviceId, String reason) throws UserDeviceNotFoundException {
        EmployeeDevice device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id" + deviceId));

        device.setBlocked(true);
        device.setBlockedReason(reason);

        deviceRepository.save(device);
    }

    @Override
    public boolean isDeviceBlocked(Long deviceId) throws UserDeviceNotFoundException {
        EmployeeDevice device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id: " + deviceId));
        return device.isBlocked();
    }

    @Override
    public Page<EmployeeDevice> getAllEmployeeDevices(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    @Override
    public Page<EmployeeDevice> getAllEmployeeDevicesByEmployeeId(Long employeeId, Pageable pageable) throws EmployeeNotFoundException {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with code: " + employeeId)
                );
        return deviceRepository.findAllByEmployeeId(employeeId, pageable);
    }
}
