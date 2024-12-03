// MIT License
//
// Copyright (c) 2024 Robin Forestier (Forestierr)
//                    Antoine Leresche (A2va)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package ch.heigvd.dai.commands;

import ch.heigvd.dai.core.Terminal;
import ch.heigvd.dai.utils.Key;
import ch.heigvd.dai.utils.Message;
import com.googlecode.lanterna.screen.Screen;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import picocli.CommandLine;

/**
 * The client command.
 */
@CommandLine.Command(
    name = "client",
    description = "Launch the client.",
    version = "1.0.0",
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Client implements Callable<Integer> {

  @CommandLine.Option(
      names = {"--host"},
      description = "The host to connect to.",
      defaultValue = "127.0.0.1",
      required = false,
      scope = CommandLine.ScopeType.INHERIT)
  private String host;

  private final Terminal terminal = new Terminal();
  private final Screen screen = terminal.getScreen();

  private BufferedReader input;
  private BufferedWriter output;

  private AtomicBoolean isDead = new AtomicBoolean(false);
  private int score = 0;
  private int bestScore = 0;

  /**
   * The call method is the main method of the client.
   * It's call by Root when the client command is executed.
   * @return 0 for success
   * @throws InterruptedException
   * @throws UnknownHostException
   * @throws IOException
   */
  @Override
  public Integer call() throws InterruptedException, UnknownHostException, IOException {
    terminal.checkSize();
    initConnection();

    return 0;
  }

  /**
   * The initConnection method is responsible for connecting to the server
   */
  private void initConnection() {
    terminal.print("Connecting to the server at " + host + " ...");
    terminal.refresh();

    try (Socket socket = new Socket(host, Root.getPort());
        BufferedReader input =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter output =
            new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

      this.input = input;
      this.output = output;

      welcome();

    } catch (Exception e) {
      System.out.println("Error while connecting to the server" + e);
      e.printStackTrace();
    }
  }

  /**
   * The welcome method is responsible for displaying the welcome screen and handling the user input
   * @throws IOException
   * @throws InterruptedException
   */
  private void welcome() throws IOException, InterruptedException {
    String msg;
    Message message;

    // Draw the welcome screen
    terminal.drawBackground();
    terminal.drawWelcome();
    terminal.refresh();

    // Wait for user input
    while (true) {
      Key k = Key.parseKeyStroke(screen.pollInput());
      if (k != Key.NONE) {
        if (k == Key.FLY) {
          // send START message to the server
          output.write(Message.START.toString());
          output.flush();

          msg = Message.readUntilEOT(input);
          message = Message.fromString(msg);

          if (message == Message.ACK) {
            // start a single player game
            gameLoop();
          }
          break;
        } else if (k == Key.MULTI) {
          // TODO : Play multiplayer
          terminal.print("Multiplayer not implemented yet.");
          terminal.refresh();
        } else if (k == Key.QUIT) {
          output.write(Message.QUIT.toString());
          output.flush();

          msg = Message.readUntilEOT(input);
          message = Message.fromString(msg);

          // If the server acknowledge the QUIT message, we can break the loop
          if (message == Message.ACK) {
            break;
          }
        }
      }
    }
  }

  /**
   * The gameOver method is responsible for displaying the game over screen and handling the user input
   * @throws IOException
   * @throws InterruptedException
   */
  private void gameOver() throws IOException, InterruptedException {
    String msg;
    Message message;

    // Draw the game over screen
    terminal.drawBackground();
    terminal.drawGameOver(score, bestScore);
    terminal.refresh();

    // Wait for user input
    while (true) {
      Key k = Key.parseKeyStroke(screen.pollInput());
      if (k != Key.NONE) {
        if (k == Key.FLY) {
          // send START message to the server (restart a new game)
          output.write(Message.START.toString());
          output.flush();

          msg = Message.readUntilEOT(input);
          message = Message.fromString(msg);

          if (message == Message.ACK) {
            // start a single player game
            gameLoop();
          } else if (message == Message.ERROR) {
            terminal.print("Error: " + message.getData());
            terminal.refresh();
          }
          break;
        } else if (k == Key.MULTI) {
          // TODO : Play multiplayer
          terminal.print("Multiplayer not implemented yet.");
          terminal.refresh();
        } else if (k == Key.QUIT) {
          output.write(Message.QUIT.toString());
          output.flush();

          msg = Message.readUntilEOT(input);
          message = Message.fromString(msg);

          if (message == Message.ACK) {
            break;
          } else if (message == Message.ERROR) {
            terminal.print("Error: " + message.getData());
            terminal.refresh();
          }
        }
      }
    }
  }

  /**
   * The gameLoop method is responsible for handling the game loop
   * @throws IOException
   * @throws InterruptedException
   */
  private void gameLoop() throws IOException, InterruptedException {
    isDead.set(false);

    Thread keyPoller =
        new Thread(
            () -> {
              while (true) {
                try {
                  Thread.sleep(100);

                  if (isDead.get()) {
                    break;
                  }

                  Key k = Key.parseKeyStroke(screen.pollInput());
                  if (k == Key.NONE) {
                    continue;
                  }

                  Message m = Message.FLY;
                  if (k == Key.FLY) {
                    m = Message.FLY;
                  } else if (k == Key.QUIT) {
                    m = Message.QUIT;
                    isDead.set(true);
                  }

                  // send FLY message to the server
                  output.write(m.toString());
                  output.flush();

                } catch (InterruptedException | IOException e) {
                  e.printStackTrace();
                }
              }
            });
    keyPoller.start();

    // Initial position of the bird
    int xBird = Terminal.SCREEN_MIN_HEIGHT / 2;
    int yBird = 6;

    while (true) {
      // read the DATA message from the server
      String msg = Message.readUntilEOT(input);
      Message message = Message.fromString(msg);

      // Dead or want to quit : show game over screen
      if (message == Message.DEAD || (message == Message.ACK && isDead.get())) {
        isDead.set(true);
        keyPoller.join();
        gameOver();
        break;
      } else if (message == Message.ERROR) {
        terminal.print("Error: " + message.getData());
        terminal.refresh();
        break;
      }

      terminal.drawBackground();

      if (message == Message.DATA) {
        // get the data from the message
        // For FLYY and PIPE commands it look like this: "DATA B x y P x y w ... P x y w S s "
        // where B stands for Bird and P for Pipe and S is for score.
        String data = message.getData();
        String[] parts = data.split(" ");

        for (int i = 0; i < parts.length; i++) {
          // get the x and y coordinates of the bird
          if (parts[i].equals("B")) {
            xBird = Integer.parseInt(parts[i + 1]);
            yBird = Integer.parseInt(parts[i + 2]);
          }

          // get the x, y and width of the pipe
          if (parts[i].equals("P")) {
            terminal.drawPipe(
                Integer.parseInt(parts[i + 1]),
                Integer.parseInt(parts[i + 2]),
                Integer.parseInt(parts[i + 3]));
          }

          // get the score
          if (parts[i].equals("S")) {
            score = Integer.parseInt(parts[i + 1]);
            bestScore = Math.max(score, bestScore);
            terminal.drawScore(score, bestScore);
          }
        }
      }

      terminal.drawBird(xBird, yBird);
      terminal.refresh();
      Thread.sleep(100);
    }
  }
}
