package org.example.security;

import lombok.Getter;

@Getter
public class PublicKeyRequest {
    // Ти можеш передавати якусь інформацію в запиті, наприклад, ідентифікатор
    private String hash;
}