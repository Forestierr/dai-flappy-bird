package ch.heigvd.dai.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private static int SERVER_ID;
    private static final String TEXTUAL_DATA = "👋 from Server " + SERVER_ID;

    public ClientHandler(Socket socket, int serverId) {
        this.socket = socket;
        this.SERVER_ID = serverId;
    }

    @Override
    public void run() {
        try (socket; // This allow to use try-with-resources with the socket
             BufferedReader in =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            System.out.println(
                    "[Server "
                            + SERVER_ID
                            + "] new client connected from "
                            + socket.getInetAddress().getHostAddress()
                            + ":"
                            + socket.getPort());

            System.out.println(
                    "[Server " + SERVER_ID + "] received textual data from client: " + in.readLine());

            try {
                System.out.println(
                        "[Server " + SERVER_ID + "] sleeping for 10 seconds to simulate a long operation");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(
                    "[Server " + SERVER_ID + "] sending response to client: " + TEXTUAL_DATA);

            out.write(TEXTUAL_DATA + "\n");
            out.flush();

            System.out.println("[Server " + SERVER_ID + "] closing connection");
        } catch (IOException e) {
            System.out.println("[Server " + SERVER_ID + "] exception: " + e);
        }
    }
}
