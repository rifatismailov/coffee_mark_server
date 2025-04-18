package org.example.until;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


import java.io.IOException;

public class KeyUtils {

    public static PublicKey loadPublicKey(String pem) throws Exception {
        byte[] encoded = Base64.getDecoder().decode(getReplaceText(pem));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static PublicKey loadPublicKey(File publicKeyFile) {
        try {
            byte[] publicBytes = readKeyBytes(publicKeyFile);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicBytes);
            return keyFactory.generatePublic(pubSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey loadPrivateKey(File privateKeyFile) {
        try {
            byte[] privateBytes = readKeyBytes(privateKeyFile);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateBytes);
            return keyFactory.generatePrivate(privSpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readKeyBytes(File file) throws IOException {
        return Base64.getDecoder().decode(getReplaceText(new String(Files.readAllBytes(file.toPath()))));
    }

    private static String getReplaceText(String text) {
        return text
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\n", "")
                .trim();
    }

    public static void saveKey(String key_path, String pem) throws IOException {
        // Отримуємо потік для запису в файл у внутрішньому сховищі
        Files.write(Paths.get(key_path), pem.getBytes());
    }

}
