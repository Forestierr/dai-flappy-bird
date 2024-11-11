package ch.heigvd.dai.core;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;

public class Terminal {
  private com.googlecode.lanterna.terminal.Terminal terminal;
  private Screen screen = null;
  private TextGraphics text;

  private final int SCREEN_MIN_WIDTH = 80;
  private final int SCREEN_MIN_HEIGHT = 24;

  public Terminal() {
    try {
      terminal = new DefaultTerminalFactory().createTerminal();
      screen = new TerminalScreen(terminal);
      screen.startScreen();
      terminal.addResizeListener(
          new TerminalResizeListener() {
            @Override
            public void onResized(
                com.googlecode.lanterna.terminal.Terminal terminal, TerminalSize terminalSize) {
              screen.doResizeIfNecessary();
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
    text = screen.newTextGraphics();
  }

  public void print(String s) {
    try {
      // turn off cursor
      screen.setCursorPosition(null);
      // get all lines from the string
      String[] lines = s.split("\n");
      // get terminal size
      int width = screen.getTerminalSize().getColumns();
      int height = screen.getTerminalSize().getRows();

      while (height < SCREEN_MIN_HEIGHT || width < SCREEN_MIN_WIDTH) {
        text.putString(0, 0, "Terminal too small");
        text.putString(0, 1, "Please resize the terminal");
        text.putString(0, 2, "Terminal size : " + width + "x" + height);
        text.putString(0, 3, "Minimum size : " + SCREEN_MIN_WIDTH + "x" + SCREEN_MIN_HEIGHT);
        width = screen.getTerminalSize().getColumns();
        height = screen.getTerminalSize().getRows();
        screen.refresh();

        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      // display the string
      for (int i = 0; i < lines.length; i++) {
        text.putString(0, i, lines[i]);
      }

      screen.refresh();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void drawBackground() {
    int width = screen.getTerminalSize().getColumns();
    int height = screen.getTerminalSize().getRows();

    // draw the ground (BLUE)
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        text.putString(j, i, " ");
      }
    }

    // draw the grass (GREEN)
    text.setBackgroundColor(TextColor.ANSI.GREEN);
    text.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
    for (int i = 0; i < width; i++) {
      text.putString(i, height - 1, "X");
    }

    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  public void drawBird(int x, int y) {
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    text.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
    text.putString(x, y, "@>");

    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  public void drawPipe(int x, int y, int space) {
    text.setForegroundColor(TextColor.ANSI.GREEN);

    // draw the top pipe
    for (int i = 0; i < y - (space / 2); i++) {
      text.putString(x, i, "█");
    }

    // draw the bottom pipe
    for (int i = y + (space / 2); i < screen.getTerminalSize().getRows() - 1; i++) {
      text.putString(x, i, "█");
    }

    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  public void refresh() {
    try {
      screen.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      screen.stopScreen();
      terminal.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
