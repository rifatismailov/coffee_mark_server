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

}
