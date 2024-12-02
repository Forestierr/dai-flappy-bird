package ch.heigvd.dai.utils;

import com.googlecode.lanterna.screen.Screen;
import java.awt.event.KeyListener;

public class KeyPoller extends Thread {

  private Screen screen;
  private KeyListener listener;

  public KeyPoller(Screen screen, KeyListener listener) {
    this.listener = listener;
    this.screen = screen;
  }

  public void run() {
    while (true) {
      try {
        if (listener != null) {
          listener.onKeyPressed(Key.parseKeyStroke(screen.readInput()));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public interface KeyListener {
    void onKeyPressed(Key key);
  }
}
