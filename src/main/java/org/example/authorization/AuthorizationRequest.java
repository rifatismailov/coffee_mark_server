package org.example.authorization;

import lombok.Getter;

@Getter
public class AuthorizationRequest {
    private String email;
    private String password;
    private String hash_user_public;
    private String uuid;
}

