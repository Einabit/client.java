package com.einabit.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class EinabitClient {

    private static final Logger LOGGER = Logger.getLogger(EinabitClient.class.getName());
    private static final String MESSAGE_DELIMITER = ",";
    private static final int BUFFER_SIZE = 21;

    private final String host;
    private final int port;

    private EinabitClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public static EinabitClientBuilder builder() {
        return new EinabitClientBuilder();
    }

    public String value(final String variable) {
        return execute(Operation.VALUE.name().toLowerCase() + MESSAGE_DELIMITER + variable);
    }

    public String fetch(final String variable, final long from, final long to) {
        return execute(Operation.FETCH.name().toLowerCase() + MESSAGE_DELIMITER + variable + MESSAGE_DELIMITER + from + MESSAGE_DELIMITER + to);
    }

    public void tap(final String variable, final EinabitServerListener callback) {
        try (
                final Socket socket = connect();
                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())
        ) {
            dataOutputStream.writeBytes(Operation.TAP.name().toLowerCase() + MESSAGE_DELIMITER + variable);

            int readBytes;

            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readBytes = dataInputStream.read(buffer)) != -1) {
                callback.onSubscribe(new String(buffer));
                buffer = new byte[readBytes];
            }
        } catch (IOException e) {
            LOGGER.severe("Could not read the value");
        }
    }

    private Socket connect() throws IOException {
        return new Socket(host, port);
    }

    private String execute(final String message) {
        try (
                final Socket socket = connect();
                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())
        ) {
            dataOutputStream.writeBytes(message);

            return new String(dataInputStream.readAllBytes());
        } catch (IOException e) {
            LOGGER.severe("Could not read the value");
        }

        return null;
    }

    public static class EinabitClientBuilder {

        private String host;
        private int port = 1337;

        public EinabitClientBuilder host(final String host) {
            this.host = host;
            return this;
        }

        public EinabitClientBuilder port(final int port) {
            this.port = port;
            return this;
        }

        public EinabitClient build() {
            return new EinabitClient(host, port);
        }

    }

}
