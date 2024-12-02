package ch.heigvd.dai.server;

public class Bird {
  private int x;
  private int y;
  private int velocity;

  public Bird(int x, int y) {
    this.x = x;
    this.y = y;
    this.velocity = 0;
  }

  public void update() {
    if (velocity < 1) {
      velocity += 1;
    }
    y += velocity;
  }

  public void fly() {
    velocity = -3;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
