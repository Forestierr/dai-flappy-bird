package ch.heigvd.dai.server;

import ch.heigvd.dai.core.Terminal;
import java.util.ArrayList;

public class Game {

  private int score;
  private int frame = 0;
  private boolean isDead = false;
  private Bird bird;
  private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

  public Game() {
    bird = new Bird(Terminal.SCREEN_MIN_HEIGHT / 2, 6);
    pipes.add(new Pipe(Terminal.SCREEN_MIN_WIDTH - 20, Terminal.SCREEN_MIN_HEIGHT / 2, 7));
    pipes.add(new Pipe(Terminal.SCREEN_MIN_WIDTH, Terminal.SCREEN_MIN_HEIGHT / 2, 7));
    score = 0;
  }

  public synchronized void update() {
    boolean delFlag = false;

    for (Pipe pipe : pipes) {
      pipe.move();
      // If the pipe is out of the screen, remove it
      if (pipe.getX() < 0) {
        delFlag = true;
      }

      // If the pipe is 1x away from the bird, add 1 to the score
      if (pipe.getX() == bird.getX() + 1) {
        score++;
      }
    }

    if (delFlag) {
      pipes.remove(0);
    }

    setFrame(getFrame() + 1);

    // Add a new pipe every 20 of frame
    if (getFrame() == 20) {
      setFrame(0);
      // Get the last pipe to get the y position
      int y = pipes.get(pipes.size() - 1).getY();
      // Add a new pipe with a random space
      y = y + (int) (Math.random() * 10) - 5;
      if (y < 0) {
        y = 8;
      } else if (y > Terminal.SCREEN_MIN_HEIGHT) {
        y = 12;
      }
      // randomize the space between the pipes from 7 to 9
      int space = 7 + (int) (Math.random() * 3);

      addPipe(Terminal.SCREEN_MIN_WIDTH - 1, y, space);
    }

    bird.update();

    if (checkCollision()) {
      isDead = true;
    }
  }

  private boolean checkCollision() {
    for (Pipe pipe : pipes) {
      if (bird.getX() == pipe.getX()
          && (bird.getY() < pipe.getY() - (pipe.getSpace() / 2)
              || bird.getY() > pipe.getY() + (pipe.getSpace() / 2))) {
        return true;
      }

      // Check if the bird is out of the screen
      // screen height is 20 and we check if the bird touch the grass
      if (bird.getY() < 0 || bird.getY() > Terminal.SCREEN_MIN_HEIGHT - 2) {
        return true;
      }
    }
    return false;
  }

  public synchronized void fly() {
    bird.fly();
  }

  private void addPipe(int x, int y, int space) {
    pipes.add(new Pipe(x, y, space));
  }

  public synchronized Boolean isDead() {
    return isDead;
  }

  public synchronized void setDead(Boolean dead) {
    isDead = dead;
  }

  public synchronized String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("B ").append(bird.getX()).append(" ").append(bird.getY());
    for (Pipe pipe : pipes) {
      sb.append(" P ")
          .append(pipe.getX())
          .append(" ")
          .append(pipe.getY())
          .append(" ")
          .append(pipe.getSpace());
    }
    sb.append(" S ").append(score).append(" ");
    return sb.toString();
  }

  public void reset() {
    pipes.clear();
    bird = new Bird(Terminal.SCREEN_MIN_HEIGHT / 2, 6);
    pipes.add(new Pipe(Terminal.SCREEN_MIN_WIDTH - 20, Terminal.SCREEN_MIN_HEIGHT / 2, 7));
    pipes.add(new Pipe(Terminal.SCREEN_MIN_WIDTH, Terminal.SCREEN_MIN_HEIGHT / 2, 7));
    score = 0;
    frame = 0;
    isDead = false;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }
}
