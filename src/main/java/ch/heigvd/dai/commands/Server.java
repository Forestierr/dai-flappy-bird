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

import ch.heigvd.dai.server.ClientHandler;
import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import picocli.CommandLine;

/** The server command. */
@CommandLine.Command(
    name = "server",
    description = "Launch the server.",
    version = "1.0.0",
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Server implements Callable<Integer> {
  @CommandLine.ParentCommand protected Root parent;

  private static final int SERVER_ID = (int) (Math.random() * 1000000);

  @Override
  public Integer call() {
    // Start the server
    try (ServerSocket serverSocket = new ServerSocket(parent.getPort());
        ExecutorService executor = Executors.newCachedThreadPool(); ) {
      System.out.println("[Server " + SERVER_ID + "] starting with id " + SERVER_ID);
      System.out.println("[Server " + SERVER_ID + "] listening on port " + Root.getPort());

      // Accept clients
      while (!serverSocket.isClosed()) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("[Server " + SERVER_ID + "] new client connected");
        executor.submit(new ClientHandler(clientSocket, SERVER_ID));
      }
    } catch (IOException e) {
      System.out.println("[Server " + SERVER_ID + "] exception: " + e);
      e.printStackTrace();
      return 1;
    }

    return 0;
  }
}
