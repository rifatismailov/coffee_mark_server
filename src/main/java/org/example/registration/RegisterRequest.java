package org.example.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.List;

@Getter
public class RegisterRequest {
    @Id
    private Long id;  // або будь-який інший унікальний ідентифікатор
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String role;
    @Column
    private String image;
    @Column
    private String public_key;  // Зберігаємо у Base64-форматі
    @Column
    private  String uuid;

    @JsonProperty("cafes")  // Додаємо анотацію для мапінгу
    private List<CafeRequest> cafeList; // Тепер ім'я залишається як cafeList на сервері



    public static class CafeRequest {
        @Getter
        private String name;
        @Getter
        private String address;
        @Getter
        private String cafe_image;
    }
}

