package ch.heigvd.dai.server;

public class Pipe {
  private int x;
  private int y;
  private int space;

  public Pipe(int x, int y, int width) {
    this.x = x;
    this.y = y;
    this.space = space;
  }

  public void move() {
    x--;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getSpace() {
    return space;
  }
}
