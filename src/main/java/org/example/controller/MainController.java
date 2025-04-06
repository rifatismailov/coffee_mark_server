package org.example.controller;

import org.example.authorization.AuthorizationRequest;
import org.example.authorization.AuthorizationResponse;
import org.example.security.Decryptor;
import org.example.until.Hash;
import org.example.until.KeyUtils;
import org.example.until.LocalErrorResponse;
import org.example.registration.RegisterRequest;
import org.example.registration.RegisterResponse;
import org.example.security.PublicKeyRequest;
import org.example.security.PublicKeyResponse;
import org.example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

@RestController
@RequestMapping("/api/auth")
public class MainController {

    @Autowired
    private UserService userService;
    private static final String KEY_DIRECTORY = "src/main/resources/";

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


    @PostMapping("/public-key")
    public ResponseEntity<?> getPublicKey(@RequestBody PublicKeyRequest request) {
        try {
            PublicKey publicKey = KeyUtils.loadPublicKey(new File(KEY_DIRECTORY+"public.pem"));
            if(!Hash.getPublicKeyHash(publicKey).equals(request.getHash())){
                System.out.println("PublicKey hash: " + request.getHash());
                // Читаємо публічний ключ з файлу у строку
                String publicKeyStr = new String(Files.readAllBytes(Paths.get(KEY_DIRECTORY+"public.pem")));

                return ResponseEntity.ok(new PublicKeyResponse(true, publicKeyStr));
            }
            //якщо хеш суми співпали відправляємо назад до клієнта
            return ResponseEntity.ok(new PublicKeyResponse(true, request.getHash()));
        } catch (Exception e) {
            return ResponseEntity.ok(new LocalErrorResponse("Помилка отримання ключа", e.getMessage()));
        }
    }

    @PostMapping("/authorization")
    public ResponseEntity<?> getAuthorization(@RequestBody AuthorizationRequest request) {
        try {
            PrivateKey privateKey = KeyUtils.loadPrivateKey(new File(KEY_DIRECTORY+"private.pem"));
            String email = Decryptor.decrypt( request.getEmail(),privateKey);
            String password = Decryptor.decrypt(request.getPassword(),privateKey);
            System.out.println("Authorization request: " + email+" "+password);
            // Читаємо публічний ключ з файлу private.pem

            return ResponseEntity.ok(new AuthorizationResponse(true, email+" "+password));
        } catch (Exception e) {
            return ResponseEntity.ok(new LocalErrorResponse("Помилка авторізації", e.getMessage()));
        }
    }
}



