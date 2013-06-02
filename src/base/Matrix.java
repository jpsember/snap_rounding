package base;

import java.awt.geom.*;
import java.util.*;

/**
 * Matrix class.
 * <br>
 * A permutation array is an int array of length n, containing a permutation
 * of [0,1,...,n].  The correspondence between a permutation array pa and
 * a permutation matrix m is: if pa[j]=i, this means a 1 appears 
 * in row i, column j of m.
 */
public class Matrix implements IMatrix {

  // size of homogenous 2d matrix
  private static final int SIZE2D = 3;

  /**
   * Multiply every coefficient in matrix by a scalar
   * @param scale scale factor
   */
  public void multBy(double scale) {
    for (int i = 0; i < coeff.length; i++)
      coeff[i] *= scale;
  }

  /**
   * Multiply two matrices together.  Destination matrix may be
   * one of the input matrices.
   * 
   * @param ma  matrix of size h1 x w1
   * @param mb   matrix of size h2 x w2
   * @param md  destination matrix of size h1 x w2, or null
   * @return destination matrix 
   */
  public static Matrix mult(Matrix ma, Matrix mb, Matrix md) {

    if (md == null)
      md = new Matrix(ma.height, mb.width);

    if (ma.width != mb.height || md.width != mb.width || md.height != ma.height)
      throw new IllegalArgumentException();

    double[] a = ma.coeff, b = mb.coeff;

    double[] d = md.coeff;
    if (md == ma || md == mb)
      d = new double[d.length];

    int ch = ma.width;
    int k = 0;
    for (int y1 = 0, y1a = 0; y1 < ma.height; y1++, y1a += ch) {
      for (int x2 = 0; x2 < mb.width; x2++) {
        double s = 0;
        for (int x = 0, xa = x2; x < ch; x++, xa += mb.width) {
          s += a[y1a + x] * b[xa];
        }
        d[k++] = s;
      }
    }
    md.coeff = d;
    return md;
  }

  /**
   * Construct a square matrix of zeros 
   * @param size
   */
  public Matrix(int size) {
    this(size, size);
  }

  /**
   * Construct a matrix from values stored in an array
   * @param array array containing sequence w h c11 c12 ... c21 c22 .. chw
   *  where w is width, h is height, and c11,c12,..,chw are coefficients stored
   *  by rows
   */
  public Matrix(double[] array) {
    this(array, 0);
  }

  /**
   * Construct a matrix from values stored in an array
   * @param array array containing sequence w h c11 c12 ... c21 c22 .. chw
   *  where w is width, h is height, and c11,c12,..,chw are coefficients stored
   *  by rows
   * @param offset offset to start of sequence
   */
  public Matrix(double[] array, int offset) {
    int t = offset;
    this.width = (int) array[t++];
    this.height = (int) array[t++];
    coeff = new double[width * height];
    int k = 0;
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        coeff[k++] = array[t++];
  }

  /**
   * Copy constructor
   * @param src matrix to copy 
   */
  public Matrix(Matrix src) {
    this(src.height, src.width, src.coeff);
  }

  /**
   * Construct a matrix of zeros 
   * @param height
   * @param width
   */
  public Matrix(int height, int width) {
    this.width = width;
    this.height = height;
    coeff = new double[width * height];
  }

  /**
   * Construct matrix
   * @param height
   * @param width
   * @param coeff array of coefficients, stored by rows
   */
  public Matrix(int height, int width, double[] coeff) {
    this(height, width, coeff, 0);
  }
  /**
   * Construct matrix 
   * @param height
   * @param width
   * @param coeff array of coefficients, stored by rows
   * @param offset index of first coefficient within array
   */
  public Matrix(int height, int width, double[] coeff, int offset) {
    this(height, width);
    readValues(coeff, offset);
  }

  /**
   * Initialize coefficients from array
   * @param dc array of coefficients, stored by rows
   * @param offset index of first coefficient within array
   */
  private void readValues(double[] dc, int offset) {
    for (int i = 0; i < coeff.length; i++)
      coeff[i] = dc[i + offset];
  }

