package org.example.user;

import org.example.RegisterRequest;
import org.example.RegisterResponse;
import org.example.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            System.out.println("Received request: " + request);

            // Успішна реєстрація
            RegisterResponse registeredUser = userService.registerUser(request);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            // Повний JSON-звіт про помилку
            return ResponseEntity.ok(new ErrorResponse("Помилка реєстрації", e.getMessage()));
        }
    }

    // Клас для звітів про помилки
    static class ErrorResponse {
        private String status;
        private String message;

        public ErrorResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}



