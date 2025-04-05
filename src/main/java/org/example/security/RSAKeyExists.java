package org.example.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

@Component
public class RSAKeyExists implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(RSAKeyExists.class);

    private static final String KEY_DIRECTORY = "src/main/resources/";
    private static final String PUBLIC_KEY_PATH = KEY_DIRECTORY + "/public.pem";
    private static final String PRIVATE_KEY_PATH = KEY_DIRECTORY + "/private.pem";
    private  static final String TEST="coffee mark";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File publicKeyFile = new File(PUBLIC_KEY_PATH);
        File privateKeyFile = new File(PRIVATE_KEY_PATH);

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            if (!keysMatch(publicKeyFile, privateKeyFile)) {
                logger.warn("The keys do not match. Deleting...");
                publicKeyFile.delete();
                privateKeyFile.delete();
                generateKeys();
            } else {
                logger.warn("The keys are found and valid.");
            }
        } else {
            logger.warn("No keys found. Generating new ones...");
            generateKeys();
        }
    }

    private void generateKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 2048-біт
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Кодування в PEM
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        // Створення директорії якщо не існує
        Files.createDirectories(Paths.get(KEY_DIRECTORY));

        // Запис ключів
        Files.write(Paths.get(PUBLIC_KEY_PATH), publicKeyPem.getBytes());
        Files.write(Paths.get(PRIVATE_KEY_PATH), privateKeyPem.getBytes());

        logger.info("New RSA keys are generated and saved.");
    }

    private boolean keysMatch(File publicKeyFile, File privateKeyFile) {
        try {
            // Зчитування ключів з файлів
            byte[] publicBytes = readKeyBytes(publicKeyFile);
            byte[] privateBytes = readKeyBytes(privateKeyFile);

            // Створення об'єктів ключів
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicBytes);
            PublicKey publicKey = keyFactory.generatePublic(pubSpec);

            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privSpec);

            // Перевірка: шифруємо текст публічним, розшифровуємо приватним
            byte[] testMessage = TEST.getBytes();
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = encryptCipher.doFinal(testMessage);

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = decryptCipher.doFinal(encrypted);

            return new String(decrypted).equals(TEST);

        } catch (Exception e) {
            return false;
        }
    }

    private byte[] readKeyBytes(File file) throws IOException {
        String key = new String(Files.readAllBytes(file.toPath()))
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\n", "")
                .trim();

        return Base64.getDecoder().decode(key);
    }
}

