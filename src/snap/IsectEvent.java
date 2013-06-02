package snap;

import base.*;

/**
 */
class IsectEvent {

  /**
   * Constructor.
   * Constructs an IsectEvent for two neighboring segments.
   *
   * @param left : the lower of the two segments
   */
  public IsectEvent(Segment left ) {

    if (left == null) {
      return;
    }
    Segment a = left;
    Segment b = a.neighbor(true);
    if (b == null) {
      return;
    }

    this.pair = BlackBox.constructFor(a, b);  

    if (pair.state() != BlackBox.NOT_PARALLEL || pair.lower() != pair.a())
      pair = null;

  }

  public boolean valid() {
    return pair != null;
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("icept[");
    sb.append(pair);
    sb.append("]");
    return sb.toString();
  }

  /**
   * Compare two heap data objects to see which is smaller
   * @param left Object
   * @param right Object
   * @return int : left - right, as in standard comparator interface
   */
  protected static IsectEvent smallerOf(IsectEvent a, IsectEvent b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    int out;
    if (!a.valid()) {
      out = 1;
    } else if (!b.valid()) {
      out = -1;
    } else {
      out = IPoint2.compare(a.pair.getIntersectionPixel(true), b.pair
          .getIntersectionPixel(true));
    }

    return (out <= 0 ? a : b);
  }

  public boolean occursWithin(SweepStrip s) {
    return s.stripX() == pair.getIntersectionPixel(true).x;
  }

  /**
   * Get pixel containing intersection
   * @param transformedSegs : if true, gets pixel corresponding to transformed
   *  segments
   * @return pixel of intersection
   */
  public IPoint2 iPt(boolean transformedSegs) {
    return pair.getIntersectionPixel(transformedSegs);
  }

  public Segment a() {
    return pair.a();
  }

  public Segment b() {
    return pair.b();
  }

  private BlackBox pair;
}
