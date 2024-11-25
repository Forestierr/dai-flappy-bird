package ch.heigvd.dai.utils;

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

  Message(String message) {
    this.message = message;
  }

  public void setData(String data) {
    if (this == Message.PIPE || this == Message.DATA || this == Message.JOIN) {
      this.data = data;
    }
    throw new IllegalArgumentException(message + " cannot carry data");
  }

  public String getData() {
    return data;
  }

  public static Message fromString(String message) {
    String msg = message.substring(0, 3);

    for (Message m : Message.values()) {
      if (m.message.equals(msg)) {
        if (m == Message.PIPE || m == Message.DATA || m == Message.JOIN) {
          m.setData(message.substring(3));
        }
        return m;
      }
    }
    throw new IllegalArgumentException("No constant with text " + message + " found");
  }

  @Override
  public String toString() {
    return super.toString() + SEPARATOR + message + EOT;
  }
}
