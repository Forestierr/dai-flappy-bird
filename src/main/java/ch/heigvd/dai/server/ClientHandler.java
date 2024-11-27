package ch.heigvd.dai.server;

import ch.heigvd.dai.utils.Message;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

  private final Socket socket;
  private int serverId;
  private Game game;

  public ClientHandler(Socket socket, int serverId) {
    this.socket = socket;
    this.serverId = serverId;
  }

  @Override
  public void run() {
    try (socket; // This allow to use try-with-resources with the socket
        BufferedReader input =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter output =
            new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

      System.out.println("[Server " + serverId + "] Sending ACK to client");
      output.write(Message.ACK.toString());
      output.flush();

      Message message = Message.fromString(Message.readUntilEOT(input));

      switch (message) {
        case START:
          System.out.println("[Server " + serverId + "] received STRT message");
          game = new Game();
          game.update();
          output.write(Message.START.toString());
          output.flush();
          break;
        case FLY:
          System.out.println("[Server " + serverId + "] received FLYY message");
          game.update();
          System.out.println("[Game DATA] " + game);
          // Send the game data to the client
          Message data = Message.DATA.setData(game.toString());
          System.out.println("[Server " + serverId + "] Sending DATA to client");
          output.write(data.toString());
          output.flush();
          break;
        case JOIN:
          System.out.println("[Server " + serverId + "] received LOBY message");
          if (message.getData() != null) {
            System.out.println(
                "[Server " + serverId + "] received LOBY message with data: " + message.getData());
            System.out.println("[Server " + serverId + "] Join lobby " + message.getData());
            output.write(Message.DATA.toString());
          } else {
            System.out.println("[Server " + serverId + "] received LOBY message without data");
            System.out.println("[Server " + serverId + "] Create lobby");
            output.write(Message.DATA.toString());
          }
          break;
        case LIST:
          System.out.println("[Server " + serverId + "] received LIST message");
          output.write(Message.DATA.toString());
          break;
        case PIPE:
          System.out.println("[Server " + serverId + "] received PIPE message");
          output.write(Message.DATA.toString());
          break;
        case QUIT:
          System.out.println("[Server " + serverId + "] received QUIT message");
          output.write(Message.ACK.toString());
          break;
        default:
          System.out.println("[Server " + serverId + "] received unknown message");
          output.write(Message.ERROR.toString());
          break;
      }

      output.flush();

    } catch (IOException e) {
      System.out.println("[Server " + serverId + "] exception: " + e);
      e.printStackTrace();
    }
  }
}
