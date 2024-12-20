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

  public static final int SCREEN_MIN_WIDTH = 80;
  public static final int SCREEN_MIN_HEIGHT = 20;

  /** Constructor */
  public Terminal() {
    try {
      terminal = new DefaultTerminalFactory().createTerminal();
      screen = new TerminalScreen(terminal);
      screen.startScreen();
      // turn off the cursor
      screen.setCursorPosition(null);
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

  /** Check if the terminal is big enough If not, display a message to the user */
  public void checkSize() {
    try {
      // get terminal size
      int width = getWidth();
      int height = getHeight();

      while (height < SCREEN_MIN_HEIGHT || width < SCREEN_MIN_WIDTH) {
        text.putString(0, 0, "Terminal too small");
        text.putString(0, 1, "Please resize the terminal");
        text.putString(0, 2, "Terminal size : " + width + "x" + height);
        text.putString(0, 3, "Minimum size : " + SCREEN_MIN_WIDTH + "x" + SCREEN_MIN_HEIGHT);
        width = getWidth();
        height = getHeight();
        screen.refresh();

        Thread.sleep(100);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Print a string on the terminal
   *
   * @param s the string to print
   */
  public void print(String s) {
    // get all lines from the string
    String[] lines = s.split("\n");

    // display the string
    for (int i = 0; i < lines.length; i++) {
      text.putString(0, i, lines[i]);
    }

    try {
      screen.refresh();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Draw the background */
  public void drawBackground() {
    // draw the ground (BLUE)
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    for (int i = 0; i < SCREEN_MIN_HEIGHT; i++) {
      for (int j = 0; j < SCREEN_MIN_WIDTH; j++) {
        text.putString(j, i, " ");
      }
    }

    // draw the grass (GREEN)
    text.setBackgroundColor(TextColor.ANSI.GREEN);
    text.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
    for (int i = 0; i < SCREEN_MIN_WIDTH; i++) {
      text.putString(i, SCREEN_MIN_HEIGHT - 1, "X");
    }

    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /** Draw the welcome screen */
  public void drawWelcome() {
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    text.setForegroundColor(TextColor.ANSI.BLACK);

    text.putString((SCREEN_MIN_WIDTH / 2) - 15, 5, "   Welcome to Flappy Bird     ", SGR.BOLD);
    text.putString((SCREEN_MIN_WIDTH / 2) - 15, 10, " Press \"SPACE BAR\" to play  ", SGR.BOLD);
    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 15, 12, "Or press \"m\" for multiplayer", SGR.CROSSED_OUT);

    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 17, 18, "Robin Forestier & Antoine Leresche", SGR.ITALIC);

    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /**
   * Draw the game over screen
   *
   * @param score the score to display
   */
  public void drawGameOver(int score, int bestScore) {
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    text.setForegroundColor(TextColor.ANSI.RED_BRIGHT);

    text.putString((SCREEN_MIN_WIDTH / 2) - 15, 3, "         Game Over           ", SGR.BOLD);
    text.setForegroundColor(TextColor.ANSI.BLACK);
    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 15,
        6,
        "         Score : " + score + "         ",
        SGR.BLINK,
        SGR.BOLD);
    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 15, 8, "      Best Score : " + bestScore + "     ", SGR.BOLD);
    text.putString((SCREEN_MIN_WIDTH / 2) - 15, 12, " Press \"SPACE BAR\" to play  ", SGR.BOLD);
    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 15, 14, "Or press \"m\" for multiplayer", SGR.CROSSED_OUT);

    text.putString(
        (SCREEN_MIN_WIDTH / 2) - 17, 18, "Robin Forestier & Antoine Leresche", SGR.ITALIC);

    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /**
   * Draw the bird
   *
   * @param x the x position of the bird
   * @param y the y position of the bird
   */
  public void drawBird(int x, int y) {
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    text.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
    text.putString(x, y, "@>", SGR.BOLD);
    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /**
   * Draw a pipe
   *
   * @param x the x position of the pipe
   * @param y the y position of the middle of pipe
   * @param space the space between the top and bottom pipe
   */
  public void drawPipe(int x, int y, int space) {
    text.setForegroundColor(TextColor.ANSI.GREEN);

    // draw the top pipe
    for (int i = 1; i < y - (space / 2); i++) {
      text.putString(x, i, "█");
    }

    // draw the bottom pipe
    for (int i = y + (space / 2); i < SCREEN_MIN_HEIGHT - 1; i++) {
      text.putString(x, i, "█");
    }

    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /**
   * Draw the score
   *
   * @param score the score to display
   */
  public void drawScore(int score, int bestScore) {
    text.setBackgroundColor(TextColor.ANSI.BLUE_BRIGHT);
    text.setForegroundColor(TextColor.ANSI.BLACK);
    text.putString(1, 0, "Score : " + score, SGR.BOLD);
    text.putString(13, 0, "Best Score : " + bestScore, SGR.BOLD);
    text.setBackgroundColor(TextColor.ANSI.DEFAULT);
    text.setForegroundColor(TextColor.ANSI.DEFAULT);
  }

  /** Refresh the screen */
  public void refresh() {
    try {
      screen.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the screen
   *
   * @return the screen
   */
  public Screen getScreen() {
    return screen;
  }

  /** Close the terminal */
  public void close() {
    try {
      screen.stopScreen();
      terminal.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the width of the terminal
   *
   * @return the width of the terminal
   */
  public int getWidth() {
    return screen.getTerminalSize().getColumns();
  }

  /**
   * Get the height of the terminal
   *
   * @return the height of the terminal
   */
  public int getHeight() {
    return screen.getTerminalSize().getRows();
  }
}
