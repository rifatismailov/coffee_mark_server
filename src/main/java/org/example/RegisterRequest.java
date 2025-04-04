package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.List;

public class RegisterRequest {
    @Getter
    @Id
    private Long id;  // або будь-який інший унікальний ідентифікатор
    @Getter
    @Column
    private String username;
    @Getter
    @Column
    private String password;
    @Getter
    @Column
    private String email;
    @Getter
    @Column
    private String role;
    @Getter
    @Column(name = "cafe_image") // Додаємо нове поле до таблиці
    private String cafeImage;
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

