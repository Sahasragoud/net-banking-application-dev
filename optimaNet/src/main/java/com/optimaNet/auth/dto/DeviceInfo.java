package com.optimaNet.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {
    private String deviceId;
    private String deviceFingerprint;
    private String deviceType;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
}

