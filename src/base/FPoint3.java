package base;


public class FPoint3 implements IVector {

  public double x, y, z;

  public FPoint3(double x, double y, double z) {
    set(x, y, z);
  }

  public FPoint3() {
  }

  public void set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void setTo(IVector v) {
    set(v.x(), v.y(), v.z());
  }

  public void add(double x, double y, double z) {
    this.x += x;
    this.y += y;
    this.z += z;
  }

  public void add(FPoint3 pt) {
    add(pt.x, pt.y, pt.z);
  }

  public static FPoint3 add(FPoint3 a, FPoint3 b, FPoint3 dest) {
    if (dest == null)
      dest = new FPoint3();
    dest.x = a.x + b.x;
    dest.y = a.y + b.y;
    dest.z = a.z + b.z;
    return dest;
  }

  public static FPoint3 addMultiple(FPoint3 a, double mult, FPoint3 b,
      FPoint3 dest) {
    if (dest == null)
      dest = new FPoint3();

    dest.x = a.x + mult * b.x;
    dest.y = a.y + mult * b.y;
    dest.z = a.z + mult * b.z;

    return dest;
  }

  public static double distance(FPoint3 a, FPoint3 b) {
    return FPoint3.distance(a.x, a.y, a.z, b.x, b.y, b.z);
  }

  public static double distanceSq(FPoint3 a, FPoint3 b) {
    return distanceSq(a.x, a.y, a.z, b.x, b.y, b.z);
  }

  /**
   * Returns the square of the distance between two 3d points
   * @param x1
   * @param y1
   * @param z1 first point
   * @param x2
   * @param y2
   * @param z2 second point
   * @return the square of the distance between the two points
   */
  public static double distanceSq(double x1, double y1, double z1, double x2,
      double y2, double z2) {
    x1 -= x2;
    y1 -= y2;
    z1 -= z2;
    return (x1 * x1 + y1 * y1 + z1 * z1);
  }

  /**
   * Returns the distance between two 3d points
   * @param x1
   * @param y1
   * @param z1 first point
   * @param x2
   * @param y2
   * @param z2 second point
   * @return the distance between the two points
   */
  public static double distance(double x1, double y1, double z1, double x2,
      double y2, double z2) {
    return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
  }

  public FPoint3(IVector src) {
    set(src.x(), src.y(), src.z());
  }

  public static FPoint3 difference(FPoint3 b, FPoint3 a, FPoint3 d) {
    if (d == null)
      d = new FPoint3();
    d.set(b.x - a.x, b.y - a.y, b.z - a.z);
    return d;
  }

  public static FPoint3 crossProduct(FPoint3 a, FPoint3 b, FPoint3 c,
      FPoint3 dest) {
    return crossProduct(b.x - a.x, b.y - a.x, b.z - a.z, c.x - a.x, c.y - a.y,
        c.z - a.z, dest);
  }

  /**
   * Get the square of the distance of this point from the origin
   * @return square of distance from origin
   */
  public double lengthSq() {
    return x * x + y * y + z * z;
  }

  /**
   * Get the distance of this point from the origin
   * @return distance from origin
   */
  public double length() {
    return Math.sqrt(lengthSq());
  }

  /**
   * Adjust location of point so it lies at unit distance, in 
   * the same direction from the origin as the original.  If point is at origin,
   * leaves it there.
   * return the original point's distance from the origin, squared
   */
  public double normalize() {
    double lenSq = lengthSq();
    if (lenSq != 0 && lenSq != 1) {
      lenSq = Math.sqrt(lenSq);
      double scale = 1 / lenSq;
      x *= scale;
      y *= scale;
      z *= scale;
    }
    return lenSq;
  }

  /**
   * Calculate the inner (dot) product of two points
   * @param s
   * @param t
   * @return the inner product
   */
  public static double innerProduct(FPoint3 s, FPoint3 t) {
    return innerProduct(s.x, s.y, s.z, t.x, t.y, t.z);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    sb.append(Tools.f(x));
    Tools.addSp(sb);
    sb.append(Tools.f(y));
    Tools.addSp(sb);
    sb.append(Tools.f(z));
    Tools.addSp(sb);
    return sb.toString();
  }

  public static FPoint3 crossProduct(double x1, double y1, double z1,
      double x2, double y2, double z2, FPoint3 dest) {
    if (dest == null)
      dest = new FPoint3();
    dest.x = y1 * z2 - z1 * y2;
    dest.y = z1 * x2 - x1 * z2;
    dest.z = x1 * y2 - y1 * x2;
    return dest;
  }

  public static FPoint3 crossProduct(FPoint3 a, FPoint3 b, FPoint3 dest) {
    return crossProduct(a.x, a.y, a.z, b.x, b.y, b.z, dest);

  }

  public static double innerProduct(double x1, double y1, double z1, double x2,
      double y2, double z2) {
    return x1 * x2 + y1 * y2 + z1 * z2;
  }

  //  /**
  //   * @deprecated
  //   * @param a
  //   * @throws IOException
  //   */
  //  public void serialize(DataOutputStream a) throws IOException {
  //    a.writeDouble(x);
  //    a.writeDouble(y);
  //    a.writeDouble(z);
  //  }

  //  /**
  //   * Construct FPoint3 from serialized data
  //   * @param s : DataInputStream
  //   * @throws IOException 
  //   * @deprecated
  //   */
  //  public FPoint3(DataInputStream s) throws IOException {
  //    this(s.readDouble(), s.readDouble(), s.readDouble());
  //  }
  //
  public void negate() {
    x = -x;
    y = -y;
    z = -z;
  }

  public void scale(double d) {
    x *= d;
    y *= d;
    z *= d;

  }

  /**
   * Interpolate between two points
   * @param a : first point
   * @param b : second point
   * @param mult : interpolation factor (0=a, 1=b)
   * @param dest : where to store interpolated point, or null to construct
   * @return interpolated point
   */
  public static FPoint3 interpolate(FPoint3 a, FPoint3 b, double mult,
      FPoint3 dest) {
    if (dest == null)
      dest = new FPoint3();
    dest.set(a.x + mult * (b.x - a.x), a.y + mult * (b.y - a.y), a.z + mult
        * (b.z - a.z));
    return dest;
  }

  public void clear() {
    x = 0;
    y = 0;
    z = 0;
  }

  public double[] coeff() {
    throw new UnsupportedOperationException();
  }

  public double get(int y, int x) {
    return get(y);
  }

  public double get(int y) {
    if (y == 0)
      return this.x;
    if (y == 1)
      return this.y;
    return this.z;
  }

  public int height() {
    return 3;
  }

  public void set(int y, int x, double v) {
    set(y, v);
  }

  public void set(int y, double v) {
    if (y == 0)
      this.x = v;
    else if (y == 1)
      this.y = v;
    else
      this.z = v;
  }

  public int size() {
    return 3;
  }

  public int width() {
    return 1;
  }

  public void setX(double x) {
    this.x = x;

  }

  public void setY(double y) {
    this.y = y;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

}