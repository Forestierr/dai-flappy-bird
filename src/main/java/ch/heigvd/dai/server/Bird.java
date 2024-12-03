package ch.heigvd.dai.server;

public class Bird {
  private int x;
  private int y;
  private int velocity;

  /**
   * Constructor
   *
   * @param x : x position
   * @param y : y position
   */
  public Bird(int x, int y) {
    this.x = x;
    this.y = y;
    this.velocity = -1;
  }

  /**
   * Update the position of the bird
   */
  public void update() {
    if (velocity < 1) {
      velocity += 1;
    }
    y += velocity;
  }

  /**
   * Make the bird fly
   */
  public void fly() {
    velocity = -3;
  }

  /**
   * Get the x position of the bird
   *
   * @return x position
   */
  public int getX() {
    return x;
  }

  /**
   * Get the y position of the bird
   *
   * @return y position
   */
  public int getY() {
    return y;
  }
}
