package com.optimaNet.exception;

import com.optimaNet.auth.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       OTP & REGISTRATION FLOW
       ========================= */

    @ExceptionHandler(OTPInvalidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOTP(OTPInvalidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // ❗ Correct semantic
                .body(new ErrorResponse("INVALID_OTP", ex.getMessage()));
    }

    @ExceptionHandler(OTPExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpiredOTP(OTPExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(new ErrorResponse("OTP_EXPIRED", ex.getMessage()));
    }

    @ExceptionHandler(OTPBlockedException.class)
    public ResponseEntity<ErrorResponse> handleBlockedOTP(OTPBlockedException ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse("OTP_BLOCKED", ex.getMessage()));
    }

    /* =========================
       USER / IDENTITY
       ========================= */

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(UserIdentityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserIdentityNotFound(UserIdentityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_IDENTITY_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(UserDeviceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserDeviceNotFound(UserDeviceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_DEVICE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(EmployeeDeviceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeDeviceNotFound(EmployeeDeviceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("EMPLOYEE_DEVICE_NOT_FOUND", ex.getMessage()));
    }

    /* =========================
       SESSION / TOKEN
       ========================= */

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("SESSION_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenNotFound(TokenNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("TOKEN_NOT_FOUND", ex.getMessage()));
    }

    /* =========================
       KYC / DOCUMENTS
       ========================= */

    @ExceptionHandler(InvalidKYCDetailsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidKYC(InvalidKYCDetailsException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_KYC", ex.getMessage()));
    }

    @ExceptionHandler(KycApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKycApplicationNotFound(KycApplicationNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("KYC_APPLICATION_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(KycDocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKycDocumentNotFound(KycDocumentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("KYC_DOCUMENT_NOT_FOUND", ex.getMessage()));
    }

    /* =========================
       EMPLOYEE / ADMIN
       ========================= */

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("EMPLOYEE_NOT_FOUND", ex.getMessage()));
    }

    /* =========================
       SECURITY
       ========================= */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // ❗ Fixed
                .body(new ErrorResponse("ACCESS_DENIED", ex.getMessage()));
    }

    @ExceptionHandler(DeviceBlockedException.class)
    public ResponseEntity<ErrorResponse> handleDeviceBlocked(DeviceBlockedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("DEVICE_BLOCKED", ex.getMessage()));
    }

    /* =========================
       VALIDATION / CONFLICTS
       ========================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", "Invalid input format"));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("RESOURCE_ALREADY_EXISTS", ex.getMessage()));
    }

    /* =========================
       FINAL SAFETY NET (MANDATORY)
       ========================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred"
                ));
    }
}
