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

  @Override
  public Integer call() throws InterruptedException, UnknownHostException, IOException {
    initConnection();

    return 0;
  }

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

  private void welcome() throws IOException, InterruptedException {
    String msg;
    Message message;

    isDead.set(false);

    terminal.drawBackground();
    terminal.drawWelcome();
    terminal.refresh();

    while (true) {
      Key k = Key.parseKeyStroke(screen.pollInput());
      if (k != Key.NONE) {
        if (k == Key.FLY) {
          // send START message to the server
          System.out.println("Sending START message to the server");
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
          break;
        }
      }
    }
  }

  private void gameOver() throws IOException, InterruptedException {
    String msg;
    Message message;

    terminal.drawBackground();
    terminal.drawGameOver(score);
    terminal.refresh();

    while (true) {
      Key k = Key.parseKeyStroke(screen.pollInput());
      if (k != Key.NONE) {
        if (k == Key.FLY) {
          // send START message to the server
          System.out.println("Sending START message to the server");
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
          break;
        }
      }
    }
  }

  private void gameLoop() throws IOException, InterruptedException {
    isDead.set(false);

    int xBird = 5;
    int yBird = 6;

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

    while (true) {

      // read the DATA message from the server
      String msg = Message.readUntilEOT(input);
      Message message = Message.fromString(msg);

      // SAD ...
      if (message == Message.DEAD) {
        isDead.set(true);
        output.flush();
        keyPoller.join();
        gameOver();
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

          if (parts[i].equals("P")) {
            terminal.drawPipe(
                Integer.parseInt(parts[i + 1]),
                Integer.parseInt(parts[i + 2]),
                Integer.parseInt(parts[i + 3]));
          }

          if (parts[i].equals("S")) {
            score = Integer.parseInt(parts[i + 1]);
            terminal.drawScore(score);
          }
        }
      }

      terminal.drawBird(xBird, yBird);
      terminal.refresh();
      Thread.sleep(100);
    }
  }
}
