package com.optimaNet.v2.service;

import com.optimaNet.v2.dto.*;
import com.optimaNet.v2.entity.*;
import com.optimaNet.v2.enums.CustomerStatus;
import com.optimaNet.v2.enums.KycDocumentType;
import com.optimaNet.v2.enums.KycStatus;
import com.optimaNet.v2.enums.SavingsAccountStatus;
import com.optimaNet.v2.enums.TransactionType;
import com.optimaNet.v2.exception.V2NotFoundException;
import com.optimaNet.v2.exception.V2ValidationException;
import com.optimaNet.v2.repository.CustomerV2Repository;
import com.optimaNet.v2.repository.CustomerProfileV2Repository;
import com.optimaNet.v2.repository.KycCaseV2Repository;
import com.optimaNet.v2.repository.NomineeV2Repository;
import com.optimaNet.v2.repository.SavingsAccountV2Repository;
import com.optimaNet.v2.repository.SavingsTransactionV2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Locale;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class CoreBankingV2Service {
    private static final BigDecimal LITE_TXN_LIMIT = new BigDecimal("5000.00");
    private static final String BANK_NAME = "OptimaNet Bank";
    private static final String BRANCH_NAME = "Digital Banking Branch";
    private static final String IFSC_CODE = "OPTI0001234";

    private final CustomerV2Repository customerRepository;
    private final CustomerProfileV2Repository customerProfileRepository;
    private final KycCaseV2Repository kycRepository;
    private final NomineeV2Repository nomineeRepository;
    private final SavingsAccountV2Repository savingsRepository;
    private final SavingsTransactionV2Repository transactionRepository;
    private final KycDocumentVerificationService kycDocumentVerificationService;
    private final TotpService totpService;

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
        customer.setCustomerCode(generateTemporaryCustomerCode());
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
        boolean hasAadhaar = request.aadhaarNumber() != null && !request.aadhaarNumber().isBlank();
        boolean hasVoterId = request.voterIdNumber() != null && !request.voterIdNumber().isBlank();
        if (hasAadhaar == hasVoterId) {
            throw new V2ValidationException("Provide exactly one identity number: Aadhaar or Voter ID");
        }

        kycCase.setAadhaarNumber(request.aadhaarNumber());
        kycCase.setVoterIdNumber(request.voterIdNumber());
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

        customer.setCustomerStatus(CustomerStatus.LITE);
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
        boolean eligibleStatus = customer.getCustomerStatus() == CustomerStatus.ACTIVE
                || customer.getCustomerStatus() == CustomerStatus.LITE;
        boolean eligibleKyc = kycCase.getKycStatus() == KycStatus.SUBMITTED
                || kycCase.getKycStatus() == KycStatus.APPROVED;
        if (!eligibleStatus || !eligibleKyc) {
            throw new V2ValidationException("Customer is not eligible for account opening");
        }
        // Idempotency safety: if an active savings account already exists, return it.
        SavingsAccountV2 existing = savingsRepository
                .findFirstByCustomer_IdAndAccountStatusOrderByCreatedAtDesc(customerId, SavingsAccountStatus.ACTIVE)
                .orElse(null);
        if (existing != null) {
            return toSavingsResponse(existing);
        }

        SavingsAccountV2 account = new SavingsAccountV2();
        account.setCustomer(customer);
        account.setAccountStatus(SavingsAccountStatus.ACTIVE);
        account.setAvailableBalance(request.initialDeposit());
        account.setInterestRate(request.interestRate());
        // account_number is NOT NULL in DB, so use a temporary value before first insert.
        account.setAccountNumber(generateTemporarySavingsAccountNumber());
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
        enforceTransactionPolicy(account, request, true);
        account.setAvailableBalance(account.getAvailableBalance().add(request.amount()));
        savingsRepository.save(account);
        String remarks = request.remarks();
        if (remarks == null || remarks.isBlank()) {
            remarks = "External deposit";
        }
        writeTransaction(account, TransactionType.DEPOSIT, request.amount(), remarks);
        return toSavingsResponse(account);
    }

    public SavingsAccountResponse withdraw(String accountNumber, BalanceOperationRequest request) {
        SavingsAccountV2 account = getSavingsAccount(accountNumber);
        ensureAccountActive(account);
        enforceTransactionPolicy(account, request, false);
        if (account.getAvailableBalance().compareTo(request.amount()) < 0) {
            throw new V2ValidationException("Insufficient balance");
        }
        account.setAvailableBalance(account.getAvailableBalance().subtract(request.amount()));
        savingsRepository.save(account);
        writeTransaction(account, TransactionType.WITHDRAWAL, request.amount(), request.remarks());
        return toSavingsResponse(account);
    }

    public CustomerResponse setMpin(Long customerId, String mpin) {
        CustomerV2 customer = getCustomer(customerId);
        customer.setMpinHash(hashMpin(mpin));
        customer = customerRepository.save(customer);
        return toCustomerResponse(customer);
    }

    public CustomerProfileResponse upsertProfile(Long customerId, CustomerProfileRequest request) {
        CustomerV2 customer = getCustomer(customerId);
        CustomerProfileV2 profile = customerProfileRepository.findByCustomer_Id(customerId)
                .orElseGet(() -> {
                    CustomerProfileV2 created = new CustomerProfileV2();
                    created.setCustomer(customer);
                    return created;
                });
        profile.setOccupation(request.occupation().trim());
        profile.setIncomeSource(request.incomeSource().trim());
        profile.setYearlyIncome(request.yearlyIncome().trim());
        profile.setMaritalStatus(request.maritalStatus().trim());
        profile.setFatherName(request.fatherName().trim());
        profile.setMotherMaidenName(request.motherMaidenName().trim());
        profile = customerProfileRepository.save(profile);
        return toProfileResponse(profile);
    }

    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(Long customerId) {
        CustomerProfileV2 profile = customerProfileRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new V2NotFoundException("Profile not found for customer id: " + customerId));
        return toProfileResponse(profile);
    }

    public List<NomineeResponse> replaceNominees(Long customerId, List<NomineeRequest> nominees) {
        if (nominees == null || nominees.isEmpty()) {
            throw new V2ValidationException("At least one nominee is required");
        }
        CustomerV2 customer = getCustomer(customerId);
        nomineeRepository.deleteByCustomer_Id(customerId);
        List<NomineeV2> rows = nominees.stream().map(request -> {
            if (request.ageYears() < 18 &&
                    ((request.guardianName() == null || request.guardianName().isBlank())
                            || (request.guardianRelationship() == null || request.guardianRelationship().isBlank()))) {
                throw new V2ValidationException("Guardian details are required for minor nominee");
            }
            NomineeV2 nominee = new NomineeV2();
            nominee.setCustomer(customer);
            nominee.setDepositorName(blankToNull(request.depositorName()));
            nominee.setDepositorAddress(blankToNull(request.depositorAddress()));
            nominee.setNomineeName(request.nomineeName().trim());
            nominee.setNomineeAddress(blankToNull(request.nomineeAddress()));
            nominee.setRelationship(blankToNull(request.relationship()));
            nominee.setAgeYears(request.ageYears());
            nominee.setGuardianName(blankToNull(request.guardianName()));
            nominee.setGuardianRelationship(blankToNull(request.guardianRelationship()));
            return nominee;
        }).toList();

        return nomineeRepository.saveAll(rows).stream().map(this::toNomineeResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NomineeResponse> getNominees(Long customerId) {
        getCustomer(customerId);
        return nomineeRepository.findByCustomer_IdOrderByIdAsc(customerId).stream()
                .map(this::toNomineeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public V2LoginResponse loginV2(V2LoginRequest request) {
        CustomerV2 customer = customerRepository.findByCustomerCode(request.customerCode())
                .orElseThrow(() -> new V2ValidationException("Invalid customer code or MPIN"));

        if (customer.getCustomerStatus() == CustomerStatus.BLOCKED || customer.getCustomerStatus() == CustomerStatus.REJECTED) {
            throw new V2ValidationException("Customer is not allowed to login");
        }
        if (customer.getMpinHash() == null || customer.getMpinHash().isBlank()) {
            throw new V2ValidationException("MPIN is not set for this customer");
        }
        if (!customer.getMpinHash().equals(hashMpin(request.mpin()))) {
            throw new V2ValidationException("Invalid customer code or MPIN");
        }
        if (Boolean.TRUE.equals(customer.getMfaEnabled())) {
            if (request.mfaCode() == null || request.mfaCode().isBlank()) {
                return new V2LoginResponse(false, true, customer.getId(), customer.getCustomerCode(), customer.getFullName(), customer.getCustomerStatus());
            }
            if (!verifyMfa(customer.getId(), request.mfaCode())) {
                throw new V2ValidationException("Invalid MFA code");
            }
        }
        return new V2LoginResponse(true, false, customer.getId(), customer.getCustomerCode(), customer.getFullName(), customer.getCustomerStatus());
    }

    public MfaSetupResponse setupMfa(Long customerId) {
        CustomerV2 customer = getCustomer(customerId);
        String secret = totpService.generateSecret();
        customer.setMfaSecret(secret);
        customer.setMfaEnabled(Boolean.FALSE);
        customerRepository.save(customer);
        String otpauth = totpService.buildOtpAuthUrl("OptimaNet Bank", customer.getCustomerCode(), secret);
        return new MfaSetupResponse(customerId, secret, otpauth);
    }

    public CustomerResponse enableMfa(Long customerId, String code) {
        CustomerV2 customer = getCustomer(customerId);
        if (customer.getMfaSecret() == null || customer.getMfaSecret().isBlank()) {
            throw new V2ValidationException("MFA setup not initialized");
        }
        if (!totpService.verifyCode(customer.getMfaSecret(), code)) {
            throw new V2ValidationException("Invalid MFA code");
        }
        customer.setMfaEnabled(Boolean.TRUE);
        customer = customerRepository.save(customer);
        return toCustomerResponse(customer);
    }

    public boolean verifyMfa(Long customerId, String code) {
        CustomerV2 customer = getCustomer(customerId);
        if (!Boolean.TRUE.equals(customer.getMfaEnabled())) {
            return true;
        }
        if (customer.getMfaSecret() == null || customer.getMfaSecret().isBlank()) {
            throw new V2ValidationException("MFA secret is not configured");
        }
        return totpService.verifyCode(customer.getMfaSecret(), code);
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
    public CustomerResponse getCustomerByCode(String customerCode) {
        CustomerV2 customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new V2NotFoundException("Customer not found for code: " + customerCode));
        return toCustomerResponse(customer);
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

    @Transactional(readOnly = true)
    public PanVerificationResponse verifyPan(String panNumber) {
        return kycDocumentVerificationService.verifyPan(panNumber);
    }

    @Transactional(readOnly = true)
    public KycDocumentVerificationResponse verifyKycDocument(KycDocumentType documentType, String documentNumber) {
        return kycDocumentVerificationService.verifyDocument(documentType, documentNumber);
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

    private void enforceTransactionPolicy(SavingsAccountV2 account, BalanceOperationRequest request, boolean deposit) {
        CustomerV2 customer = account.getCustomer();
        if (customer.getCustomerStatus() != CustomerStatus.ACTIVE && customer.getCustomerStatus() != CustomerStatus.LITE) {
            throw new V2ValidationException("Customer is not permitted to transact");
        }
        if (request.mpin() == null || request.mpin().isBlank()) {
            throw new V2ValidationException("MPIN is required to authorize transaction");
        }
        if (customer.getMpinHash() == null || customer.getMpinHash().isBlank()) {
            throw new V2ValidationException("Set MPIN before making transactions");
        }
        if (!customer.getMpinHash().equals(hashMpin(request.mpin()))) {
            throw new V2ValidationException("Invalid MPIN");
        }
        if (customer.getCustomerStatus() == CustomerStatus.LITE
                && request.amount().compareTo(LITE_TXN_LIMIT) > 0) {
            throw new V2ValidationException("Lite customers can transact up to INR 5000 per transaction");
        }
        if (deposit) {
            if (request.sourceReference() == null || request.sourceReference().isBlank()) {
                throw new V2ValidationException("Source reference is required for deposit");
            }
            if (request.sourceReference().trim().equalsIgnoreCase(account.getAccountNumber())) {
                throw new V2ValidationException("Self deposit from the same account is not allowed");
            }
        }
    }

    private String formatCustomerCode(Long id) {
        return "OPT-CUST-" + Year.now().getValue() + "-" + String.format("%06d", id);
    }

    private String generateTemporaryCustomerCode() {
        long random = ThreadLocalRandom.current().nextLong(100000, 999999);
        return "TMP-" + Year.now().getValue() + "-" + random;
    }

    private String formatSavingsAccountNumber(Long id) {
        return "SAV" + Year.now().getValue() + String.format("%08d", id);
    }

    private String generateTemporarySavingsAccountNumber() {
        long random = Math.abs(ThreadLocalRandom.current().nextLong());
        String suffix = Long.toString(random, 36).toUpperCase(Locale.ROOT);
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }
        return "TMP-SAV-" + suffix;
    }

    private String hashMpin(String mpin) {
        return DigestUtils.sha256Hex(mpin);
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
                Boolean.TRUE.equals(customer.getMfaEnabled()),
                customer.getCreatedAt()
        );
    }

    private CustomerProfileResponse toProfileResponse(CustomerProfileV2 profile) {
        return new CustomerProfileResponse(
                profile.getCustomer().getId(),
                profile.getOccupation(),
                profile.getIncomeSource(),
                profile.getYearlyIncome(),
                profile.getMaritalStatus(),
                profile.getFatherName(),
                profile.getMotherMaidenName()
        );
    }

    private NomineeResponse toNomineeResponse(NomineeV2 nominee) {
        return new NomineeResponse(
                nominee.getId(),
                nominee.getCustomer().getId(),
                nominee.getDepositorName(),
                nominee.getDepositorAddress(),
                nominee.getNomineeName(),
                nominee.getNomineeAddress(),
                nominee.getRelationship(),
                nominee.getAgeYears(),
                nominee.getGuardianName(),
                nominee.getGuardianRelationship()
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private KycCaseResponse toKycResponse(KycCaseV2 kycCase) {
        return new KycCaseResponse(
                kycCase.getId(),
                kycCase.getCustomer().getId(),
                kycCase.getKycStatus(),
                maskAadhaar(kycCase.getAadhaarNumber()),
                maskVoter(kycCase.getVoterIdNumber()),
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
                account.getCustomer().getCustomerCode(),
                account.getCustomer().getFullName(),
                account.getCustomer().getCustomerStatus().name(),
                BANK_NAME,
                BRANCH_NAME,
                IFSC_CODE,
                buildUpiHandle(account.getCustomer().getMobileNumber()),
                account.getAccountStatus(),
                account.getAvailableBalance(),
                account.getInterestRate(),
                account.getCreatedAt(),
                transactions
        );
    }

    private String buildUpiHandle(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return "pending@optima";
        }
        return mobileNumber + "@optima";
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

    private String maskVoter(String voterId) {
        if (voterId == null || voterId.length() < 3) {
            return voterId;
        }
        return "XXX" + voterId.substring(voterId.length() - 3);
    }
}
