package com.einabit.client.security;

import com.einabit.client.security.exceptions.EncryptorException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public class AESEncryptor implements Encryptor {

    private static final Integer KEY_SIZE = 16;

    private final String password;

    public AESEncryptor(String password) {
        this.password = password;
    }

    @Override
    public String encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.WRAP_MODE, generateKey(), generateIv());

            byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new EncryptorException(e.getMessage());
        }
    }

    private Key generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(password);
            keyGenerator.init(KEY_SIZE);

            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptorException(e.getMessage());
        }
    }

    private AlgorithmParameterSpec generateIv() {
        byte[] iv = new byte[KEY_SIZE];
        new SecureRandom().nextBytes(iv);

        return new IvParameterSpec(iv);
    }

}
