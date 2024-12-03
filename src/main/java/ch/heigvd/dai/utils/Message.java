package ch.heigvd.dai.utils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * This class represents the messages that can be exchanged between the server and the client.
 */
public enum Message {
  START("STRT"),
  FLY("FLYY"),
  PIPE("PIPE"),
  JOIN("JOIN"),
  LIST("LIST"),
  QUIT("QUIT"),
  DATA("DATA"),
  ERROR("EROR"),
  DEAD("DEAD"),
  ACK("ACKK");

  private static final String SEPARATOR = " ";
  private static final char EOT = 0x04;

  private String message;
  private String data;

  /**
   * Constructor
   *
   * @param message message
   */
  Message(String message) {
    this.message = message;
  }

  /**
   * Set the data for the message
   *
   * @param data data to communicate
   * @return the message
   */
  public Message setData(String data) {
    if (this == Message.PIPE || this == Message.DATA || this == Message.JOIN) {
      this.data = data;
      return this;
    }
    throw new IllegalArgumentException(message + " cannot carry data");
  }

  /**
   * Get the data of the message
   *
   * @return the data
   */
  public String getData() {
    return data;
  }

  /**
   * Convert the message to a string ready to be sent
   *
   * @return the message as a string
   */
  @Override
  public String toString() {
    if (data == null) {
      return message + EOT;
    }
    return message + SEPARATOR + data + EOT;
  }

  /**
   * Convert a string to a message
   *
   * @param message message as a string
   * @return the message
   */
  public static Message fromString(String message) {
    if (message.length() < 4) {
      return Message.ERROR;
    }
    String msg = message.substring(0, 4);

    for (Message m : Message.values()) {
      if (m.message.equals(msg)) {
        if (m == Message.PIPE || m == Message.DATA || m == Message.JOIN) {
          m.setData(message.substring(4, message.length() - 1));
        }
        return m;
      }
    }
    throw new IllegalArgumentException("No constant with text " + message + " found");
  }

  /**
   * Read the message until the end of transmission
   *
   * @param reader reader
   * @return the message
   * @throws IOException
   */
  public static String readUntilEOT(BufferedReader reader) throws IOException {
    StringBuilder response = new StringBuilder();
    int c;

    while ((c = reader.read()) != -1) {
      if (c == EOT) {
        break;
      }
      response.append((char) c);
    }

    return response.toString();
  }
}
