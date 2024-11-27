package ch.heigvd.dai.commands;

import picocli.CommandLine;

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

  public static int getPort() {
    return PORT;
  }
}
