package base;

import java.awt.*;
import java.util.Comparator;

public class IPoint2 extends Point implements IVector {

  public IPoint2() {
  }

  public void set(int x, int y) {
    super.setLocation(x, y);
  }

  /**
   * Lexicographically compare two points
   * 
   * @param p0
   *          Point
   * @param p1
   *          Point
   * @return zero if points are equal, < 0 if p0 precedes p1, > 0 if p0 follows
   *         p1
   */
  public static int compare(Point p0, Point p1) {
    return compare(p0.x, p0.y, p1.x, p1.y);
  }

  /**
   * Lexicographically compare two points
   * 
   * @return zero if points are equal, < 0 if p0 precedes p1, > 0 if p0 follows
   *         p1
   */
  public static int compare(int x0, int y0, int x1, int y1) {
    int out = x0 - x1;
    if (out == 0) {
      out = y0 - y1;
    }
    return out;
  }

  /**
   * Calculate cross product as vector p0..p1 rotates to p0..p2
   * 
   * @param p0
   *          Point
   * @param p1
   *          Point
   * @param p2
   *          Point
   * @return int
   */
  public static int crossProduct(Point p0, Point p1, Point p2) {
    int x1 = p1.x - p0.x, y1 = p1.y - p0.y, x2 = p2.x - p0.x, y2 = p2.y - p0.y;
    return (x1 * y2) - (y1 * x2);
  }

  public static boolean equals(Point p0, Point p1) {
    return compare(p0, p1) == 0;
  }

  public IPoint2(int x, int y) {
    super(x, y);
  }

  public IPoint2(Point pt) {
    super(pt.x, pt.y);
  }

  public IPoint2(double x, double y) {
    this((int) Math.round(x), (int) Math.round(y));
  }

  public IPoint2(FPoint2 pt) {
    this(pt.x, pt.y);

  }

  public static IPoint2 construct(IVector p0) {
    return new IPoint2(p0.x(), p0.y());
  }

  /**
   * Get string describing object
   * 
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    write(sb, this);
    return sb.toString();

  }

  public static final Comparator comparator = new Comparator() {
    public int compare(Object object, Object object1) {
      return IPoint2.compare((Point) object, (Point) object1);
    }
  };

  public double get(int row) {
    return (row == 0) ? x : y;
  }

  public double length() {
    return 2;
  }

  public double lengthSq() {
    double xf = x;
    double yf = y;
    return xf * xf + yf * yf;
  }

  public void negate() {
    x = -x;
    y = -y;
  }

  public double normalize() {
    throw new UnsupportedOperationException();
  }

  public void scale(double s) {
    throw new UnsupportedOperationException();
  }

  public void set(int index, double v) {
    if (index == 0)
      setX(v);
    else {
      if (index != 1)
        throw new IllegalArgumentException();
      setY(v);
    }
  }

  public void setTo(IVector v) {
    x = (int) v.x();
    y = (int) v.y();
  }

  public void setX(double x) {
    this.x = (int) x;
  }

  public void setY(double y) {
    this.y = (int) y;
  }

  public void setZ(double z) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    return 2;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    x = y = 0;
  }

  public double[] coeff() {
    throw new UnsupportedOperationException();
  }

  public double get(int y, int x) {
    return (y == 1 ? this.y : this.x);
  }

  public int height() {
    return 2;
  }

  public void set(int y, int x, double v) {
    set(y, v);
  }

  public int width() {
    return 1;
  }

  public static String toString(int x, int y) {
    return new IPoint2(x, y).toString();
  }

  public static String toString(int ax0, int y0, int ax1, int y1) {
    StringBuilder sb = new StringBuilder();
    toString(sb, ax0, y0);
    sb.append('-');
    toString(sb, ax1, y1);
    return sb.toString();
  }

  public static void toString(StringBuilder sb, IPoint2 pt) {
    toString(sb, pt.x, pt.y);
  }
  public static void toString(StringBuilder sb, int x, int y) {
    sb.append("(");
    sb.append(x);
    sb.append(",");
    sb.append(y);
    sb.append(")");
  }

  public static String toString(IPoint2 a0, IPoint2 a1) {
    return toString(a0.x, a0.y, a1.x, a1.y);
  }

  /**
   * Utility function: write object to text file, suitable for parsing
   * @param sb : StringBuilder to write to
   * @param pt : FPoint2
   */
  public static void write(StringBuilder sb, IPoint2 pt) {
    Tools.addSp(sb);
    sb.append(pt.x); //Tools.f(pt.x));
    Tools.addSp(sb);
    sb.append(pt.y); //Tools.f(pt.y));
    Tools.addSp(sb);
    }
}
