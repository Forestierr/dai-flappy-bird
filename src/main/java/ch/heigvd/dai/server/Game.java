package ch.heigvd.dai.server;

import java.util.ArrayList;

public class Game {

  private int score = 0;
  private Bird bird;
  private ArrayList<Pipe> pipes = new ArrayList<Pipe>();

  public Game() {
    bird = new Bird(10, 10);
    pipes.add(new Pipe(15, 15, 5));
    pipes.add(new Pipe(20, 15, 5));
    pipes.add(new Pipe(24, 15, 5));
  }

  public void update() {
    for (Pipe pipe : pipes) {
      pipe.move();
    }
    bird.update();
    score++;

    if (checkCollision()) {
      // DEAD
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
