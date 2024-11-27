package ch.heigvd.dai.utils;

import com.googlecode.lanterna.input.KeyStroke;

/** Enumeration of the different keys that can be pressed by the user during the game. */
public enum Key {
  UP,
  DOWN,
  QUIT,
  ENTER,
  FLY,
  MULTI,
  NONE;

  /**
   * Parse a KeyStroke into a KEY
   *
   * @param key the KeyStroke to parse
   * @return the corresponding KEY
   */
  public static Key parseKeyStroke(KeyStroke key) {
    if (key == null) {
      return Key.NONE;
    }
    return switch (key.getKeyType()) {
      case ArrowUp -> Key.UP;
      case ArrowDown -> Key.DOWN;
      case Enter -> Key.ENTER;
      case Escape -> Key.QUIT;
      case Character -> // TODO : ADD toLowerCase() to the key.getCharacter()
          switch (key.getCharacter()) {
            case 'q' -> Key.QUIT;
            case 'm' -> Key.MULTI;
            case ' ' -> Key.FLY;
            default -> Key.NONE;
          };
      default -> Key.NONE;
    };
  }
}
