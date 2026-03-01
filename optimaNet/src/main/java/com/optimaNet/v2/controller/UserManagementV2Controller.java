package com.optimaNet.v2.controller;

import com.optimaNet.v2.dto.CustomerResponse;
import com.optimaNet.v2.enums.CustomerStatus;
import com.optimaNet.v2.service.CoreBankingV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserManagementV2Controller {

    private final CoreBankingV2Service service;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list(
            @RequestParam(required = false) CustomerStatus status
    ) {
        return ResponseEntity.ok(service.listCustomers(status));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getCustomerById(customerId));
    }

    @PutMapping("/{customerId}/status/{status}")
    public ResponseEntity<CustomerResponse> updateStatus(
            @PathVariable Long customerId,
            @PathVariable CustomerStatus status
    ) {
        return ResponseEntity.ok(service.updateCustomerStatus(customerId, status));
    }
}
