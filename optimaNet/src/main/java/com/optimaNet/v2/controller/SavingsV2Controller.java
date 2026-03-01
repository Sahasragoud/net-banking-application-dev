package com.optimaNet.v2.controller;

import com.optimaNet.v2.dto.BalanceOperationRequest;
import com.optimaNet.v2.dto.OpenSavingsAccountRequest;
import com.optimaNet.v2.dto.SavingsAccountResponse;
import com.optimaNet.v2.service.CoreBankingV2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/savings")
@CrossOrigin(origins = "http://localhost:5173")
public class SavingsV2Controller {

    private final CoreBankingV2Service service;

    @PostMapping("/{customerId}/open")
    public ResponseEntity<SavingsAccountResponse> open(
            @PathVariable Long customerId,
            @Valid @RequestBody OpenSavingsAccountRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.openSavingsAccount(customerId, request));
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<SavingsAccountResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody BalanceOperationRequest request
    ) {
        return ResponseEntity.ok(service.deposit(accountNumber, request));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<SavingsAccountResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody BalanceOperationRequest request
    ) {
        return ResponseEntity.ok(service.withdraw(accountNumber, request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<SavingsAccountResponse> getByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getSavingsAccountDetails(accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SavingsAccountResponse>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getSavingsAccountsByCustomer(customerId));
    }
}
