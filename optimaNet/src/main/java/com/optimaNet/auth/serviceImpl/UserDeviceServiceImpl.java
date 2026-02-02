package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.dto.DeviceInfo;
import com.optimaNet.auth.entity.User;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.auth.repository.UserDeviceRepository;
import com.optimaNet.auth.service.UserDeviceService;
import com.optimaNet.auth.service.UserService;
import com.optimaNet.exception.DeviceBlockedException;
import com.optimaNet.exception.UserDeviceNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserService userService;
    private final UserDeviceRepository userDeviceRepository;

    public UserDeviceServiceImpl(UserService userService, UserDeviceRepository userDeviceRepository) {
        this.userService = userService;
        this.userDeviceRepository = userDeviceRepository;
    }

    @Override
    public UserDevice registerOrUpdateDevice(Long userId, DeviceInfo deviceInfo, String ipAddress) throws UserNotFoundException, DeviceBlockedException {
        User user = userService.getUserById(userId);

        UserDevice device = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceInfo.getDeviceId()) ;

        if(device == null){
            // register new device
            device = new UserDevice();
            device.setUser(user);
            device.setDeviceId(deviceInfo.getDeviceId());
            device.setDeviceFingerprint(deviceInfo.getDeviceFingerprint());
            device.setDeviceType(deviceInfo.getDeviceType());
            device.setDeviceModel(deviceInfo.getDeviceModel());
            device.setOsVersion(deviceInfo.getOsVersion());
            device.setAppVersion(deviceInfo.getAppVersion());
            device.setFirstSeenIp(ipAddress);
            device.setLastSeenIp(ipAddress);
            device.setTrusted(true);
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
        return userDeviceRepository.save(device);
    }

    @Override
    public void markDeviceTrusted(Long deviceId) throws UserDeviceNotFoundException {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id" + deviceId));

        device.setTrusted(true);
        userDeviceRepository.save(device);
    }

    @Override
    public void blockDevice(Long deviceId, String reason) throws UserDeviceNotFoundException {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id" + deviceId));

        device.setBlocked(true);
        device.setBlockedReason(reason);

        userDeviceRepository.save(device);
    }

    @Override
    public boolean isDeviceBlocked(Long deviceId) throws UserDeviceNotFoundException {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new UserDeviceNotFoundException("Device Not found with id: " + deviceId));
        return device.isBlocked();
    }

    @Override
    public Page<UserDevice> getAllUserDevices(Pageable pageable) {
        return userDeviceRepository.findAll(pageable);
    }

    @Override
    public Page<UserDevice> getAllUserDevicesByUserId(Long userId, Pageable pageable) throws UserNotFoundException {
        userService.getUserById(userId);
        return userDeviceRepository.findAllByUserId(userId, pageable);
    }
}
