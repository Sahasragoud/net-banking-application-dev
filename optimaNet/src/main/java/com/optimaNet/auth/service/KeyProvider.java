package com.optimaNet.auth.service;

import javax.crypto.SecretKey;

public interface KeyProvider {
    SecretKey getAadhaarKey();
}

