package com.optimaNet.auth.service;


import com.optimaNet.auth.dto.DeviceInfo;
import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.exception.DeviceBlockedException;
import com.optimaNet.exception.UserDeviceNotFoundException;
import com.optimaNet.exception.UserNotFoundException;
import org.springframework.data.domain.*;

public interface UserDeviceService {

    UserDevice registerOrUpdateDevice(
            Long userId,
            DeviceInfo deviceInfo,
            String ipAddress
    ) throws UserNotFoundException, UserDeviceNotFoundException, DeviceBlockedException;

    void markDeviceTrusted(Long deviceId) throws UserDeviceNotFoundException;

    void blockDevice(Long deviceId, String reason) throws UserDeviceNotFoundException;

    boolean isDeviceBlocked(Long deviceId) throws UserDeviceNotFoundException;

    Page<UserDevice> getAllUserDevices(Pageable pageable);

    Page<UserDevice> getAllUserDevicesByUserId(Long userId, Pageable pageable) throws UserNotFoundException;

}
