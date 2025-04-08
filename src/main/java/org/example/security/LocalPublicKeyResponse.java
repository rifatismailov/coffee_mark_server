package org.example.security;

import lombok.Getter;

@Getter
public class LocalPublicKeyResponse {
    private boolean success;
    private String message;

    public LocalPublicKeyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}

