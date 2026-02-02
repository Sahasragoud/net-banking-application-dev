package com.optimaNet.auth.controller;

import com.optimaNet.auth.entity.UserAuthToken;
import com.optimaNet.auth.security.JwtUserPrincipal;
import com.optimaNet.auth.service.AuthTokenService;
import com.optimaNet.exception.TokenNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthTokenService authTokenService;

    public AuthController(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) throws TokenNotFoundException {
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();

        authTokenService.logout(
                principal.getUserId(), principal.getDeviceId()
        );

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/authTokens")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserAuthToken> getAuthTokens(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return authTokenService.getAllAuthTokens(PageRequest.of(page, size, sortBy));

    }


    }
