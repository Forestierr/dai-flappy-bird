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

  @Override
  public Integer call() throws InterruptedException {

    /* TODO :
     *  Init the connection with the server
     *  (Create a lobby, join a lobby, etc.)
     *  Start the game
     *  Display the game / handle the inputs
     */
    initConnection();

    Thread.sleep(5000);

    return 0;
  }

  private void initConnection() {

    terminal.print("Connecting to the server...");

    terminal.drawBackground();
    terminal.drawPipe(10, 5, 5);
    terminal.drawPipe(20, 8, 5);
    terminal.drawPipe(30, 6, 5);
    terminal.drawPipe(40, 7, 5);
    terminal.drawBird(5, 6);

    terminal.refresh();
  }
}
