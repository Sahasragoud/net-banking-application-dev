package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.KYCStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class UnassignedApplicationResponse {
    private Long applicationId;
    private KYCStatus status;
    private Long userId;
    private LocalDateTime createdAt;
}
