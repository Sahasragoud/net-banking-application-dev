package com.optimaNet.auth.serviceImpl;

import com.optimaNet.auth.service.KeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AadhaarKeyProvider implements KeyProvider {

    @Value("${security.aadhaar.key}")
    private String base64Key;

    @Override
    public SecretKey getAadhaarKey() {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, "AES");
    }
}
