package com.optimaNet.auth.security;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtils {

    private IpUtils() {
        // Utility class — prevent instantiation
    }

    public static String getClientIp(HttpServletRequest request) {

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "CF-Connecting-IP",
                "Forwarded"
        };

        for (String header : headers) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
