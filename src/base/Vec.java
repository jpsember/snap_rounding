package base;

public class Vec extends Matrix implements IVector {

  /**
   * Multiply Matrix M and vector b together
   * @param M
   * @param b
   * @param dest : Vector to hold result; if null, constructs one
   * @return [M][b]
   */
  public static Vec mult(Matrix M, Vec b, Vec dest) {

    if (dest == null)
      dest = new Vec(b.size());

    if (M.width != b.height || b.height != dest.height)
      throw new IllegalArgumentException();

    double[] vb = b.coeff();
    double[] vm = M.coeff;
    double[] vd = dest.coeff();

    for (int i = 0, k = 0; i < M.height; i++) {
      double vdAmt = 0;
      for (int j = 0; j < M.width; j++, k++) {
        vdAmt += vm[k] * vb[j];
      }
      vd[i] = vdAmt;
    }
    return dest;
  }

  /**
   * Multiply vector, Matrix, and vector together: aMb
   * @param a
   * @param M
   * @param b
   * @return value of aMb
   */
  public static double mult(Vec a, Matrix M, Vec b) {
    return Vec.innerProduct(a, mult(M, b, null));
  }

//  /**
//   * Solve Ax = B
//   * @param A matrix
//   * @param B vector
//   * @param dest where to store result; if null, constructs new object
//   * @return x
//   * @throws FPError if matrix is singular
//   * @deprecated
//   */
//  public static IVector solve(Matrix A, IVector B, IVector dest) {
//    return MatrixUtil.invert(A,null).mult(B, dest);
//  }

  /**
   * Construct zero vector
   * @param size
   */
  public Vec(int size) {
    super(size, 1);
  }

  public static Vec scalar(double n) {
    Vec v = new Vec(1);
    v.set(0, n);
    return v;
  }

  /**
   * Constructor for scalar (1-dimensional vector)
   */
  public Vec() {
    super(1, 1);
  }

  /**
   * Constructor for vector of size 3
   * @param c0
   * @param c1
   * @param c2
   */
  public Vec(double c0, double c1, double c2) {
    super(3, 1);
    coeff[0] = c0;
    coeff[1] = c1;
    coeff[2] = c2;
  }

  /**
   * Constructor for vector of size 2
   * @param c0
   * @param c1
   */
  public Vec(double c0, double c1) {
    super(2, 1);
    coeff[0] = c0;
    coeff[1] = c1;
  }

  /**
   * Constructor for vector of size 4
   * @param c0
   * @param c1
   * @param c2
   * @param c3
   */
  public Vec(double c0, double c1, double c2, double c3) {
    super(4, 1);
    coeff[0] = c0;
    coeff[1] = c1;
    coeff[2] = c2;
    coeff[3] = c3;
  }

  /**
   * Subtract two vectors.  Destination can be same as one of the input vectors.
   * @param a
   * @param b
   * @param dest : if not null, creates one
   */
  public static IVector sub(IVector a, IVector b, IVector dest) {
    if (dest == null)
      dest = new Vec(a.size());

    for (int i = 0; i < a.size(); i++)
      dest.set(i, a.get(i) - b.get(i));

    return dest;
  }

  /**
   * Add two vectors.  Destination can be same as one of the input vectors.
   * @param a
   * @param b
   * @param dest
   */
  public static IVector add(IVector a, IVector b, IVector dest) {
    if (dest == null)
      dest = new Vec(a.size());
    for (int i = 0; i < a.size(); i++)
      dest.set(i, a.get(i) + b.get(i));
    return dest;
  }

  public void add(int coeff, double amt) {
    this.coeff[coeff] += amt;
  }

  /**
   * Construct vector from array of doubles
   * @param coeff : array of doubles
   */
  public Vec(double[] coeff) {
    this(coeff.length);
    for (int i = 0; i < coeff.length; i++)
      this.coeff[i] = coeff[i];
  }

  public Vec(IVector src) {
    this(src.size());
    for (int i = 0; i < coeff.length; i++)
      this.coeff[i] = src.get(i);
  }

