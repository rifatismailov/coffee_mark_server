package org.example.controller;

import org.example.authorization.AuthorizationRequest;
import org.example.security.*;
import org.example.registration.RegisterRequest;
import org.example.user.UserRepository;
import org.example.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private static final String KEY_DIRECTORY = "src/main/resources/";

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
    return userService.registerUser(request);
    }

    @PostMapping("/authorization")
    public ResponseEntity<?> getAuthorization(@RequestBody AuthorizationRequest request) {
        return userService.authorization(request);
    }

    @PostMapping("/public-key")
    public ResponseEntity<?> getPublicKey(@RequestBody PublicKeyRequest request) {
        return userService.getPublicKey(request);
    }

    @PostMapping("/local-public-key")
    public ResponseEntity<?> setLocalPublicKey(@RequestBody LocalPublicKeyRequest request) {
        return userService.updateLocalPublicKey(request);
    }
}



