package com.optimaNet.v2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimaNet.v2.dto.PanVerificationResponse;
import com.optimaNet.v2.exception.V2ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PanVerificationService {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${pan.verification.provider.url:}")
    private String providerUrl;

    @Value("${pan.verification.provider.api-key:}")
    private String providerApiKey;

    @Value("${pan.verification.provider.auth-header:Authorization}")
    private String authHeader;

    @Value("${pan.verification.provider.auth-prefix:Bearer }")
    private String authPrefix;

    public PanVerificationResponse verifyPan(String panNumber) {
        if (providerUrl == null || providerUrl.isBlank()) {
            throw new V2ValidationException("PAN provider is not configured. Set pan.verification.provider.url");
        }
        if (providerApiKey == null || providerApiKey.isBlank()) {
            throw new V2ValidationException("PAN provider API key is not configured.");
        }

        RestClient client = restClientBuilder.build();
        try {
            String rawResponse = client.post()
                    .uri(providerUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(authHeader, authPrefix + providerApiKey)
                    .body(Map.of("panNumber", panNumber))
                    .retrieve()
                    .body(String.class);

            String holderName = extractHolderName(rawResponse);
            if (holderName == null || holderName.isBlank()) {
                throw new V2ValidationException("PAN provider response does not include holder name.");
            }

            return new PanVerificationResponse(panNumber, holderName, "PAN_PROVIDER");
        } catch (RestClientException ex) {
            throw new V2ValidationException("PAN verification failed: " + ex.getMessage());
        }
    }

    private String extractHolderName(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String[] candidatePaths = {
                    "holderName",
                    "fullName",
                    "name",
                    "data.holderName",
                    "data.fullName",
                    "data.name",
                    "result.holderName",
                    "result.fullName",
                    "result.name"
            };

            for (String path : candidatePaths) {
                JsonNode current = root;
                for (String node : path.split("\\.")) {
                    current = current.path(node);
                }
                if (!current.isMissingNode() && !current.isNull() && current.isTextual()) {
                    return current.asText().trim();
                }
            }
            return null;
        } catch (Exception ex) {
            throw new V2ValidationException("Unable to parse PAN provider response.");
        }
    }
}