  //  /**
  //   * @deprecated
  //   * @param t
  //   * @return
  //   */
  //  public static String toString(AffineTransform t) {
  //    StringBuilder sb = new StringBuilder();
  //    sb.append("AffineTransform[\n");
  //    double[] m = new double[6];
  //    t.getMatrix(m);
  //    sb.append(Tools.f(m[0]));
  //    sb.append(Tools.f(m[2]));
  //    sb.append(Tools.f(m[4]));
  //    sb.append('\n');
  //    sb.append(Tools.f(m[1]));
  //    sb.append(Tools.f(m[3]));
  //    sb.append(Tools.f(m[5]));
  //    sb.append("]");
  //    return sb.toString();
  //  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int y = 0; y < height; y++) {
      if (y > 0)
        sb.append('\n');
      sb.append("[");
      for (int x = 0; x < width; x++) {
        if (x > 0)
          sb.append(' ');
        //        sb.append(get(y,x));
        sb.append(Tools.f(get(y, x)));
      }
      sb.append("]");

    }
    return sb.toString();
  }

  /**
   * Describe matrix in compact way
   * @return
   */
  public String c() {
    //    int[] maxWidths = new int[width];
    String[] strs = new String[width * height];
    int[] mw = new int[width];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        double d = get(y, x);
        String s = toString(d);
        //        d = MyMath.snapToGrid(d, 1e-1);
        //        int di = (int) d;
        //        String s;
        //        if (di == d)
        //          s = Integer.toString(di);
        //        else
        //          s = Tools.f(d, 5, 1).trim();
        //        if (di >= 0)
        //          s = " " + s;
        strs[y * width + x] = s;
        mw[x] = Math.max(mw[x], s.length());
      }
    }

    StringBuilder sb = new StringBuilder();
    for (int y = 0; y < height; y++) {
      if (y > 0)
        sb.append('\n');
      sb.append("[");

      for (int x = 0; x < width; x++) {
        String s = strs[y * width + x];
        if (x > 0)
          sb.append(' ');
        sb.append(Tools.sp(mw[x] - s.length()));
        sb.append(s);
      }
      sb.append("]");
    }
    return sb.toString();
  }
  /**
   * Add a value to a coefficient
   * @param y : row (0..height-1)
   * @param x : column (0..width-1)
   * @param val
   */
  public void add(int y, int x, double val) {
    coeff[y * width + x] += val;
  }

  public void set(int y, int x, double v) {
    coeff[y * width + x] = v;
  }

  /**
   * Clear matrix to zeros
   */
  public void clear() {
    for (int i = 0; i < coeff.length; i++)
      coeff[i] = 0;
  }

  /**
   * Get array of coefficients.
   * They are indexed by [y*height + x].
   * This returns the actual array, so any changes user makes will
   * persist.
   * @return coefficients
   */
  public double[] coeff() {
    return coeff;
  }

  /**
   * Get coefficient from matrix, by returning coeff[y*width + x].
   * @param y : row (0..height-1)
   * @param x : column (0..width-1)
   * @return coefficient
   */
  public double get(int y, int x) {
    return coeff[y * width + x];
  }

  /**
   * Get width of matrix
   * @return width
   */
  public int width() {
    return width;
  }

  /**
   * Get height of matrix
   * @return height
   */
  public int height() {
    return height;
  }

  public void setIdentity() {
    //  tools.ASSERT(square, "Not square!");
    for (int i = 0; i < height; i++)
      for (int j = 0; j < height; j++)
        set(i, j, i == j ? 1.0 : 0);
  }

  /**
   * Multiply matrix by vector.
   * @param b : vector to multiply by
   * @param r : where to store result
   */
  public Vec mult(Vec b, Vec r) {

    if (r == null)
      r = new Vec(b.size());

    mult(b.coeff(), r.coeff());
    return r;
  }

  public IVector mult(IVector b, IVector r) {
    if (r == null)
      r = new Vec(b.size());

    for (int i = 0, k = 0; i < height; i++) {
      double c = 0;
      for (int j = 0; j < width; j++, k++) {
        c += coeff[k] * b.get(j);
      }
      r.set(i, c);
    }
    return r;
    //
    //    mult(b.coeff(), r.coeff());
    //    return r;
  }

  public static double scalarProduct(IMatrix a, IMatrix b) {
    if (a.width() != 1 || b.width() != 1 || a.height() != b.height())
      throw new IllegalArgumentException();
    double v = 0;
    for (int i = 0; i < a.height(); i++)
      v += a.get(i, 0) * b.get(i, 0);
    return v;
  }

  /**
   * Apply a permutation to a vector
   * @param perm permutation array
   * @param b vector
   * @param res result, or null
   * @return result
   */
  public static double[] permute(int[] perm, double[] b, double[] res) {

    double[] r2 = res;
    if (res == null || res == b)
      r2 = new double[b.length];

    for (int i = 0; i < perm.length; i++)
      r2[perm[i]] = b[i];

    if (res != null && res != r2) {
      for (int i = 0; i < res.length; i++)
        res[i] = r2[i];
    }
    return r2;
  }
  /**
   * Apply the inverse of a permutation to a vector
   * @param perm permutation array
   * @param b vector
   * @param res result, or null
   * @return result
   */
  public static double[] permuteInv(int[] perm, double[] b, double[] res) {

    double[] r2 = res;
    if (res == null || res == b)
      r2 = new double[b.length];

    for (int i = 0; i < perm.length; i++)
      r2[i] = b[perm[i]];

    if (res != null && res != r2) {
      for (int i = 0; i < res.length; i++)
        res[i] = r2[i];
    }
    return r2;
  }
  /**
   * Multiply matrix by vector
   * @param b  array of values representing vector
   * @param res array of resulting values, or null
   * @return resulting values
   */
  public double[] mult(double[] b, double[] res) {
    if (res == null)
      res = new double[height];
    for (int i = 0, k = 0; i < height; i++) {
      double c = 0;
      for (int j = 0; j < width; j++, k++) {
        c += coeff[k] * b[j];
      }
      res[i] = c;
    }

    return res;
  }

  //  /**
  //   * Construct a matrix from a script
  //   * @param scr  array of doubles: [height width elem(0,0) elem(0,1) ... elem(ht-1,wd-1)]
  //   * @return Matrix
  //   * @deprecated
  //   */
  //  public static Matrix buildFromScript(double[] scr) {
  //    return buildFromScript((int) scr[0], (int) scr[1], scr, 2);
  //  }

  //  /**
  //   * @deprecated
  //   */
  //  public static Matrix buildFromScript(int height, int width, double[] scr,
  //      int valsOffset) {
  //
  //    Matrix m = new Matrix(height, width);
  //    double[] c = m.coeff();
  //    for (int i = 0; i < c.length; i++)
  //      c[i] = scr[i + valsOffset];
  //    return m;
  //  }

  /**
   * Transform 2d point.  Matrix must be 3x3 homogeneous matrix
   * @param pt  2-d point
   * @param dest where to store result, or null 
   * @return result
   */
  public FPoint2 apply(IVector pt, FPoint2 dest) {
    return apply(pt.x(), pt.y(), dest);
  }
  /**
   * Transform 2d point.  Matrix must be 3x3 homogeneous matrix
   * @param pt  2-d point
   * @return result
   */
  public FPoint2 apply(IVector pt) {
    return apply(pt, null);
  }

  /**
   * Transform 2d point.  Matrix must be 3x3 homogeneous matrix
   * @param x
   * @param y coordinates of point
   * @param dest where to store result, or null 
   * @return result
   */
  public FPoint2 apply(double x, double y, FPoint2 dest) {

    if (dest == null)
      dest = new FPoint2();

    if (width != 3 || height != 3)
      throw new IllegalArgumentException();

    double[] vm = coeff;

    dest.set(//
        vm[SIZE2D * 0 + 0] * x + vm[SIZE2D * 0 + 1] * y + vm[SIZE2D * 0 + 2], //
        vm[SIZE2D * 1 + 0] * x + vm[SIZE2D * 1 + 1] * y + vm[SIZE2D * 1 + 2] //
    );
    return dest;
  }

  /**
   * Scale a 2d matrix.  Destination matrix 
   * can be the same as the source.
   * @param sx scale factor
   * @param sy scale factor
   * @param dest destination matrix, or null
   * @return destination matrix
   */
  public Matrix scale(double sx, double sy, Matrix dest) {

    if (dest == null)
      dest = new Matrix(this);

    double[] v = coeff();
    double[] d = dest.coeff();

    for (int i = 0; i < SIZE2D - 1; i++) {
      d[i + 0] = v[i + 0] * sx;
      d[i + SIZE2D * 1] = v[i + SIZE2D * 1] * sy;
    }

    //    Streams.out.println("scaled matrix\n"+source+"\n to \n"+dest);
    return dest;

  }

  /**
   * Apply translation to 2d homogeneous matrix
   * @param x
   * @param y
   */
  public void translate(double x, double y) {
    double[] v = coeff();
    v[0 * SIZE2D + SIZE2D - 1] += x;
    v[1 * SIZE2D + SIZE2D - 1] += y;
  }

  /**
   * Apply translation to 2d matrix
   * @param tr translation
   */
  public void translate(IVector tr) {
    translate(tr.x(), tr.y());
  }

  /**
   * Rotate 2d matrix
   * @param ang rotation angle
   */
  public void rotate(double ang) {
    mult(getRotate(ang), this, this);
  }

  /**
   * Get 2d rotation matrix
   * @param ang angle of rotation
   * @return 3x3 homogenous matrix
   */
  public static Matrix getRotate(double ang) {
    Matrix m = new Matrix(SIZE2D, SIZE2D);
    double[] v = m.coeff();

    double c = Math.cos(ang), s = Math.sin(ang);

    v[SIZE2D * 2 + 2] = 1.0;

    v[SIZE2D * 0 + 0] = c;
    v[SIZE2D * 0 + 1] = -s;
    v[SIZE2D * 1 + 1] = c;
    v[SIZE2D * 1 + 0] = s;
    return m;
  }

  /**
   * Get 2d translation matrix
   * @param pt translation
   * @param neg if true, negates translation
   * @return 2d matrix
   */
  public static Matrix getTranslate(IVector pt, boolean neg) {
    Matrix m = new Matrix(3);
    m.setIdentity();
    double[] v = m.coeff();
    double sign = neg ? -1 : 1;
    v[SIZE2D * 0 + SIZE2D - 1] = pt.x() * sign;
    v[SIZE2D * 1 + SIZE2D - 1] = pt.y() * sign;
    return m;
  }

  /**
   * Get 2d scale matrix
   * @param scale scale factor
   * @return 2d matrix
   */
  public static Matrix getScale(IVector scale) {
    return getScale(scale.x(), scale.y());
  }

  /**
   * Get 2d scale matrix
   * @param sx
   * @param sy scale factors
   * @return 2d matrix
   */
  public static Matrix getScale(double sx, double sy) {
    Matrix m = new Matrix(3);
    m.setIdentity();
    double[] v = m.coeff();
    v[SIZE2D * 0 + 0] = sx;
    v[SIZE2D * 1 + 1] = sy;
    return m;
  }
  /**
   * Scale a 2d matrix.  Destination can be the same as the source.
   * @param s scale factor
   * @param dest destination matrix, or null
   * @return destination matrix
   */
  public Matrix scale(double s, Matrix dest) {
    return scale(s, s, dest);
  }

  /**
   * Get transpose of matrix
   * @param dest destinaton matrix, or null
   * @return destination matrix
   */
  public Matrix transpose(Matrix dest) {

    double[] ret = new double[coeff.length];

    if (dest == null)
      dest = new Matrix(width, height);
    if (dest.height != width || dest.width != height)
      throw new IllegalArgumentException();
    for (int i = 0; i < height; i++)
      for (int j = 0; j < width; j++)
        ret[j * height + i] = get(i, j);
    dest.coeff = ret;
    return dest;
  }

  /**
   * Fill matrix with random integers [-50..50)
   * @param r
   */
  public void randomize(Random r) {
    if (r == null)
      r = new Random();

    final int W = 100;
    for (int i = 0; i < coeff.length; i++)
      coeff[i] = r.nextInt(W) - W / 2;
  }

  public Matrix solve(Matrix b, Matrix result) {
    if (result == null)
      result = new Matrix(height, 1);
    solve(b.coeff(), result.coeff);
    return result;
  }

  public Matrix invert(Matrix dest) {
    int n = width;
    if (n != height)
      throw new IllegalArgumentException();

    Matrix src = new Matrix(this);
    if (dest == null)
      dest = new Matrix(n);
    dest.setIdentity();

    for (int c = 0; c < n; c++) {
      // find row with largest magnitude in column c, for submatrix 
      double maxElem = 0;
      int maxRow = -1;
      for (int r = c; r < n; r++) {
        double v = src.get(r, c);
        if (Math.abs(v) > Math.abs(maxElem)) {
          maxElem = v;
          maxRow = r;
        }
      }
      if (maxRow < 0)
        throw new FPError("singular matrix");

      // if necessary, exchange rows maxRow with c
      if (maxRow != c) {
        exchangeRows(src, maxRow, c);
        exchangeRows(dest, maxRow, c);
      }

      // scale row c so it contains a 1 in column c.
      double scaleFactor = 1 / maxElem;
      scaleRow(src, c, scaleFactor);
      scaleRow(dest, c, scaleFactor);

      // for every other row, subtract multiples of row c to set 0 in column c.
      for (int r = 0; r < n; r++) {
        if (r == c)
          continue;
        double factor = -src.get(r, c);
        addRowToRow(src, c, r, factor);
        addRowToRow(dest, c, r, factor);
      }
    }
    return dest;
  }

  /**
   * Solve Ax=B 
   * @param b coefficients of B
   * @param result where to store result, or null
   * @return result coefficients of x
   */
  public double[] solve(double[] b, double[] result) {

    final boolean db = false;
    if (db)
      Streams.out.println("solve:\n" + toString(this, b));

    int n = width;

    // construct result buffer if necessary
    if (result == null)
      result = new double[n];

    // verify arguments are valid
    if (height != n || b.length != n || result.length != n)
      throw new IllegalArgumentException();

    // construct work copies of a and b, so we don't overwrite originals
    Matrix work = new Matrix(this);
    double[] bw = (double[]) b.clone();

    for (int diag = 0; diag < n - 1; diag++) {
      // find pivot element, the highest magnitude
      // element in minor starting at (col,col)
      int pivRow = -1;
      {
        if (db)
          Streams.out.println("column " + diag + ", finding pivot;\n"
              + toString(work, bw));
        double maxPivot = 0;
        for (int row = diag; row < n; row++) {
          double v = Math.abs(work.get(row, diag));
          if (v > maxPivot) {
            maxPivot = v;
            pivRow = row;
          }
        }
        if (pivRow < 0)
          throw new FPError("singular matrix");

        // exchange rows if pivot row != current column
        if (pivRow != diag) {
          if (db)
            Streams.out.println(" pivot row is " + pivRow + ", exchanging");

          // exchange rows so pivot row is next row
          exchangeRows(work, pivRow, diag);
          double td = bw[pivRow];
          bw[pivRow] = bw[diag];
          bw[diag] = td;
        }
      }

      double pivRecip = -1.0 / work.get(diag, diag);

      for (int row = diag + 1; row < n; row++) {
        double v = work.get(row, diag);
        if (v == 0)
          continue;

        v = v * pivRecip;
        addRowToRow(work, diag, row, v);
        bw[row] += bw[diag] * v;
      }
      if (db)
        Streams.out.println("after adding multiples of row " + diag
            + " to other rows:\n" + toString(work, bw));
    }
    // matrix is now upper triangular; use back substitution

    if (db)
      Streams.out
          .println("matrix is now upper triangular, using back substitution");

    for (int row = n - 1; row >= 0; row--) {
      double v = work.get(row, row);
      if (v == 0)
        throw new FPError("singular matrix");
      double acc = bw[row];
      for (int col = row + 1; col < n; col++) {
        acc -= work.get(row, col) * result[col];
      }
      double xi = acc / v;
      result[row] = xi;
    }

    if (db)
      Streams.out.println(" result=" + new Vec(result));

    return result;
  }

  /**
   * Solve Ax=B, where A (this) is assumed to be lower triangular
   * @param b coefficients of B
   * @param result where to store result, or null
   * @return result coefficients of x
   */
  public double[] solveLowerTri(double[] b, double[] result) {

    final boolean db = false;
    if (db)
      Streams.out.println("solveLowerTri:\n" + toString(this, b));

    int n = width;

    // construct result buffer if necessary
    if (result == null)
      result = new double[n];

    // verify arguments are valid
    if (height != n || b.length != n || result.length != n)
      throw new IllegalArgumentException();

    for (int row = 0; row < n; row++) {
      double v = get(row, row);
      if (v == 0)
        throw new FPError("singular matrix");
      double acc = b[row];
      for (int col = 0; col < row; col++) {
        acc -= get(row, col) * result[col];
      }
      double xi = acc / v;
      result[row] = xi;
    }
    if (db)
      Streams.out.println(" result=" + new Vec(result));
    return result;
  }

  /**
   * Solve Ax=B, where A (this) is assumed to be upper triangular
   * @param b coefficients of B
   * @param result where to store result, or null
   * @return result coefficients of x
   */
  public double[] solveUpperTri(double[] b, double[] result) {

    final boolean db = false;
    if (db)
      Streams.out.println("solveUpperTri:\n" + toString(this, b));

    int n = width;

    // construct result buffer if necessary
    if (result == null)
      result = new double[n];

    // verify arguments are valid
    if (height != n || b.length != n || result.length != n)
      throw new IllegalArgumentException();

    if (db)
      Streams.out
          .println("matrix is now upper triangular, using back substitution");

    for (int row = n - 1; row >= 0; row--) {
      double v = get(row, row);
      if (v == 0)
        throw new FPError("singular matrix");
      double acc = b[row];
      for (int col = row + 1; col < n; col++) {
        acc -= get(row, col) * result[col];
      }
      double xi = acc / v;
      result[row] = xi;
    }

    if (db)
      Streams.out.println(" result=" + new Vec(result));

    return result;
  }

  /**
  * Construct a permutation matrix from a permutation array
  * @param pivots permutation array
  * @return permutation matrix
  */
  public static Matrix permutation(int[] pivots) {
    int n = pivots.length;
    Matrix r = new Matrix(n);
    for (int i = 0; i < n; i++) {
      r.set(pivots[i], i, 1.0);
    }
    return r;
  }

  /**
   * Get string displaying a matrix and a column vector 
   * @param M 
   * @param B  coefficients of column vector
   * @return string displaying augmented matrix 
   */
  public static String toString(Matrix M, double[] B) {
    return toString(M, B, 1);
  }

  /**
   * Display matrix as string; transpose if its width is 1 and its height is > 1
   * @return String
   */
  public String d() {
    String s = null;
    if (height > 1 && width == 1) {
      StringBuilder sb = new StringBuilder();
      sb.append('(');
      for (int i = 0; i < height; i++) {
        if (i > 0)
          sb.append(' ');
        sb.append(toString(get(i, 0)));

        //        double d = MyMath.snapToGrid(get(i, 0), 1e-3);
        //        int di = (int) d;
        //        if (di == d)
        //          sb.append(di);
        //        else
        //          sb.append(d);
        //        sb.append(s);
      }
      sb.append(')');
      s = sb.toString();
    } else
      s = toString();
    return s;
  }
  private static String toString(double v) {
    v = MyMath.snapToGrid(v, 1e-1);

    int vi = (int) v;
    String s;
    if (vi == v)
      s = Integer.toString(vi);
    else
      s = Tools.f(v, 8, 1).trim();
    if (v >= 0)
      s = " " + s;
    return s;
  }

  public static String toString(Matrix M, double[] B, int bCols) {
    if (bCols * M.height != B.length)
      throw new IllegalArgumentException();

    StringBuilder sb = new StringBuilder();

    int bi = 0;
    for (int y = 0; y < M.height; y++) {
      if (y > 0)
        sb.append('\n');
      sb.append("[");
      for (int x = 0; x < M.width; x++) {
        if (x > 0)
          sb.append(' ');
        sb.append(Tools.f(M.get(y, x)));
      }
      sb.append(" | ");

      for (int x = 0; x < bCols; x++) {
        if (x > 0)
          sb.append(' ');
        sb.append(Tools.f(B[bi + x]));
      }
      bi += bCols;
      sb.append("]");
    }
    return sb.toString();
  }
  private static void scaleRow(Matrix src, int row, double scale) {
    int i = row * src.width;
    for (int j = 0; j < src.width; j++)
      src.coeff[i + j] *= scale;
  }

  /**
   * Add a multiple of one row to another
   * @param srcRow
   * @param destRow
   * @param multiple
   */
  private static void addRowToRow(Matrix src, int srcRow, int destRow,
      double multiple) {
    int i = srcRow * src.width;
    int j = destRow * src.width;

    for (int k = 0; k < src.width; k++)
      src.coeff[j + k] += multiple * src.coeff[i + k];
  }

  /**
   * Exchange two rows 
   * @param a
   * @param b
   */
  private static void exchangeRows(Matrix src, int a, int b) {
    int i = a * src.width, j = b * src.width;
    for (int k = 0; k < src.width; k++, i++, j++) {
      double tmp = src.coeff[i];
      src.coeff[i] = src.coeff[j];
      src.coeff[j] = tmp;
    }
  }

  /**
   * Copy portion of one matrix to another
   * @param src source matrix
   * @param srcRow row in source matrix of first coefficient to copy
   * @param srcCol column in source matrix of first coefficient to copy
   * @param dest destination matrix
   * @param destRow row in destination matrix
   * @param destCol column in destination matrix
   * @param nRows size of copied submatrix, in rows
   * @param nCols size of copied submatrix, in columns
   */
  public static void copy(Matrix src, int srcRow, int srcCol, Matrix dest,
      int destRow, int destCol, int nRows, int nCols) {

    int si = srcRow * src.width + srcCol;
    int di = destRow * dest.width + destCol;
    for (int j = 0; j < nRows; j++) {
      for (int i = 0; i < nCols; i++) {
        dest.coeff[di + i] = src.coeff[si + i];
      }
      di += dest.width;
      si += src.width;
    }
  }

  /**
   * Construct a matrix as a submatrix of another
   * @param srcRow
   * @param srcCol
   * @param nRows
   * @param nCols
   * @return submatrix
   */
  public Matrix subMatrix(int srcRow, int srcCol, int nRows, int nCols) {
    Matrix n = new Matrix(nRows, nCols);
    Matrix.copy(this, srcRow, srcCol, n, 0, 0, nRows, nCols);
    return n;
  }

  /**
   * Construct a matrix from a column of another
   * @param srcCol column to extract
   * @return m x 1 matrix
   */
  public Matrix getColumn(int srcCol) {
    return subMatrix(0, srcCol, this.height, 1);
  }
  /**
   * Construct a matrix from a row of another
   * @param srcRow row to extract
   * @return 1 x n matrix
   */
  public Matrix getRow(int srcRow) {
    return subMatrix(srcRow, 0, 1, this.width);
  }

  /**
   * Perform Cholsky factorization of matrix
   * @return lower triangular matrix L, such that LLt equals this matrix;
   * @throws FPError if matrix is not positive definite
   */
  public Matrix factorCh() {
    final boolean db = false;
    if (db)
      Streams.out.println("luFactor:\n" + this);

    int n = width;

    // verify arguments are valid
    if (height != n)
      throw new IllegalArgumentException();

    Matrix L = new Matrix(n, n);
    for (int diag = 0; diag < n; diag++) {

      double diagElem = get(diag, diag);
      double lii;
      {
        double acc = diagElem;
        for (int k = 0; k < diag; k++) {
          double e = L.get(diag, k);
          acc -= e * e;
        }
        if (acc <= 0)
          throw new FPError("not positive definite");
        lii = Math.sqrt(acc);
        L.set(diag, diag, lii);
      }

      for (int j = diag + 1; j < n; j++) {
        double acc = get(j, diag);
        for (int k = 0; k < diag; k++) {
          acc -= L.get(j, k) * L.get(diag, k);
        }
        L.set(j, diag, acc / lii);
      }
    }
    if (db)
      Streams.out.println(" factored=\n" + L);
    return L;
  }

  /**
   * Perform LU factorization on matrix.
   * Given a square matrix, it returns an array containing 
   * a permutation array, a lower triangular matrix L, and an 
   * upper triangular matriix U.
   * @return array containing permutation array, L matrix, U matrix
   */
  public DArray factorLU() {

    final boolean db = false;
    if (db)
      Streams.out.println("luFactor:\n" + this);

    int n = width;

    // verify arguments are valid
    if (height != n)
      throw new IllegalArgumentException();

    // construct work copies of a and b, so we don't overwrite originals
    Matrix U = new Matrix(this);

    int[] perm = new int[n];
    for (int i = 0; i < n; i++)
      perm[i] = i;

    Matrix L = new Matrix(n, n);

    for (int diag = 0; diag < n - 1; diag++) {
      // find pivot element, the highest magnitude
      // element in minor starting at (col,col)
      int pivRow = -1;
      {
        if (db)
          Streams.out.println("column " + diag + ", finding pivot;\n" + U);
        double maxPivot = 0;
        for (int row = diag; row < n; row++) {
          double v = Math.abs(U.get(row, diag));
          if (v > maxPivot) {
            maxPivot = v;
            pivRow = row;
          }
        }
        if (pivRow < 0)
          throw new FPError("singular matrix");

        // exchange rows if pivot row != current column
        if (pivRow != diag) {
          if (db)
            Streams.out.println(" pivot row is " + pivRow + ", exchanging");

          int tmp = perm[diag];
          perm[diag] = perm[pivRow];
          perm[pivRow] = tmp;

          // exchange rows so pivot row is next row
          exchangeRows(U, pivRow, diag);
          exchangeRows(L, pivRow, diag);
        }
      }

      double pivRecip = -1.0 / U.get(diag, diag);

      for (int row = diag + 1; row < n; row++) {
        L.set(diag, diag, 1.0);
        double v = U.get(row, diag);
        if (v == 0)
          continue;

        v = v * pivRecip;
        L.set(row, diag, -v);
        addRowToRow(U, diag, row, v);
      }
      if (db)
        Streams.out.println("after adding multiples of row " + diag
            + " to other rows:\n" + U);
    }
    L.set(n - 1, n - 1, 1.0);
    DArray ret = DArray.build(perm, L, U);

    if (db)
      Streams.out.println("P=\n" + DArray.toString(perm) + "\nL=\n" + L
          + "\nU=\n" + U);
    return ret;
  }

  protected double[] coeff;

  protected int height, width;

  /**
   * Calculate coefficient-wise sum a+b
   * @param a
   * @param b
   * @param dest destination matrix, or null; can be the same as a or b
   * @return destination matrix
   */
  public static Matrix sum(Matrix a, Matrix b, Matrix dest) {
    if (dest == null)
      dest = new Matrix(a.height, a.width);
    if (a.width != b.width || a.height != b.height || a.width != dest.width
        || a.height != dest.height)
      throw new IllegalArgumentException();
    for (int i = 0; i < a.coeff.length; i++)
      dest.coeff[i] = a.coeff[i] + b.coeff[i];
    return dest;
  }

  /**
   * Calculate coefficient-wise difference a - b
   * @param a
   * @param b
   * @param dest destination matrix, or null; can be the same as a or b
   * @return destination matrix
   */
  public static Matrix diff(Matrix a, Matrix b, Matrix dest) {
    if (dest == null)
      dest = new Matrix(a.height, a.width);
    if (a.width != b.width || a.height != b.height || a.width != dest.width
        || a.height != dest.height)
      throw new IllegalArgumentException();
    for (int i = 0; i < a.coeff.length; i++)
      dest.coeff[i] = a.coeff[i] - b.coeff[i];
    return dest;
  }

  /**
   * Construct AffineTransform from 2d transformation matrix
   * @return
   */
  public AffineTransform toAffineTransform() {
    if (width != 3 || height != 3)
      throw new IllegalArgumentException();

    AffineTransform t = new AffineTransform(coeff[0], coeff[3], coeff[1],
        coeff[4], coeff[2], coeff[5]);

//    Streams.out.println("cvt to affine transform:\n" + this + "\n returning:\n"
//        + t);
    return t;
  }

}
