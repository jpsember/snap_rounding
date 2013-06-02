package base;

/**
 * Directed lines
 */
public class LineEqn implements IPlaneCurve {

  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof LineEqn))
      return false;
    LineEqn aux = (LineEqn) obj;
    return aux.a == a && aux.b == b && aux.c == c;
  }
  /**
   * Constructor
   * 
   * @param p0 : point on the line
   * @param dir : direction of line (polar angle)
   */
  public LineEqn(FPoint2 p0, double dir) {
    this(p0, MyMath.ptOnCircle(p0, dir, 1.0));
  }

  /**
   * Constructor, for line directed from p1 to p2
   * @param p1
   * @param p2
   */
  public LineEqn(FPoint2 p1, FPoint2 p2) {
    this(p1.x, p1.y, p2.x, p2.y);
  }

  /**
   * Constructor, for line satisfying equation Ax + By + C = 0
   * @param a 
   * @param b
   * @param c
   */
  public LineEqn(double a, double b, double c) {
    set(a, b, c);
  }

  public LineEqn(double x1, double y1, double x2, double y2) {
    final double NEARZERO = 1e-10;

    do {
      double origx1 = x1;
      double origy1 = y1;

      x2 -= x1;
      y2 -= y1;
      x1 = y1 = 0;

      // scale second endpoint so length is 1.0
      double len = x2 * x2 + y2 * y2;
      if (len < NEARZERO * NEARZERO)
        break;

      double scl = 1.0 / Math.sqrt(len);
      x2 *= scl;
      y2 *= scl;

      double A, B, C;
      double dx = x2;
      double dy = y2;

      if (Math.abs(dx) >= Math.abs(dy)) {
        B = 1;
        A = -dy / dx;

        if (dx < 0) {
          A = -A;
          B = -B;
        }
      } else {
        A = 1;
        B = -dx / dy;
        if (dy >= 0) {
          A = -A;
          B = -B;
        }
      }
      double s2 = 1.0 / Math.sqrt(A * A + B * B);
      A *= s2;
      B *= s2;
      C = -(A * origx1 + B * origy1);

      set(A, B, C);
    } while (false);

  }

  /**
   * Determine if line is well defined
   * @return true if so
   */
  public boolean defined() {
    return a != 0 || b != 0;
  }

  private boolean set(double A, double B, double C) {

    this.a = 0;
    this.b = 0;

    double mag = Math.sqrt(A * A + B * B);
    if (mag > 0) {
      double s = 1.0 / mag;
      this.a = A * s;
      this.b = B * s;
      this.c = C * s;

      p0 = pt(0);
      p1 = pt(1);

    }
    if (false) {
      Tools.warn("debug printing");
      Streams.out.println("A=" + A + "\nB=" + B + "\nC=" + C + "\np0=" + p0
          + "\np1=" + p1);
    }

    return defined();
  }

  /**
   * Calculate point based on parameter
   * @param t : parameter; 0 returns pt0(), 1 returns pt1()
   * @return point 
   */
  public FPoint2 pt(double t) {

    if (!defined())
      throw new IllegalStateException("line undefined");

    double x, y;

    if (Math.abs(b) >= .5) {
      x = b * t;
      y = -c / b - a * t;
    } else {
      x = -c / a + b * t;
      y = -a * t;
    }
    return new FPoint2(x, y);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LineEqn");
    if (defined()) {
      sb.append(" A=" + Tools.f(a));
      sb.append(" B=" + Tools.f(b));
      sb.append(" C=" + Tools.f(c));
      sb.append("\n p0=" + p0);
      sb.append("\n p1=" + p1);
    } else
      sb.append(" <undefined>");

    return sb.toString();
  }

  /**
   * Calculate distance of a point from a line
   * @param pt : point
   * @return distance of point from line
   */
  public double distanceFrom(FPoint2 pt) {
    return Math.abs(signedDistanceFrom(pt));
  }

  /**
   * Calculate signed distance of a point from a line
   * @param pt : point
   * @return 0 if point is on the line; +dist if to left; -dist if to right
   */
  public double signedDistanceFrom(FPoint2 pt) {
    if (!defined())
      throw new IllegalStateException("line undefined");
    double dx = a * pt.x;
    double dy = b * pt.y;
    double dz = c;
    return (dx + dy + dz);
  }

  /**
   * Calculate x coordinate of point with a particular y coordinate.
   * If line is vertical, returns 0
   * @param x
   * @return y-coordinate
   */
  public double yAt(double x) {
    double y = 0;
    if (b != 0) {
      y = (-a * x - c) / b;
    }
    return y;
  }

  /**
   * Calculate y coordinate of point with a particular x coordinate.
   * If line is horizontal, returns 0
   * @param y
   * @return x-coordinate
   */
  public double xAt(double y) {
    double x = 0;
    if (a != 0) {
      x = (-b * y - c) / a;
    }
    return x;
  }

  /**
   * Calculate closest point on line to an input point
   * @param p3
   * @return closest point to line
   */
  public FPoint2 closestPointTo(FPoint2 p3) {
    return pt(parameterFor(p3)); //calcClosestPointTo(p3));
  }

  //  /**
  //   * Get parameter of closest point on line to an input point
  //   * @param pt input point
  //   * @return parameter of closest point on line
  //   * @deprecated use parameterFor
  //   */
  //  public double calcClosestPointTo(FPoint2 pt) {
  //    return calcClosestPointTo(pt.x, pt.y);
  //  }

  /**
   * @deprecated use parameterFor
   */
  public double calcClosestPointTo(double x, double y) {
    return (x - p0.x) * (p1.x - p0.x) + (y - p0.y) * (p1.y - p0.y);
  }

  /**
   * Determine which side of a line a point is on
   * @param pt : point to test
   * @return 0 if the point is on the line,
   *  1 if it's to the left, -1 if to the right
   */
  public int sideOfLine(FPoint2 pt) {
    if (!defined())
      throw new IllegalStateException("line undefined");

    double area2 = (p1.x - p0.x) * (pt.y - p0.y) - (pt.x - p0.x)
        * (p1.y - p0.y);
    return (int) Math.signum(area2);
  }

  private double a, b, c;
  private FPoint2 p0, p1;

  public static FPoint2 intersection(LineEqn l0, LineEqn l1) {
    FPoint2 is = MyMath.linesIntersection(l0.p0, l0.p1, l1.p0, l1.p1, null);
    return is;
  }

  public double polarAngle() {
    if (!defined())
      throw new IllegalStateException("line undefined");
    return Math.atan2(-a, b);
  }

  public double slope() {
    if (!defined())
      throw new IllegalStateException("line undefined");
    return -a / b;
  }

  /**
   * Clip line to rectangle
   * @param r : rectangle to clip to
   * @return array containing min, max t, or null if line doesn't intersect rect
   */
  public double[] clipToRect(FRect r) {
    if (!defined())
      throw new IllegalStateException();
    double[] ret = null;

    FPoint2 pt0 = pt(-10000), pt1 = pt(10000);
    if (MyMath.clipSegmentToRect(pt0, pt1, r)) {
      ret = new double[2];
      double t0 = parameterFor(pt0);
      double t1 = parameterFor(pt1);
      ret[0] = Math.min(t0, t1);
      ret[1] = Math.max(t0, t1);
    }
    return ret;
  }
  public int degree() {
    return 1;
  }
  public double coeff(int n) {
    switch (n) {
    default:
      throw new IllegalArgumentException();
    case 2:
      return a;
    case 1:
      return b;
    case 0:
      return c;
    }
  }

  public double parameterFor(double x, double y) {
    return (x - p0.x) * (p1.x - p0.x) + (y - p0.y) * (p1.y - p0.y);
  }

  public double parameterFor(FPoint2 pt) {
    return parameterFor(pt.x, pt.y); //(pt);
  }

    public void render(double t0, double t1) {
      throw new UnsupportedOperationException();
    }

}
