package ch.heigvd.dai.server;

public class Pipe {
  private int x;
  private int y;
  private int space;

  /**
   * Constructor
   *
   * @param x x coordinate of the pipe
   * @param y y coordinate of the middle of the space between the two pipes
   * @param space space between the two pipes (up and down) in pixels
   */
  public Pipe(int x, int y, int space) {
    this.x = x;
    this.y = y;
    this.space = space;
  }

  /**
   * Move the pipe to the left by one pixel
   */
  public void move() {
    x--;
  }

  /**
   * get the x coordinate of the pipe
   *
   * @return x coordinate of the pipe
   */
  public int getX() {
    return x;
  }

  /**
   * get the y coordinate of the pipe
   *
   * @return y coordinate of the pipe
   */
  public int getY() {
    return y;
  }

  /**
   * get the space between the two pipes
   *
   * @return space between the two pipes
   */
  public int getSpace() {
    return space;
  }
}
