package base;

public class Scalar implements IVector {
  public Scalar(double v) {
    setValue(v);
  }

  public Scalar(IVector src) {
    setValue(src.x());
  }

  public void alignToGrid(double gridX) {
    double iGrid = 1 / gridX;
    setValue(Math.round(value * iGrid) * gridX);
  }

  public void setValue(double v) {
    this.value = v;
  }

  public Scalar() {
  }

  public boolean clamp(double x0, double x1) {
    boolean valid = true;
    if (value < x0 || value > x1) {
      valid = false;
      setValue(MyMath.clamp(value, x0, x1));
    }
    return valid;
  }

  public String toString() {
    return toString(true);
  }

  /**
   * Get string representation of point
   * @param allDigits  if true, results are not rounded
   * @return string representation of point
   */
  public String toString(boolean allDigits) {
    if (allDigits) {
      return Double.toString(value);
    } else
      return Tools.f(value);
  }

  public void clear() {
    setValue(0);
  }

  public double[] coeff() {
    throw new UnsupportedOperationException();
  }
  public double lengthSq() {
    return value * value;
  }

  //  public double get(int y, int x) {
  //    return (y == 1 ? this.y : this.x);
  //  }
  public double length() {
    return Math.abs(value);
  }

  public double get(int y) {
    return value;
  }

  public int height() {
    return 1;
  }

  // public void set(FPoint2 pt) {this.x = pt.x; this.y = pt.y;}
  // public void set(double x, double y) {
  //   this.x = x; this.y = y;
  // }

  public void set(int y, int x, double v) {
    set(y, v);
  }

  public void set(int y, double v) {
    if (y != 0)
      throw new IllegalArgumentException();
    setValue(v);
  }

  public int width() {
    return 1;
  }

  public int size() {
    return 1;
  }

  //public double x, y;

  public void setX(double x) {
    setValue(x);
  }

  public void setY(double y) {
    throw new UnsupportedOperationException();
  }

  public void setZ(double z) {
    throw new UnsupportedOperationException();
  }

  public double x() {
    return value;
  }

  public double y() {
    throw new UnsupportedOperationException();
  }

  public double z() {
    throw new UnsupportedOperationException();
  }

  public void negate() {
    setValue(-value);
  }

  public void scale(double s) {
    setValue(value * s);
  }

  public void setTo(IVector v) {
    setValue(v.x());
  }

  private double value;

  public double normalize() {
    setValue(Math.signum(value));
    return value;
  }

  public double get(int y, int x) {
    if (x != 0 || y != 0)
      throw new IllegalArgumentException();
    return value;
  }
}
