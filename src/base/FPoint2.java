package base;

import java.util.*;

public class FPoint2 extends java.awt.geom.Point2D.Double implements IVector {
  public static FPoint2 add(FPoint2 a, FPoint2 b, FPoint2 d) {
    if (d == null)
      d = new FPoint2();
    d.x = a.x + b.x;
    d.y = a.y + b.y;
    return d;
  }

  public FPoint2(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public FPoint2 add(FPoint2 pt) {
    return add(pt.x, pt.y);
  }

  public FPoint2 add(double x, double y) {
    this.x += x;
    this.y += y;
    return this;
  }

  public static FPoint2 difference(FPoint2 b, FPoint2 a, FPoint2 d) {
    if (d == null)
      d = new FPoint2();
    d.set(b.x - a.x, b.y - a.y);
    return d;
  }

  public static void addMultiple(FPoint2 a, double mult, FPoint2 b, FPoint2 dest) {
    dest.x = a.x + mult * b.x;
    dest.y = a.y + mult * b.y;
  }

  public FPoint2 subtract(double x, double y) {
    this.x -= x;
    this.y -= y;
    return this;
  }

  public double lengthSq() {
    return x * x + y * y;
  }

  public double length() {
    return Math.sqrt(lengthSq());
  }

  public double normalize() {
    double lenSq = lengthSq();
    if (lenSq != 0 && lenSq != 1) {
      lenSq = Math.sqrt(lenSq);
      double scale = 1 / lenSq;
      x *= scale;
      y *= scale;
    }
    return lenSq;
  }

  public FPoint2 subtract(FPoint2 pt) {
    return subtract(pt.x, pt.y);
  }

  public FPoint2(IVector src) {
    this.x = src.x();
    this.y = src.y();
  }

  public static FPoint2 interpolate(FPoint2 p1, FPoint2 p2, double t) {
    return new FPoint2(p1.x + t * (p2.x - p1.x), p1.y + t * (p2.y - p1.y));
  }

  public static FPoint2 midPoint(FPoint2 p1, FPoint2 p2) {
    return interpolate(p1, p2, .5);
    //  return new FPoint2(.5 * (p1.x+p2.x),.5*(p1.y+p2.y));
  }

  public boolean isValid() {

    return !(java.lang.Double.isInfinite(x) || java.lang.Double.isInfinite(y)
        || java.lang.Double.isNaN(x) || java.lang.Double.isNaN(y));
  }

  /**
   * Align point to a grid
   * @param gridSize : size of grid
   */
  public void alignToGrid(double gridSize) {
    alignToGrid(gridSize, gridSize);
  }
  public void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Align point to a grid
   * @param pixelWidth width of pixels in grid
   * @param pixelHeight height of pixels in grid
   */
  public void alignToGrid(double pixelWidth, double pixelHeight) {
    double iGrid = 1 / pixelWidth;
    x = Math.round(x * iGrid) * pixelWidth;
    iGrid = 1 / pixelHeight;
    y = Math.round(y * iGrid) * pixelHeight;
  }

  public FPoint2() {
  }

  public boolean clamp(double x0, double y0, double x1, double y1) {
    boolean valid = true;
    if (x < x0 || x > x1) {
      valid = false;
      x = MyMath.clamp(x, x0, x1);
    }
    if (y < y0 || y > y1) {
      valid = false;
      y = MyMath.clamp(y, y0, y1);
    }
    return valid;
  }

  //  public boolean clamp(FRect r) {
  //    return clamp(r.x, r.y, r.x + r.width, r.y + r.height);
  //  }
  public static double distance(FPoint2 a, FPoint2 b) {
    return distance(a.x, a.y, b.x, b.y);
  }

  public static double distanceSquared(FPoint2 a, FPoint2 b) {
    return distanceSq(a.x, a.y, b.x, b.y);
  }

  //   public static double distanceSq (double x1, double y1, double x2, double y2) {
  //    x1 -= x2;
  //    y1 -= y2;
  //    return (x1*x1)+(y1*y1);
  //  }
  //  public static double distance (double x1, double y1, double x2, double y2) {
  //    return Math.sqrt(distanceSq(x1,y1,x2,y2));
  //  }

  //  public String dump(boolean withComma) {
  //    return Tools.f(x) + (withComma ? "," : " ") + Tools.f(y);
  //  }

  //public String dump() { // plotInfo
  //  return dump(false);
  //}

  /**
   * Dump point as x and y (rounded), with leading space before each
   * @return String
   */
  public String toString() {
    return toString(false, true);
  }

  /**
  * Dump point
  * @param allDigits  if true, results are not rounded
  * @param numbersOnly   if true, returns ' xxxxx yyyy '; otherwise, returns '(xxx,yyy)'
  * @return String
  */
  public String toString(boolean allDigits, boolean numbersOnly) {
    if (!numbersOnly) {
      if (allDigits) {
        return "(" + x + "," + y + ")";
      } else {
        return "(" + Tools.f(x) + "," + Tools.f(y) + ")";
      }
    } else {
      if (allDigits) {
        return " " + x + " " + y + " ";
      } else {
        return " " + Tools.f(x) + " " + Tools.f(y) + " ";
      }
    }
  }

  /**
   * Dump point
   * @param allDigits  if true, results are not rounded
   * @return String
   */
  public String toString(boolean allDigits) {
    return toString(true, false);
  }

  public void clear() {
    x = 0;
    y = 0;
  }

  public double[] coeff() {
    throw new UnsupportedOperationException();
  }

  public double get(int y, int x) {
    return (y == 1 ? this.y : this.x);
  }

  public double get(int y) {
    return (y == 1 ? this.y : this.x);
  }

  public int height() {
    return 2;
  }

  // public void set(FPoint2 pt) {this.x = pt.x; this.y = pt.y;}
  // public void set(double x, double y) {
  //   this.x = x; this.y = y;
  // }

  public void set(int y, int x, double v) {
    set(y, v);
  }

  public void set(int y, double v) {
    if (y == 0)
      this.x = v;
    else {
      if (y != 1)
        throw new IllegalArgumentException();
      this.y = v;
    }
  }

  public int width() {
    return 1;
  }

  public int size() {
    return 2;
  }

  //public double x, y;

  public void setX(double x) {
    this.x = x;

  }

  public void setY(double y) {
    this.y = y;
  }

  public void setZ(double z) {
    throw new UnsupportedOperationException();
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

  public void negate() {
    x = -x;
    y = -y;
  }

  public void scale(double s) {
    x *= s;
    y *= s;
  }

  public void setTo(IVector v) {
    x = v.x();
    y = v.y();
  }

  /**
   * Compare two points lexicographically, so they are sorted
   * first by y, then by x (if y's are equal)
   * @param pt1
   * @param pt2
   * @return negative, zero, or positive, if pt1 is less than, equal to,
   *  or greater than pt2
   */
  public static int compareLex(FPoint2 pt1, FPoint2 pt2) {
    return compareLex(pt1, pt2, true);
    //    
    //    double res = pt1.y - pt2.y;
    //    if (res == 0)
    //      res = pt1.x - pt2.x;
    //    return MyMath.sign(res);
  }
  /**
   * Compare two points lexicographically
   * @param pt1
   * @param pt2
   * @param yHasPrecedence true to sort by y, and use x to break ties;
   *  false to sort by x first
   * @return negative, zero, or positive, if pt1 is less than, equal to,
   *  or greater than pt2
   */
  public static int compareLex(FPoint2 pt1, FPoint2 pt2, boolean yHasPrecedence) {
    double res;
    if (yHasPrecedence) {
      res = pt1.y - pt2.y;
      if (res == 0)
        res = pt1.x - pt2.x;
    } else {
      res = pt1.x - pt2.x;
      if (res == 0)
        res = pt1.y - pt2.y;
    }
    return MyMath.sign(res);
  }

  public static final Comparator comparatorYX = new Comparator() {
    public int compare(Object arg0, Object arg1) {
      FPoint2 p0 = (FPoint2) arg0, p1 = (FPoint2) arg1;
      return compareLex(p0, p1, true);
    }
  };
  public static final Comparator comparatorXY = new Comparator() {
    public int compare(Object arg0, Object arg1) {
      FPoint2 p0 = (FPoint2) arg0, p1 = (FPoint2) arg1;
      return compareLex(p0, p1, false);
    }
  };

}
