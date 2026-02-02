package com.optimaNet.auth.service;

import com.google.common.primitives.Bytes;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CryptoService {

    private final KeyProvider keyProvider;

    public CryptoService(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = keyProvider.getAadhaarKey();

            byte[] iv = SecureRandom.getInstanceStrong().generateSeed(12);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(
                    Bytes.concat(iv, encrypted)
            );
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String cipherText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] iv = Arrays.copyOfRange(decoded, 0, 12);
            byte[] encrypted = Arrays.copyOfRange(decoded, 12, decoded.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    keyProvider.getAadhaarKey(),
                    new GCMParameterSpec(128, iv)
            );

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
