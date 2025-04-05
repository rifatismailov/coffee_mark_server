package org.example.security;

import lombok.Getter;

@Getter
public class PublicKeyRequest {
    // Ти можеш передавати якусь інформацію в запиті, наприклад, ідентифікатор
    @Getter
    private String request_body;
}