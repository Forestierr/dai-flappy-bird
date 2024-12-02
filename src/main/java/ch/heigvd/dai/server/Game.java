package ch.heigvd.dai.server;

import java.util.ArrayList;

public class Game {

  private int score;
  private int frame = 0;
  private Boolean isDead = false;
  private Bird bird;
  private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

  public Game() {
    bird = new Bird(10, 10);
    pipes.add(new Pipe(65, 10, 7));
    pipes.add(new Pipe(80, 10, 7));
    score = 0;
  }

  public void update() {
    boolean delFlag = false;

    for (Pipe pipe : pipes) {
      pipe.move();
      // If the pipe is out of the screen, remove it
      if (pipe.getX() < 0) {
        delFlag = true;
        // pipes.remove(pipe);
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

    // Add a new pipe every 10 of score
    if (getFrame() == 20) {
      setFrame(0);
      // Get the last pipe to get the y position
      int y = pipes.get(pipes.size() - 1).getY();
      // Add a new pipe with a random space
      y = y + (int) (Math.random() * 10) - 5;
      if (y < 0) {
        y = 8;
      } else if (y > 20) {
        y = 12;
      }
      // randomize the space between the pipes from 7 to 9
      int space = 7 + (int) (Math.random() * 3);

      addPipe(79, y, space);
    }

    bird.update();

    if (checkCollision()) {
      isDead = true;
    }
  }

  public Boolean checkCollision() {
    for (Pipe pipe : pipes) {
      if (bird.getX() == pipe.getX()
          && (bird.getY() < pipe.getY() - (pipe.getSpace() / 2)
              || bird.getY() > pipe.getY() + (pipe.getSpace() / 2))) {
        System.out.println("Collision");
        return true;
      }

      if (bird.getY() < 0 || bird.getY() > 20) {
        System.out.println("Out of bounds");
        return true;
      }
    }
    return false;
  }

  public void fly() {
    bird.fly();
  }

  public void addPipe(int x, int y, int space) {
    pipes.add(new Pipe(x, y, space));
  }

  public Boolean isDead() {
    return isDead;
  }

  public String toString() {
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

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }
}
