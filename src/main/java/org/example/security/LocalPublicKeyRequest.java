package org.example.security;

import lombok.Getter;
import org.example.authorization.AuthorizationRequest;

@Getter
public class LocalPublicKeyRequest {
    private AuthorizationRequest request;
    private String key;

}

