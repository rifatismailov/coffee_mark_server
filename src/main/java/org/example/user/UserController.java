package org.example.user;

import org.example.LocalErrorResponse;
import org.example.registration.RegisterRequest;
import org.example.registration.RegisterResponse;
import org.example.registration.UserService;
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
            return ResponseEntity.ok( new LocalErrorResponse("Помилка реєстрації", e.getMessage()));
        }
    }


}



