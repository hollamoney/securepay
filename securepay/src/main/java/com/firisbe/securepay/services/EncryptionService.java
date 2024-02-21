package com.firisbe.securepay.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
@Component
public class EncryptionService {

    private final String key;
    private final String algo;

    // Sabit IV
    private static final byte[] FIXED_IV = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10 };

    public EncryptionService(
            @Value("${encryption.key}") String key, @Value("${encryption.algorithm}") String algo) {
        this.key = key;
        this.algo = algo;
    }

    public String encrypt(String value) {
        if (value != null) {
            try {
                SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

                Cipher cipher = Cipher.getInstance(algo);

                // Sabit IV kullanımı
                IvParameterSpec iv = new IvParameterSpec(FIXED_IV);

                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(value.getBytes());

                byte[] combined = new byte[FIXED_IV.length + encrypted.length];
                System.arraycopy(FIXED_IV, 0, combined, 0, FIXED_IV.length);
                System.arraycopy(encrypted, 0, combined, FIXED_IV.length, encrypted.length);

                return Base64.getEncoder().encodeToString(combined);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public String decrypt(String encrypted) {
        if (encrypted != null) {
            try {
                SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

                Cipher cipher = Cipher.getInstance(algo);

                byte[] combined = Base64.getDecoder().decode(encrypted);

                // Sabit IV kullanımı
                IvParameterSpec iv = new IvParameterSpec(FIXED_IV);

                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                byte[] original = cipher.doFinal(combined, FIXED_IV.length, combined.length - FIXED_IV.length);

                return new String(original);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}