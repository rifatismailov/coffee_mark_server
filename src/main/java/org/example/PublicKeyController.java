package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class PublicKeyController {

    @PostMapping("/public-key")
    public String getPublicKey() {
        try {
            // Читаємо публічний ключ з файлу
            String publicKey = new String(Files.readAllBytes(Paths.get("src/main/resources/public.pem")));
            return publicKey;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading public key";
        }
    }
}

