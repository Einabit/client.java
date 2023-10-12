package com.einabit.client.security;

/**
 * Encryptor interface
 */
public interface Encryptor {

    /**
     * Encrypt string.
     *
     * @param message message
     * @return string
     */
    String encrypt(String message);

}
