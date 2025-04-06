package org.example.authorization;

import lombok.Getter;

@Getter
public class AuthorizationResponse {
    private final boolean success;
    private final String message;

    public AuthorizationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
