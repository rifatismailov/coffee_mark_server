package org.example.security;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

public class Decryptor {
    // Розшифрування пароля
    public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
        // Декодуємо Base64 рядок у байти
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Розшифровуємо і перетворюємо байти в рядок
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
