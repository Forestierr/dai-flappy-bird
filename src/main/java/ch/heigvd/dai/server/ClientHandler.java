package ch.heigvd.dai.server;

import ch.heigvd.dai.utils.Message;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

  private final Socket socket;
  private static int SERVER_ID;

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

      String clientMessage = in.readLine();
      Message message = Message.fromString(clientMessage);

      switch (message) {
        case START:
          System.out.println("[Server " + SERVER_ID + "] received STRT message");
          out.write(Message.ACK.toString());
          break;
        case FLY:
          System.out.println("[Server " + SERVER_ID + "] received FLYY message");
          out.write(Message.DATA.toString());
          break;
        case JOIN:
          System.out.println("[Server " + SERVER_ID + "] received LOBY message");
          if (message.getData() != null) {
            System.out.println(
                "[Server " + SERVER_ID + "] received LOBY message with data: " + message.getData());
            System.out.println("[Server " + SERVER_ID + "] Join lobby " + message.getData());
            out.write(Message.DATA.toString());
          } else {
            System.out.println("[Server " + SERVER_ID + "] received LOBY message without data");
            System.out.println("[Server " + SERVER_ID + "] Create lobby");
            out.write(Message.DATA.toString());
          }
          break;
        case LIST:
          System.out.println("[Server " + SERVER_ID + "] received LIST message");
          out.write(Message.DATA.toString());
          break;
        case PIPE:
          System.out.println("[Server " + SERVER_ID + "] received PIPE message");
          out.write(Message.DATA.toString());
          break;
        case QUIT:
          System.out.println("[Server " + SERVER_ID + "] received QUIT message");
          out.write(Message.ACK.toString());
          break;
        default:
          System.out.println("[Server " + SERVER_ID + "] received unknown message");
          out.write(Message.ERROR.toString());
          break;
      }

      out.flush();

    } catch (IOException e) {
      System.out.println("[Server " + SERVER_ID + "] exception: " + e);
      e.printStackTrace();
    }
  }
}
