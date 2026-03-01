package com.optimaNet.v2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record RegisterCustomerRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Pattern(regexp = "^[0-9]{10}$") String mobileNumber,
        @Past LocalDate dateOfBirth
) {
}
