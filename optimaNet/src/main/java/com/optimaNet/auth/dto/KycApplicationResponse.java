package com.optimaNet.auth.dto;

import com.optimaNet.auth.enums.KYCStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class KycApplicationResponse {

    private Long id;
    private KYCStatus status;
    private Long employeeId;
    private Long userId;
    private LocalDateTime assignedAt;
}
