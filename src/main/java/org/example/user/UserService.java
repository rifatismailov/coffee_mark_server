package org.example.user;

import org.example.authorization.AuthorizationRequest;
import org.example.authorization.Respond;
import org.example.authorization.AuthorizationResponse;
import org.example.cafe.Cafe;
import org.example.registration.RegisterRequest;
import org.example.registration.RegisterResponse;
import org.example.security.*;
import org.example.until.Hash;
import org.example.until.KeyUtils;
import org.example.until.LocalErrorResponse;
import org.example.until.LocalErrorType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.until.KeyUtils.loadPublicKey;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PublicKeyRecordRepository publicKeyRecordRepository;
    private static final String KEY_DIRECTORY = "src/main/resources/";

    public ResponseEntity<?> getPublicKey(PublicKeyRequest request) {
        try {
            PublicKey publicKey = loadPublicKey(new File(KEY_DIRECTORY + "public.pem"));
            if (!Hash.getPublicKeyHash(publicKey).equals(request.getHash())) {
                System.out.println("PublicKey hash: " + request.getHash());
                // Читаємо публічний ключ з файлу у строку
                String publicKeyStr = new String(Files.readAllBytes(Paths.get(KEY_DIRECTORY + "public.pem")));

                return ResponseEntity.ok(new PublicKeyResponse(true, publicKeyStr));
            }
            //якщо хеш суми співпали відправляємо назад до клієнта
            return ResponseEntity.ok(new PublicKeyResponse(true, request.getHash()));
        } catch (Exception e) {
            return ResponseEntity.ok(new LocalErrorResponse(LocalErrorType.KEY_RETRIEVAL_ERROR));
        }
    }

    private void savePublicKeyRecord(User user, String uuid, String publicKey) {
        System.out.println("Спроба збереження ключа: UUID = " + uuid + ", userId = " + user.getId());
        try {
            Optional<PublicKeyRecord> existing = publicKeyRecordRepository.findByUuid(uuid);
            System.out.println("existing: " + existing);
            if (existing.isPresent()) {
                System.out.println("Оновлення ключа для UUID: " + uuid);
                PublicKeyRecord record = existing.get();
                record.setPublicKey(publicKey);
                record.setCreatedAt(LocalDateTime.now());
                publicKeyRecordRepository.save(record);
            } else {
                System.out.println("Створення нового запису для UUID: " + uuid);
                PublicKeyRecord record = new PublicKeyRecord();
                record.setUser(user);
                record.setUuid(uuid);
                record.setPublicKey(publicKey);
                record.setCreatedAt(LocalDateTime.now());
                publicKeyRecordRepository.save(record);
            }
        }catch (Exception e) {
            System.out.println("Exception: " + e);

        }

    }


    public ResponseEntity<?> updateLocalPublicKey(LocalPublicKeyRequest request) {
        try {
            PrivateKey privateKey = KeyUtils.loadPrivateKey(new File(KEY_DIRECTORY + "private.pem"));
            AuthorizationRequest authorizationRequest = request.getRequest();
            String email = Decryptor.decrypt(authorizationRequest.getEmail(), privateKey);
            String password = Decryptor.decrypt(authorizationRequest.getPassword(), privateKey);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.USER_NOT_FOUND));
            }
            User user = userOptional.get();

            if (!BCrypt.checkpw(password, user.getPassword())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.INVALID_PASSWORD));
            }

            // Унікальний UUID пристрою
            String uuid = authorizationRequest.getUuid();
            String publicKey = request.getKey();

            // Перевірка, чи вже існує такий UUID
            savePublicKeyRecord(user, uuid, publicKey);

            return ResponseEntity.ok(new LocalPublicKeyResponse(true,getRespond(user, password, publicKey).toString()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LocalErrorResponse(LocalErrorType.KEY_LOAD_ERROR));
        }
    }

    public ResponseEntity<?> registerUser(RegisterRequest request) {
        try {
            PrivateKey privateKey = KeyUtils.loadPrivateKey(new File(KEY_DIRECTORY + "private.pem"));

            String username = Decryptor.decrypt(request.getUsername(), privateKey);
            String email = Decryptor.decrypt(request.getEmail(), privateKey);
            String password = Decryptor.decrypt(request.getPassword(), privateKey);
            String image = Decryptor.decrypt(request.getImage(), privateKey);
            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new LocalErrorResponse(LocalErrorType.USER_NAME_ALREADY_EXISTS));
            }

            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new LocalErrorResponse(LocalErrorType.USER_EMAIL_ALREADY_EXISTS));
            }

            User user = createUserFromRequest(request, username, email, password, image);


            if (user.getRole() == Role.BARISTA) {
                System.out.println(request.getCafeList());
                if (request.getCafeList() == null || request.getCafeList().isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(new RegisterResponse(false, "Баріста має мати хоча б одне кафе"));
                }
                List<Cafe> cafes = createCafeList(request, privateKey, user);
                user.setCafes(cafes);
            }
            userRepository.save(user);
            String uuid = request.getUuid();
            String publicKey = request.getPublic_key();

            // Перевірка, чи вже існує такий UUID
            System.out.println(user.getId()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getImage());

            savePublicKeyRecord(user, uuid, publicKey);

            return ResponseEntity.ok(new RegisterResponse(true, getRespond(user, password, publicKey).toString()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LocalErrorResponse(LocalErrorType.REGISTRATION_ERROR));
        }
    }

    @NotNull
    private User createUserFromRequest(@NotNull RegisterRequest request, String username, String email, String password, String image) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setImage(image);
        user.setRole(Role.valueOf(request.getRole()));
        return user;
    }

    private List<Cafe> createCafeList(@NotNull RegisterRequest request, PrivateKey privateKey, User user) {
        return request.getCafeList().stream().map(c -> {
            Cafe cafe = new Cafe();
            cafe.setName(c.getName());
            cafe.setAddress(c.getAddress());
            try {
                cafe.setCafe_image(Decryptor.decrypt(c.getCafe_image(), privateKey));
            } catch (Exception e) {
                cafe.setCafe_image("coffee_mark.png");
            }
            cafe.setBarista(user);
            return cafe;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<?> authorization(AuthorizationRequest request) {
        try {
            PrivateKey privateKey = KeyUtils.loadPrivateKey(new File(KEY_DIRECTORY + "private.pem"));

            String email = Decryptor.decrypt(request.getEmail(), privateKey);
            String password = Decryptor.decrypt(request.getPassword(), privateKey);
            String uuid = request.getUuid();

            System.out.println("Authorization request: " + email + " " + password+" "+uuid + " " + request.getHash_user_public());

            // Пошук користувача
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.USER_NOT_FOUND));
            }

            User user = userOptional.get();

            // Перевірка паролю
            if (!BCrypt.checkpw(password, user.getPassword())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.INVALID_PASSWORD));
            }

            // Перевірка публічного ключа по UUID
            Optional<PublicKeyRecord> keyRecordOptional = publicKeyRecordRepository.findByUuid(uuid);
            if (keyRecordOptional.isEmpty()) {

                // Якщо ключ не знайдено — повідомити клієнту, щоб надіслав ключ знову
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.PUBLIC_KEY_MISSING));
            }

            PublicKeyRecord record = keyRecordOptional.get();

            // Перевірка, що ключ належить користувачу
            if (!record.getUser().getId().equals(user.getId())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.INVALID_PUBLIC_KEY));
            }

            // Перевірка хешу ключа
            try {
                PublicKey storedKey = loadPublicKey(record.getPublicKey());
                String hashStored = Hash.getPublicKeyHash(storedKey);

                if (!hashStored.equals(request.getHash_user_public())) {
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new LocalErrorResponse(LocalErrorType.INVALID_PUBLIC_KEY));
                }
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LocalErrorResponse(LocalErrorType.KEY_COMPARISON_ERROR));
            }

            // Авторизація успішна
            AuthorizationResponse response = new AuthorizationResponse(true, getRespond(user, password, record.getPublicKey()).toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LocalErrorResponse(LocalErrorType.AUTH_FAILED));
        }
    }

    @GetMapping("/info")
    public Respond getRespond(User user, String password, String key) throws Exception {
        // Наприклад, у тебе є об’єкт user
        PublicKey publicKey = loadPublicKey(key);
        return Respond.builder()
                .username(Encryptor.encryptText(user.getUsername(), publicKey))
                .email(Encryptor.encryptText(user.getEmail(), publicKey))
                .password(Encryptor.encryptText(password, publicKey))
                .role(user.getRole().toString())
                .image(Encryptor.encryptText(user.getImage(), publicKey))
                .build();
    }
}



