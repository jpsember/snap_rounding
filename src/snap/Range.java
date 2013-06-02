package snap;

/**
 * ADT for a vertical range of pixels
 */
public abstract class Range {

  /**
   * Determine if range includes a pixel
   * @param y : pixel to test
   * @return
   */
  public boolean contains(int y) {
    return y >= y0() && y <= y1();
  }

  /**
   * Get endpoint of range
   * @param upper : if true, returns y1(); else, y0()
   * @return int
   */
  public int y(boolean upper) {
    return upper ? y1() : y0();
  }
  /**
   * Get minimum pixel of range
   * @return
   */
  public abstract int y0();

  /**
   * Get maximum pixel of range
   * @return
   */
  public abstract int y1();

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int y0 = y0(), y1 = y1();
    sb.append(y0);
    if (y1 != y0) {
      sb.append("..");
      sb.append(y1);
    }
    sb.append("]");
    return sb.toString();
  }

  /**
   * Determine distance between two ranges
   * @param a
   * @param b
   * @return distance between ranges, or zero if they intersect
   * @deprecated
   */
  public static int distance(Range a, Range b) {

    int d = 0;
    d = a.y0() - b.y1();
    if (d <= 0) {
      d = b.y0() - a.y1();
      if (d <= 0)
        d = 0;
    }
    return d;
  }

  /**
   * Determine minimum distance between extreme points of two ranges
   * @param a
   * @param b
   * @return minimum distance between an endpoint of a and an endpoint of b
   * @deprecated
   */
  public static int distanceBetweenEndpoints(Range a, Range b) {
    return Math.min(Math.abs(a.y0() - b.y0()), Math.min(Math.abs(a.y1()
        - b.y0()), Math.min(Math.abs(a.y0() - b.y1()), Math
        .abs(a.y1() - b.y1()))));
  }

}
