package ch.heigvd.dai.server;

import ch.heigvd.dai.utils.Message;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

public class ClientHandler implements Runnable {

  private final Socket socket;
  private int serverId;
  private Game game;

  private Semaphore mutex = new Semaphore(1);

  /**
   * Constructor
   *
   * @param socket the socket
   * @param serverId the server id
   */
  public ClientHandler(Socket socket, int serverId) {
    this.socket = socket;
    this.serverId = serverId;
  }

  /** Run the client handler */
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
      send(Message.ACK, output);

      Message message = Message.fromString(Message.readUntilEOT(input));

      switch (message) {
        case START:
          break;
        case QUIT:
          System.out.println("[Server " + serverId + "] received QUIT message");
          send(Message.ACK, output);
          return;
        default:
          System.out.println("[Server " + serverId + "] received invalid message");
          Message error = Message.ERROR;
          error.setData("Expected START message");
          send(error, output);
          return;
      }

      System.out.println("[Server " + serverId + "] received STRT message");
      // Create a new game
      game = new Game();
      game.update();
      send(Message.ACK, output);

      Thread gameThread =
          new Thread(
              () -> {
                System.out.println("[Server " + serverId + "] Game thread started");
                while (true) {
                  try {
                    Thread.sleep(100);
                    game.update();
                    if (game.isDead()) {
                      System.out.println("[Server " + serverId + "] Game over");
                      send(Message.DEAD, output);
                      break;
                    }

                    Message data = Message.DATA;
                    data.setData(game.toString());
                    send(data, output);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              });
      gameThread.start();

      // Read messages from the client while the socket is open
      // TODO: because the server does not support 2 player mode, some messages are not correctly
      // implemented yet.
      while (!socket.isClosed()) {
        message = Message.fromString(Message.readUntilEOT(input));

        switch (message) {
          case START:
            System.out.println("[Server " + serverId + "] received STRT message");
            send(Message.ACK, output);
            game.reset();
            gameThread = new Thread(gameThread);
            gameThread.start();
            break;
          case FLY:
            game.fly();
            System.out.println("[Server " + serverId + "] received FLY message");
            break;
          case JOIN:
            System.out.println("[Server " + serverId + "] received LOBY message");
            if (message.getData() != null) {
              System.out.println(
                  "[Server "
                      + serverId
                      + "] received LOBY message with data: "
                      + message.getData());
              System.out.println("[Server " + serverId + "] Join lobby " + message.getData());
              send(Message.DATA, output);
            } else {
              System.out.println("[Server " + serverId + "] received LOBY message without data");
              System.out.println("[Server " + serverId + "] Create lobby");
              send(Message.DATA, output);
            }
            break;
          case LIST:
            System.out.println("[Server " + serverId + "] received LIST message");
            send(Message.DATA, output);
            break;
          case PIPE:
            System.out.println("[Server " + serverId + "] received PIPE message");
            send(Message.DATA, output);
            break;
          case QUIT:
            System.out.println("[Server " + serverId + "] received QUIT message");
            // set is dead to true to stop the game thread
            game.setDead(true);
            gameThread.join();
            send(Message.ACK, output);
            break;
          default:
            System.out.println("[Server " + serverId + "] received unknown message");
            Message error = Message.ERROR;
            error.setData("Unknown message");
            send(Message.ERROR, output);
            break;
        }
      }

      System.out.println("[Server " + serverId + "] Client disconnected");

    } catch (IOException | InterruptedException e) {
      System.out.println("[Server " + serverId + "] exception: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Send a message to the client Concurrent access to the output stream is controlled by a mutex
   *
   * @param message the message
   * @param output the output
   */
  private void send(Message message, BufferedWriter output) {
    try {
      mutex.acquire();
      output.write(message.toString());
      output.flush();
    } catch (InterruptedException | IOException e) {
      System.out.println("[Server " + serverId + "] exception: " + e);
      e.printStackTrace();
    } finally {
      mutex.release();
    }
  }
}
