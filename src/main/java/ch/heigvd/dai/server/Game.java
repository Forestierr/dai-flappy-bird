package ch.heigvd.dai.server;

import java.util.ArrayList;

public class Game {

  private int score;
  private Boolean isDead = false;
  private Bird bird;
  private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

  public Game() {
    bird = new Bird(10, 10);
    pipes.add(new Pipe(25, 10, 3));
    pipes.add(new Pipe(35, 10, 5));
    pipes.add(new Pipe(45, 10, 3));
    score = 0;
  }

  public void update() {
    for (Pipe pipe : pipes) {
      pipe.move();
    }

    bird.update();
    score++;

    if (checkCollision()) {
      isDead = true;
    }
  }

  public Boolean checkCollision() {
    // TODO : To verify write by copilot /!\ not tested
    for (Pipe pipe : pipes) {
      if (bird.getX() == pipe.getX()
          && (bird.getY() < pipe.getY() || bird.getY() > pipe.getY() + pipe.getSpace())) {
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
    sb.append(" S ").append(score);
    return sb.toString();
  }
}
