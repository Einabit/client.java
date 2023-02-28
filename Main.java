// https://stackoverflow.com/questions/3895461/non-blocking-sockets

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {

    private final String serverName = "sandbox";
    private final int serverPort = 1337;

    public Main() {
        try {
            Socket socket = new Socket(serverName, serverPort);
            System.out.println("Connected to server " + socket.getRemoteSocketAddress());

            DataOutputStream dut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String outMsg = "value,temp1";
            dut.write(outMsg.getBytes());

            // inicio bloque "leer"
            byte[] buffer = new byte[1024];
            int bytesRead = dis.read(buffer);
            ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
            bufferStream.write(buffer, 0, bytesRead);
            // fin bloque "leer"

            // si colocamos el bloque leer DESPUéS de DataOutputStream::flush
            // tenemos una race condition y no somos capaces de leer sistemáticamente
            // el valor recibido del servidor

            dut.flush();
            // el servidor enviará su mensaje inmediatamente después de recibir
            // el mensaje del cliente

            String encodedString = new String(buffer, StandardCharsets.UTF_8);
            System.out.println("Response from server: " + encodedString);

        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Main client1 = new Main();
    }
}
