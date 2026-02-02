package com.optimaNet.auth.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentUrlService {

    public String generateOfficerViewUrl(Long documentId) {
        return "/api/kyc-officer/documents/" + documentId + "/download";
    }
}

