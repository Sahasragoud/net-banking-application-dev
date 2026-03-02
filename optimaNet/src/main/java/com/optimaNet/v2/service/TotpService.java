package com.optimaNet.v2.service;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;

@Service
public class TotpService {
    private static final int SECRET_BYTES = 20;
    private static final long STEP_SECONDS = 30L;

    public String generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        new SecureRandom().nextBytes(bytes);
        return new Base32().encodeToString(bytes).replace("=", "");
    }

    public boolean verifyCode(String secretBase32, String code) {
        if (secretBase32 == null || code == null || !code.matches("\\d{6}")) {
            return false;
        }
        long nowStep = Instant.now().getEpochSecond() / STEP_SECONDS;
        for (long step = nowStep - 1; step <= nowStep + 1; step++) {
            if (generateCode(secretBase32, step).equals(code)) {
                return true;
            }
        }
        return false;
    }

    public String buildOtpAuthUrl(String issuer, String accountName, String secretBase32) {
        String encIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encAccount = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        return "otpauth://totp/" + encIssuer + ":" + encAccount
                + "?secret=" + secretBase32 + "&issuer=" + encIssuer + "&digits=6&period=30";
    }

    private String generateCode(String secretBase32, long timeStep) {
        try {
            byte[] key = new Base32().decode(secretBase32);
            byte[] msg = ByteBuffer.allocate(8).putLong(timeStep).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(msg);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % 1_000_000;
            return String.format("%06d", otp);
        } catch (Exception ex) {
            return "000000";
        }
    }
}
