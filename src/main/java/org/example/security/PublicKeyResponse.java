package org.example.security;

public class PublicKeyResponse {
    private boolean success;
    private String message;
    public PublicKeyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