  //  public Vec(FPoint3 pt) {
  //    this(pt.x, pt.y, pt.z);
  //  }
  //
  public void set(double v) {
    coeff[0] = v;
  }

  public void set(int pos, double v) {
    coeff[pos] = v;
  }

  public double get(int pos) {
    return coeff[pos];
  }

  public double get() {
    return coeff[0];
  }

  public int size() {
    return coeff.length;
  }

  /**
   * Debug function to get coefficients as an array, in case coeff() method
   * isn't implemented
   * 
   * @param v
   * @return array of doubles
   */
  public static double[] getCoefficients(IVector v) {
    double[] a = new double[v.size()];
    for (int i = 0; i < a.length; i++)
      a[i] = v.get(i);
    return a;
  }

  public String toString() {
    final boolean full = false;

    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < size(); i++)
      if (full)
        sb.append(coeff[i] + " ");
      else
        sb.append(Tools.f(coeff[i]));
    sb.append("]");
    return sb.toString();
  }

  /**
   * Scale every component by a value
   * @param s : scale factor
   */
  public void scale(double s) {
    for (int i = 0; i < size(); i++)
      coeff[i] *= s;
  }

  /**
   * Negate every component
   */
  public void negate() {
    for (int i = 0; i < size(); i++)
      coeff[i] = -coeff[i];
  }

  public static IVector difference(IVector b, IVector a, IVector d) {
    if (d == null)
      d = new Vec(b.size());
    for (int i = 0; i < b.size(); i++)
      d.set(i, b.get(i) - a.get(i));
    return d;
  }

  //  public void setTo(Vec di) {
  //    for (int i = 0; i < size(); i++)
  //      coeff[i] = di.coeff[i];
  //  }

  public static IVector addMultiple(IVector a, double mult, IVector b,
      IVector dest) {
    if (dest == null)
      dest = new Vec(a.size());

    double[] dc = dest.coeff(), ac = a.coeff(), bc = b.coeff();

    for (int i = 0; i < a.size(); i++)
      dc[i] = ac[i] + mult * bc[i];
    return dest;
  }

  /**
   * Normalize a vector
   * @param v : IVector to normalize
   * @param dest : where to store normalized vector; if null, constructs one
   * @return dest
   */
  public static IVector normalize(IVector v, IVector dest) {
    if (dest == null)
      dest = new Vec(v.size());
    dest.setTo(v);
    dest.normalize();
    return dest;
  }

  public static double innerProduct(IVector ri, IVector ri2) {
    double r = 0;
    for (int i = 0; i < ri.size(); i++)
      r += ri.get(i) * ri2.get(i);
    return r;
  }

  public double lengthSq() {
    double r = 0;
    for (int i = 0; i < size(); i++)
      r += coeff[i] * coeff[i];
    return r;
  }

  public double length() {
    return Math.sqrt(lengthSq());
  }

  public double normalize() {
    double lenSq = lengthSq();
    if (lenSq != 0 && lenSq != 1) {
      lenSq = Math.sqrt(lenSq);
      double scale = 1 / lenSq;
      scale(scale);
    }
    return lenSq;
  }

  public void setX(double x) {
    coeff[0] = x;
  }

  public void setY(double y) {
    coeff[1] = y;
  }

  public void setZ(double z) {
    coeff[2] = z;
  }

  public double x() {
    return coeff[0];
  }

  public double y() {
    return coeff[1];
  }

  public double z() {
    return coeff[2];
  }

  public void setTo(IVector v) {
    if (v.size() != coeff.length)
      throw new IllegalArgumentException();
    for (int i = 0; i < coeff.length; i++)
      coeff[i] = v.get(i);
  }

  /**
   * Build a new vector, copying whatever coefficients will fit from an existing
   * vector
   * @param v source vector
   * @param size size of new vector
   * @return new vector
   */
  public static Vec buildFrom(IVector v, int size) {
    Vec vn = new Vec(size);
    for (int i = 0; i < v.size(); i++)
      vn.set(i, v.get(i));
    return vn;
  }

}
