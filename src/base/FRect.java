package base;

import java.awt.*;
import java.awt.geom.*;

public class FRect extends Rectangle.Double {

  public FRect(Rectangle2D r) {
    super(r.getX(), r.getY(), r.getWidth(), r.getHeight());
  }

  /**
   * Constructor
   * @param tk Tokenizer containing x y w h tokens as doubles
   */
  public FRect(TextScanner tk) {
    super(tk.readDouble(), tk.readDouble(), tk.readDouble(), tk.readDouble());
  }

  public FPoint2 midPoint() {
    return FPoint2.midPoint(start(), end());
  }

  public FPoint2 bottomLeft() {
    return new FPoint2(x, y);
  }

  public FPoint2 bottomRight() {
    return new FPoint2(endX(), y);
  }
  public FPoint2 size() {
    return new FPoint2(width, height);
  }
  public FPoint2 topLeft() {
    return new FPoint2(x, endY());
  }

  public FPoint2 topRight() {
    return new FPoint2(endX(), endY());
  }

  /**
   * Constructor
   * @param x start x
   * @param y start y
   * @param w width
   * @param h height
   */
  public FRect(double x, double y, double w, double h) {
    super(x, y, w, h);
  }

  /**
   * Constructor, for two opposite corners
   * @param pt1 FPoint2
   * @param pt2 FPoint2
   */
  public FRect(FPoint2 pt1, FPoint2 pt2) {
    super(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y), Math.abs(pt2.x
        - pt1.x), Math.abs(pt2.y - pt1.y));
  }

  public void inset(double amt) {
    x += amt;
    y += amt;
    width -= amt * 2;
    height -= amt * 2;
  }

  final public FPoint2 start() {
    return new FPoint2(x, y);
  }

  public FRect() {
    super();
  }

  public FRect(FRect src) {
    super(src.x, src.y, src.width, src.height);
  }

  public void addPoint(FPoint2 pt) {
    if (pt.x < x) {
      double a = x - pt.x;
      x -= a;
      width += a;
    } else if (pt.x > x + width) {
      width = pt.x - x;
    }
    if (pt.y < y) {
      double a = y - pt.y;
      y -= a;
      height += a;
    } else if (pt.y > y + height) {
      height = pt.y - y;
    }

  }
  public static FRect add(FRect r, FPoint2 point) {
    if (r == null)
      r = new FRect(point, point);
    else
      r.addPoint(point);
    return r;
  }

  public FPoint2 clamp(FPoint2 pt) {
    return new FPoint2(MyMath.clamp(pt.x, x, x + width), MyMath.clamp(pt.y, y,
        y + height));
  }

  final public double endX() {
    return x + width;
  }

  final public double endY() {
    return y + height;
  }

  final public FPoint2 end() {
    return new FPoint2(x + width, y + height);
  }

  /**
   * Construct corner points for rectangle
   * @param index : 0,1,2,3 for counterclockwise walk of rectangle
   *   starting from x,y
   * @return FPoint2
   */
  final public FPoint2 corner(int index) {
    switch (index) {
    default:
      Tools.ASSERT(index == 0);
      return start();
    case 1:
      return new FPoint2(x + width, y);
    case 2:
      return end();
    case 3:
      return new FPoint2(x, y + height);
    }
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString(boolean full) {
    if (full)
      return super.toString();
    else {
      StringBuilder sb = new StringBuilder();
      sb.append(Tools.f(x));
      sb.append(' ');
      sb.append(Tools.f(y));
      sb.append(' ');
      sb.append(Tools.f(width));
      sb.append(' ');
      sb.append(Tools.f(height));
      sb.append(' ');

      return sb.toString();
    }
  }

  public String toString() {
    return toString(false);
  }

  public boolean contains(Rectangle2D r) {

    return (x <= r.getMinX() //
        && y <= r.getMinY() //
        && x + width >= r.getMaxX() //
    && y + height >= r.getMaxY() //
    );
  }

}
