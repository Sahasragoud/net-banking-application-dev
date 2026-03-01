package com.optimaNet.v2.controller;

import com.optimaNet.v2.dto.CustomerResponse;
import com.optimaNet.v2.dto.RegisterCustomerRequest;
import com.optimaNet.v2.service.CoreBankingV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountCreationV2Controller {

    private final CoreBankingV2Service service;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerCustomer(request));
    }
}
