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

import picocli.CommandLine;


/**
 * The root command.
 */
@CommandLine.Command(
    description = "A flappy bird like game.",
    version = "1.0.0",
    subcommands = {
      Client.class,
      Server.class,
    },
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Root {
  private static int PORT = 2000;

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Override the default port.",
      defaultValue = "2000",
      scope = CommandLine.ScopeType.INHERIT)
  public void setPort(int port) {
    PORT = port;
  }

  /**
   * Get the port.
   * @return the port.
   */
  public static int getPort() {
    return PORT;
  }
}
