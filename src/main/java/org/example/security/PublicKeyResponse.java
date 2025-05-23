package org.example.security;

import lombok.Getter;

@Getter
public class PublicKeyResponse {
    private final boolean success;
    private final String message;
    public PublicKeyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

