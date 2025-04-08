package org.example.until;

import lombok.Getter;

// Клас для звітів про помилки
@Getter
public class LocalErrorResponse {
    // Клас для звітів про помилки

    private String status;
    private String message;

    public LocalErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Можна також додати конструктор, який приймає enum:
    public LocalErrorResponse(LocalErrorType errorType) {
        this.status = errorType.getCode();
        this.message = errorType.getMessage();
    }
}
