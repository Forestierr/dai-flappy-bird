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
import picocli.CommandLine;

@CommandLine.Command(
    name = "client",
    description = "Launch the client.",
    version = "1.0.0",
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Client implements Callable<Integer> {

  private final Terminal terminal = new Terminal();
  private final Screen screen = terminal.getScreen();

  private BufferedReader input;
  private BufferedWriter output;

  // TODO Add ip option

  @Override
  public Integer call() throws InterruptedException, UnknownHostException, IOException {

    Socket socket = new Socket("127.0.0.1", Root.getPort());

    try (BufferedReader input =
            new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter output =
            new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

      this.input = input;
      this.output = output;

      initConnection();

      // gameLoop();

    } catch (Exception e) {
      System.out.println("Error while connecting to the server" + e);
      System.out.println(e.getStackTrace());
      return 1;
    }

    return 0;
  }

  private void initConnection() throws IOException {
    terminal.print("Connecting to the server...");

    String msg = Message.readUntilEOT(this.input);
    Message message = Message.fromString(msg);

    if (message != Message.ACK) {
      throw new IOException("Cannot connect to server. ACK wasn't received");
    }

    terminal.drawBackground();
    terminal.drawWelcome();
    terminal.refresh();

    while (true) {
      Key k = Key.parseKeyStroke(screen.pollInput());
      if (k != Key.NONE) {
        if (k == Key.FLY) {
          // send START message to the server
          this.output.write(Message.START.toString());
          this.output.flush();

          System.out.println("Message sent: " + Message.START);

          msg = Message.readUntilEOT(this.input);
          message = Message.fromString(msg);

          System.out.println("Message received: " + message);

          if (message == Message.START) {
            // start a single player game
            System.out.println("Game LOOP");

            gameLoop();
          }
          break;
        } else if (k == Key.MULTI) {
          // TODO : Play multiplayer
        }
      }
    }
  }

  private void gameLoop() throws IOException {
    int xBird = 5;
    int yBird = 6;

    // send FLY message to the server
    System.out.println("Message sent: " + Message.FLY);
    this.output.write(Message.LIST.toString());
    this.output.flush();
    String msg;
    Message message;

    // wait for the server to send the first DATA
    while (true) {
      msg = Message.readUntilEOT(this.input);
      message = Message.fromString(msg);
      if (message == Message.DATA) {
        System.out.println("Message received: " + message);
        break;
      }
    }

    // TODO : while not dead
    while (true) {
      // read the DATA message from the server
      msg = Message.readUntilEOT(this.input);
      message = Message.fromString(msg);

      if (message == Message.ERROR) {
        System.out.println("Error while reading message from server " + message);
        continue;
      }

      System.out.println("Message received (gl): " + message);

      // SAD ...
      if (message == Message.DEAD) {
        break;
      }

      terminal.drawBackground();

      if (message == Message.DATA) {
        // get the data from the message
        // For FLYY and PIPE commands it look like this: "DATA B x y P x y w ... P x y w S s" where
        // B
        // stands for Bird and P for Pipe and S is for score.
        String data = message.getData();
        String[] parts = data.split(" ");
        // get the x and y coordinates of the bird
        if (parts[0].equals("B")) {
          xBird = Integer.parseInt(parts[1]);
          yBird = Integer.parseInt(parts[2]);
        }
        // get the x, y and width of the pipes
        for (int i = 3; i + 4 < parts.length; i += 4) {
          if (parts[i].equals("P")) {
            terminal.drawPipe(
                Integer.parseInt(parts[i + 1]),
                Integer.parseInt(parts[i + 2]),
                Integer.parseInt(parts[i + 3]));
          }
          if (parts[i].equals("S")) {
            terminal.drawScore(Integer.parseInt(parts[i + 1]));
          }
        }
      }

      terminal.drawBird(xBird, yBird);
      terminal.refresh();

      Key key = Key.parseKeyStroke(screen.pollInput());
      if (key != Key.NONE && key == Key.FLY) {
        // send FLY message to the server
        this.output.write(Message.FLY.toString());
      }
    }
  }
}
