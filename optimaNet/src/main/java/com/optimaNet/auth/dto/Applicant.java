package com.optimaNet.auth.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Applicant {
    private Long userId;
    private String fullName;
    private String email;
    private String aadhaarNumber;
    private String mobileNumber;
}
