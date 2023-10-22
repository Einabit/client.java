package com.einabit.client.security;

import com.einabit.client.security.exceptions.EncryptorException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Implementation of AES encryption algorithm.
 */
public class AESEncryptor implements Encryptor {

    private static final int KEY_SIZE = 16;

    private final String key;

    /**
     * Instantiates a new Aes encryptor.
     *
     * @param key key
     */
    public AESEncryptor(String key) {
        this.key = key;
    }

    @Override
    public String encrypt(String message) {

        final Key secretKey = new SecretKeySpec(this.key.getBytes(UTF_8), "AES");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[KEY_SIZE];
        secureRandom.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            byte[] encryptedMessage = cipher.doFinal(message.getBytes(UTF_8));

            byte[] combinedData = new byte[iv.length + encryptedMessage.length];
            System.arraycopy(iv, 0, combinedData, 0, iv.length);
            System.arraycopy(encryptedMessage, 0, combinedData, iv.length, encryptedMessage.length);

            return byteArrayToHexString(combinedData);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new EncryptorException(e.getMessage());
        }
    }

    private String byteArrayToHexString(byte[] array) {
        StringBuilder result = new StringBuilder();
        for (byte b : array) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

}
