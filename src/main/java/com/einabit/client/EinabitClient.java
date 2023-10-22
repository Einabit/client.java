package com.einabit.client;

import com.einabit.client.security.AESEncryptor;
import com.einabit.client.security.Encryptor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Logger;

import static com.einabit.client.Operation.*;

/**
 * Einabit client.
 * <p>
 * Predefined client which you can use to perform different operations against Einabit services. We recommend
 * using our {@link EinabitClient#builder()} to ease you setting up the configuration for the client.
 */
public class EinabitClient {

    private static final Logger LOGGER = Logger.getLogger(EinabitClient.class.getName());
    private static final String MESSAGE_DELIMITER = ",";
    private static final String EOL = "\n";
    private static final int BUFFER_SIZE = 21;

    private final String host;
    private final int port;
    private Encryptor encryptor;

    private EinabitClient(final String host, final int port, final String key) {
        this.host = host;
        this.port = port;

        if (key != null && !key.isEmpty()) {
            this.encryptor = new AESEncryptor(key);
        }
    }

    /**
     * Einabit client builder.
     *
     * @return einabit client builder
     */
    public static EinabitClientBuilder builder() {
        return new EinabitClientBuilder();
    }

    /**
     * Get current value of a variable.
     *
     * @param variable variable
     * @return variable value
     */
    public String value(final String variable) {
        return execute(VALUE.name().toLowerCase() +
                MESSAGE_DELIMITER + variable);
    }

    /**
     * Fetch values of a variable.
     *
     * @param variable variable
     * @param from     from timestamp
     * @param to       to timestamp
     * @return values delimited by commas
     */
    public String fetch(final String variable, final long from, final long to) {
        return execute(FETCH.name().toLowerCase() +
                MESSAGE_DELIMITER + variable +
                MESSAGE_DELIMITER + from +
                MESSAGE_DELIMITER + to);
    }

    /**
     * Subscribe to a variable.
     * <p>
     * In order to use this operation you will need to implement {@link EinabitServerListener} interface
     * in your custom listener.
     *
     * @param variable variable
     * @param callback callback which will be executed everytime it receives a value.
     */
    public void tap(final String variable, final EinabitServerListener callback) {
        try (
                final Socket socket = connect();
                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())
        ) {
            final String message = TAP.name().toLowerCase() + MESSAGE_DELIMITER + variable + EOL;
            final String messageToWrite = Optional.ofNullable(encryptor)
                    .map(validEncryptor -> validEncryptor.encrypt(message))
                    .orElse(message);

            dataOutputStream.writeBytes(messageToWrite);

            int readBytes;

            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readBytes = dataInputStream.read(buffer)) != -1 && !Thread.currentThread().isInterrupted()) {
                callback.onSubscribe(new String(buffer));
                buffer = new byte[readBytes];
            }
        } catch (IOException e) {
            LOGGER.severe("Could not read the value, caused by: " + e.getMessage());
        }
    }

    /**
     * Fetch last n values of a variable.
     *
     * @param variable variable
     * @param amount   amount of values to fetch
     * @return values delimited by commas
     */
    public String last(final String variable, final Integer amount) {
        return execute(LAST.name().toLowerCase() +
                MESSAGE_DELIMITER + variable +
                MESSAGE_DELIMITER + amount);
    }

    private String execute(final String message) {
        try (
                final Socket socket = connect();
                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())
        ) {
            final String messageToWrite = Optional.ofNullable(encryptor)
                    .map(validEncryptor -> validEncryptor.encrypt(message + EOL))
                    .orElse(message + EOL);

            dataOutputStream.writeBytes(messageToWrite);

            return new String(dataInputStream.readAllBytes());
        } catch (IOException e) {
            LOGGER.severe("Could not read the value, caused by: " + e.getMessage());
        }

        return null;
    }

    private Socket connect() throws IOException {
        return new Socket(host, port);
    }

    /**
     * Einabit client builder.
     * <p>
     * Allows to easily create new clients with predefined operations. By default, the port is 1337, in
     * case you need to specify a different one you can do it by using {@link EinabitClientBuilder#port(int)} method.
     */
    public static class EinabitClientBuilder {

        private String host;
        private int port = 1337;
        private String key;

        /**
         * Configure Einabit client host.
         *
         * @param host host
         * @return einabit client builder
         */
        public EinabitClientBuilder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * Configure Einabit client port.
         *
         * @param port port
         * @return einabit client builder
         */
        public EinabitClientBuilder port(final int port) {
            this.port = port;
            return this;
        }

        /**
         * Configure Einabit client key.
         *
         * @param key key
         * @return einabit client builder
         */
        public EinabitClientBuilder key(final String key) {
            this.key = key;
            return this;
        }

        /**
         * Build Einabit client.
         *
         * @return Einabit client
         */
        public EinabitClient build() {
            return new EinabitClient(host, port, key);
        }

    }

}
