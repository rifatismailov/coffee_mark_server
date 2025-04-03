package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class RegisterRequest {
    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String email;
    @Getter
    private String role;
    @Getter
    @JsonProperty("cafes")  // Додаємо анотацію для мапінгу
    private List<CafeRequest> cafeList; // Тепер ім'я залишається як cafeList на сервері

    public static class CafeRequest {
        @Getter
        private String name;
        @Getter
        private String address;
    }
}

