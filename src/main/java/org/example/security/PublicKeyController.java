package org.example.security;

import org.example.LocalErrorResponse;
import org.example.user.UserController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class PublicKeyController {
    private static final String KEY_DIRECTORY = "src/main/resources/";

    @PostMapping("/public-key")
    public ResponseEntity<?> getPublicKey(@RequestBody PublicKeyRequest request) {
        try {

            // Читаємо публічний ключ з файлу
            String publicKey = new String(Files.readAllBytes(Paths.get("src/main/resources/public.pem")));
            System.out.println("Public Key : " + publicKey.length());
            return ResponseEntity.ok(new PublicKeyResponse(true, publicKey));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new LocalErrorResponse("Помилка отримання ключа", e.getMessage()));
        }
    }
}

