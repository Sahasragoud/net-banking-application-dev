package com.optimaNet.v2.service;

import com.optimaNet.v2.dto.*;
import com.optimaNet.v2.entity.*;
import com.optimaNet.v2.enums.CustomerStatus;
import com.optimaNet.v2.enums.KycStatus;
import com.optimaNet.v2.enums.SavingsAccountStatus;
import com.optimaNet.v2.enums.TransactionType;
import com.optimaNet.v2.exception.V2NotFoundException;
import com.optimaNet.v2.exception.V2ValidationException;
import com.optimaNet.v2.repository.CustomerV2Repository;
import com.optimaNet.v2.repository.KycCaseV2Repository;
import com.optimaNet.v2.repository.SavingsAccountV2Repository;
import com.optimaNet.v2.repository.SavingsTransactionV2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreBankingV2Service {

    private final CustomerV2Repository customerRepository;
    private final KycCaseV2Repository kycRepository;
    private final SavingsAccountV2Repository savingsRepository;
    private final SavingsTransactionV2Repository transactionRepository;

    public CustomerResponse registerCustomer(RegisterCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new V2ValidationException("Email already registered");
        }
        if (customerRepository.existsByMobileNumber(request.mobileNumber())) {
            throw new V2ValidationException("Mobile number already registered");
        }

        CustomerV2 customer = new CustomerV2();
        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setMobileNumber(request.mobileNumber());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setCustomerStatus(CustomerStatus.PENDING_KYC);
        customer = customerRepository.save(customer);

        customer.setCustomerCode(formatCustomerCode(customer.getId()));
        customer = customerRepository.save(customer);

        KycCaseV2 kycCase = new KycCaseV2();
        kycCase.setCustomer(customer);
        kycCase.setKycStatus(KycStatus.NOT_STARTED);
        kycRepository.save(kycCase);

        return toCustomerResponse(customer);
    }

    public KycCaseResponse submitKyc(Long customerId, SubmitKycRequest request) {
        CustomerV2 customer = getCustomer(customerId);
        KycCaseV2 kycCase = getKycCase(customerId);
        if (kycCase.getKycStatus() == KycStatus.APPROVED) {
            throw new V2ValidationException("KYC already approved");
        }

        kycCase.setAadhaarNumber(request.aadhaarNumber());
        kycCase.setPanNumber(request.panNumber());
        kycCase.setAddressLine(request.addressLine());
        kycCase.setCity(request.city());
        kycCase.setState(request.state());
        kycCase.setPostalCode(request.postalCode());
        kycCase.setCountry(request.country());
        kycCase.setRejectionReason(null);
        kycCase.setKycStatus(KycStatus.SUBMITTED);
        kycCase.setSubmittedAt(LocalDateTime.now());
        kycRepository.save(kycCase);

        customer.setCustomerStatus(CustomerStatus.PENDING_KYC);
        customerRepository.save(customer);
        return toKycResponse(kycCase);
    }

    public KycCaseResponse approveKyc(Long customerId) {
        CustomerV2 customer = getCustomer(customerId);
        KycCaseV2 kycCase = getKycCase(customerId);
        if (kycCase.getKycStatus() != KycStatus.SUBMITTED) {
            throw new V2ValidationException("KYC must be SUBMITTED before approval");
        }

        kycCase.setKycStatus(KycStatus.APPROVED);
        kycCase.setReviewedAt(LocalDateTime.now());
        kycCase.setRejectionReason(null);
        kycRepository.save(kycCase);

        customer.setCustomerStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
        return toKycResponse(kycCase);
    }

    public KycCaseResponse rejectKyc(Long customerId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new V2ValidationException("Rejection reason is required");
        }
        CustomerV2 customer = getCustomer(customerId);
        KycCaseV2 kycCase = getKycCase(customerId);
        if (kycCase.getKycStatus() != KycStatus.SUBMITTED) {
            throw new V2ValidationException("KYC must be SUBMITTED before rejection");
        }

        kycCase.setKycStatus(KycStatus.REJECTED);
        kycCase.setReviewedAt(LocalDateTime.now());
        kycCase.setRejectionReason(reason);
        kycRepository.save(kycCase);

        customer.setCustomerStatus(CustomerStatus.REJECTED);
        customerRepository.save(customer);
        return toKycResponse(kycCase);
    }

    public SavingsAccountResponse openSavingsAccount(Long customerId, OpenSavingsAccountRequest request) {
        CustomerV2 customer = getCustomer(customerId);
        KycCaseV2 kycCase = getKycCase(customerId);
        if (customer.getCustomerStatus() != CustomerStatus.ACTIVE || kycCase.getKycStatus() != KycStatus.APPROVED) {
            throw new V2ValidationException("Customer is not eligible for account opening");
        }

        SavingsAccountV2 account = new SavingsAccountV2();
        account.setCustomer(customer);
        account.setAccountStatus(SavingsAccountStatus.ACTIVE);
        account.setAvailableBalance(request.initialDeposit());
        account.setInterestRate(request.interestRate());
        account = savingsRepository.save(account);

        account.setAccountNumber(formatSavingsAccountNumber(account.getId()));
        account = savingsRepository.save(account);

        if (request.initialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            writeTransaction(account, TransactionType.DEPOSIT, request.initialDeposit(), "Initial deposit");
        }
        return toSavingsResponse(account);
    }

    public SavingsAccountResponse deposit(String accountNumber, BalanceOperationRequest request) {
        SavingsAccountV2 account = getSavingsAccount(accountNumber);
        ensureAccountActive(account);
        account.setAvailableBalance(account.getAvailableBalance().add(request.amount()));
        savingsRepository.save(account);
        writeTransaction(account, TransactionType.DEPOSIT, request.amount(), request.remarks());
        return toSavingsResponse(account);
    }

    public SavingsAccountResponse withdraw(String accountNumber, BalanceOperationRequest request) {
        SavingsAccountV2 account = getSavingsAccount(accountNumber);
        ensureAccountActive(account);
        if (account.getAvailableBalance().compareTo(request.amount()) < 0) {
            throw new V2ValidationException("Insufficient balance");
        }
        account.setAvailableBalance(account.getAvailableBalance().subtract(request.amount()));
        savingsRepository.save(account);
        writeTransaction(account, TransactionType.WITHDRAWAL, request.amount(), request.remarks());
        return toSavingsResponse(account);
    }

    @Transactional(readOnly = true)
    public SavingsAccountResponse getSavingsAccountDetails(String accountNumber) {
        return toSavingsResponse(getSavingsAccount(accountNumber));
    }

    @Transactional(readOnly = true)
    public List<SavingsAccountResponse> getSavingsAccountsByCustomer(Long customerId) {
        return savingsRepository.findByCustomer_Id(customerId)
                .stream()
                .map(this::toSavingsResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        return toCustomerResponse(getCustomer(customerId));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> listCustomers(CustomerStatus status) {
        List<CustomerV2> customers = status == null
                ? customerRepository.findAll()
                : customerRepository.findByCustomerStatus(status);
        return customers.stream().map(this::toCustomerResponse).toList();
    }

    public CustomerResponse updateCustomerStatus(Long customerId, CustomerStatus status) {
        CustomerV2 customer = getCustomer(customerId);
        customer.setCustomerStatus(status);
        customer = customerRepository.save(customer);
        return toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public KycCaseResponse getKycCaseDetails(Long customerId) {
        return toKycResponse(getKycCase(customerId));
    }

    private void writeTransaction(SavingsAccountV2 account, TransactionType type, BigDecimal amount, String remarks) {
        SavingsTransactionV2 transaction = new SavingsTransactionV2();
        transaction.setSavingsAccount(account);
        transaction.setTxnType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfterTxn(account.getAvailableBalance());
        transaction.setRemarks(remarks);
        transactionRepository.save(transaction);
    }

    private CustomerV2 getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new V2NotFoundException("Customer not found for id: " + customerId));
    }

    private KycCaseV2 getKycCase(Long customerId) {
        return kycRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new V2NotFoundException("KYC case not found for customer id: " + customerId));
    }

    private SavingsAccountV2 getSavingsAccount(String accountNumber) {
        return savingsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new V2NotFoundException("Savings account not found for account number: " + accountNumber));
    }

    private void ensureAccountActive(SavingsAccountV2 account) {
        if (account.getAccountStatus() != SavingsAccountStatus.ACTIVE) {
            throw new V2ValidationException("Savings account is not active");
        }
    }

    private String formatCustomerCode(Long id) {
        return "OPT-CUST-" + Year.now().getValue() + "-" + String.format("%06d", id);
    }

    private String formatSavingsAccountNumber(Long id) {
        return "SAV" + Year.now().getValue() + String.format("%08d", id);
    }

    private CustomerResponse toCustomerResponse(CustomerV2 customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getMobileNumber(),
                customer.getDateOfBirth(),
                customer.getCustomerStatus(),
                customer.getCreatedAt()
        );
    }

    private KycCaseResponse toKycResponse(KycCaseV2 kycCase) {
        return new KycCaseResponse(
                kycCase.getId(),
                kycCase.getCustomer().getId(),
                kycCase.getKycStatus(),
                maskAadhaar(kycCase.getAadhaarNumber()),
                maskPan(kycCase.getPanNumber()),
                kycCase.getRejectionReason(),
                kycCase.getSubmittedAt(),
                kycCase.getReviewedAt()
        );
    }

    private SavingsAccountResponse toSavingsResponse(SavingsAccountV2 account) {
        List<SavingsTransactionResponse> transactions = transactionRepository
                .findTop20BySavingsAccountAccountNumberOrderByCreatedAtDesc(account.getAccountNumber())
                .stream()
                .map(txn -> new SavingsTransactionResponse(
                        txn.getId(),
                        txn.getTxnType(),
                        txn.getAmount(),
                        txn.getBalanceAfterTxn(),
                        txn.getRemarks(),
                        txn.getCreatedAt()
                )).toList();

        return new SavingsAccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomer().getId(),
                account.getAccountStatus(),
                account.getAvailableBalance(),
                account.getInterestRate(),
                account.getCreatedAt(),
                transactions
        );
    }

    private String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) {
            return aadhaar;
        }
        return "XXXXXXXX" + aadhaar.substring(aadhaar.length() - 4);
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4) {
            return pan;
        }
        return "XXXXXX" + pan.substring(pan.length() - 4);
    }
}
