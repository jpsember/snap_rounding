package base;

/**
 * Matrix functions for 3d graphics
 */
public class Matrix3d {
  private static final int HSIZE = 4;

  /**
   * Multiply a homogeneous matrix by a 3d point
   * @param m
   * @param x
   * @param y
   * @param z 3d point
   * @param dest where to store result, or null 
   * @return result
   */
  public static FPoint3 apply(Matrix m, double x, double y, double z,
      FPoint3 dest) {

    if (dest == null)
      dest = new FPoint3();

    if (m.width != 4 || m.height != 4)
      throw new IllegalArgumentException();

    double[] vm = m.coeff;

    //    double x = pt.x(), y = pt.y(), z = pt.z();

    dest.set(//
        vm[HSIZE * 0 + 0] * x + vm[HSIZE * 0 + 1] * y + vm[HSIZE * 0 + 2] * z
            + vm[HSIZE * 0 + 3], //
        vm[HSIZE * 1 + 0] * x + vm[HSIZE * 1 + 1] * y + vm[HSIZE * 1 + 2] * z
            + vm[HSIZE * 1 + 3], //
        vm[HSIZE * 2 + 0] * x + vm[HSIZE * 2 + 1] * y + vm[HSIZE * 2 + 2] * z
            + vm[HSIZE * 2 + 3] //
    );
    return dest;
  }

  //  /**
  //   * Construct homogeneous translation matrix for a 2d or 3d point
  //   * @param pt
  //   * @param neg
  //   * @return 3x3 or 4x4 matrix
  //   * @deprecated
  //   */
  //  public static Matrix translationMatrix(IVector pt, boolean neg) {
  //    Matrix m = null;
  //    switch (pt.size()) {
  //    default:
  //      throw new IllegalArgumentException();
  //    case 2:
  //      {
  //        m = new Matrix(3);
  //        m.setIdentity();
  //        //Matrix.identity(3, null);
  //        double[] v = m.coeff();
  //        double sign = neg ? -1 : 1;
  //        v[PSIZE * 0 + PSIZE - 1] = pt.x() * sign;
  //        v[PSIZE * 1 + PSIZE - 1] = pt.y() * sign;
  //
  //      }
  //      break;
  //    case 3:
  //      {
  //        m = new Matrix(4);
  //        m.setIdentity();
  //        //        m = Matrix.identity(4, null);
  //        double[] v = m.coeff();
  //        double sign = neg ? -1 : 1;
  //        v[HSIZE * 0 + HSIZE - 1] = pt.x() * sign;
  //        v[HSIZE * 1 + HSIZE - 1] = pt.y() * sign;
  //        v[HSIZE * 2 + HSIZE - 1] = pt.z() * sign;
  //      }
  //      break;
  //    }
  //    return m;
  //  }

  /**
   * Apply translation to 3d homogeneous matrix
   * @param m matrix to modify
   * @param x
   * @param y
   * @param z translation
   */
  public static void translate(Matrix m, double x, double y, double z) {
    double[] v = m.coeff();
    v[0 * HSIZE + HSIZE - 1] += x;
    v[1 * HSIZE + HSIZE - 1] += y;
    v[2 * HSIZE + HSIZE - 1] += z;
  }
  /**
   * Apply translation to 3d homogeneous matrix
   * @param m matrix to modify
   * @param t translation vector (3d point)
   */
  public static void translate(Matrix m, IVector t) {
    translate(m, t.x(), t.y(), t.z());
  }

  /**
   * Get 3d x-rotation matrix
   * @param ang angle of rotation around x axis
   * @return 4x4 homogeneous matrix
   */
  public static Matrix getRotateX(double ang) {

    Matrix m = new Matrix(HSIZE, HSIZE);
    double[] v = m.coeff();
    double c = Math.cos(ang), s = Math.sin(ang);

    v[HSIZE * 0 + 0] = 1.0;
    v[HSIZE * 3 + 3] = 1.0;

    v[HSIZE * 1 + 1] = c;
    v[HSIZE * 1 + 2] = -s;
    v[HSIZE * 2 + 2] = c;
    v[HSIZE * 2 + 1] = s;
    return m;
  }

  /**
   * Get matrix for 3d z-rotation
   * @param ang angle of rotation around z axis
   * @return 4x4 homogenous matrix
   */
  public static Matrix getRotateZ(double ang) {
    Matrix m = new Matrix(HSIZE, HSIZE);
    double[] v = m.coeff();

    double c = Math.cos(ang), s = Math.sin(ang);

    v[HSIZE * 2 + 2] = 1.0;
    v[HSIZE * 3 + 3] = 1.0;
    v[HSIZE * 0 + 0] = c;
    v[HSIZE * 0 + 1] = -s;
    v[HSIZE * 1 + 0] = c;
    v[HSIZE * 1 + 1] = s;
    return m;
  }

  /**
   * Get matrix for 3d y-rotation
   * @param ang angle of rotation around y axis
   * @return 4x4 homogenous matrix
   */
  public static Matrix getRotateY(double ang) {
    Matrix m = new Matrix(HSIZE, HSIZE);
    double[] v = m.coeff();
    double c = Math.cos(ang), s = Math.sin(ang);

    v[HSIZE * 1 + 1] = 1.0;
    v[HSIZE * 3 + 3] = 1.0;

    v[HSIZE * 2 + 2] = c;
    v[HSIZE * 2 + 0] = -s;
    v[HSIZE * 0 + 0] = c;
    v[HSIZE * 0 + 2] = s;
    return m;
  }

  /**
   * Get combined 3d rotation matrix
   * @param pRot planar rotation
   * @param iRot incline rotation
   * @param dest destination matrix, or null
   * @return destination matrix
   */
  public static Matrix getRotation(double pRot, double iRot, Matrix dest) {
    Matrix mIRot = getRotateX(iRot);
    Matrix mPlRot = getRotateY(pRot);
    return Matrix.mult(mPlRot, mIRot, dest);
  }

  /**
   * Get inverse of combined 3d rotation matrix; see {@link #getRotation(double, double, Matrix)}
   * @param pRot planar rotation
   * @param iRot incline rotation
   * @param dest destination matrix, or null
   * @return destination matrix
   */
  public static Matrix getRotationInv(double pRot, double iRot, Matrix dest) {
    Matrix mIRot = getRotateX(-iRot);
    Matrix mPlRot = getRotateY(-pRot);
    return Matrix.mult(mIRot, mPlRot, dest);
  }

  public static Matrix scale(Matrix src, double s, Matrix dest) {
    return scale(src, s, s, s, dest);
  }

  /**
   * Scale a 3D homogeneous matrix
   * @param source
   * @param sx
   * @param sy
   * @param sz
   * @param dest destination matrix, or null
   * @return scaled matrix
   */
  public static Matrix scale(Matrix source, double sx, double sy, double sz,
      Matrix dest) {

    if (dest == null)
      dest = new Matrix(source);

    double[] v = source.coeff();
    double[] d = dest.coeff();

    for (int i = 0; i < HSIZE - 1; i++) {
      d[i + 0] = v[i + 0] * sx;
      d[i + HSIZE * 1] = v[i + HSIZE * 1] * sy;
      d[i + HSIZE * 2] = v[i + HSIZE * 2] * sz;
    }
    return dest;
  }

  /**
   * Determine planar, incline rotations necessary to 
   * rotate a vector away from the z axis.
   * Rotations occur in two steps:
   *   [] rotate around y axis
   *   [] rotate around x' axis (x' = x axis after rotating around y)
   * @param n  direction of z axis after rotations
   * @param rot  if not null, planar rotation and incline rotation stored here
   * @return rotation matrix
   */
  public static Matrix getRotationsFor(IVector n, double[] rot) {
    n.normalize();
    double plAng = 0;
    double iAng = 0;
    double x = n.x(), y = n.y(), z = n.z();
    if (x != 0 || z != 0) {
      plAng = Math.atan2(x, z);
      // rotation around x' axis rotates y axis towards z' axis
      iAng = -Math.asin(y);
    }
    if (rot != null) {
      rot[0] = plAng;
      rot[1] = iAng;
    }

    return getRotation(plAng, iAng, null);
  }

  /**
   * Determine inverse of rotations and matrix calculated by getRotationsFor()
   * @param n : plane normal vector
   * @param rot : if not null, planar rotation and incline rotation stored here
   * @return rotation matrix
   */
  public static Matrix getRotationsForInv(IVector n, double[] rot) {
    n.normalize();
    double plAng = 0;
    double iAng = 0;

    double x = n.x(), y = n.y(), z = n.z();
    if (x != 0 || z != 0) {
      plAng = Math.atan2(x, z);
      // rotation around x' axis rotates y axis towards z' axis
      iAng = -Math.asin(y);
    }
    if (rot != null) {
      rot[0] = plAng;
      rot[1] = iAng;
    }
    return getRotationInv(plAng, iAng, null);
  }

  /**
   * Construct matrix for transforming planar point to 3d
   * Transforms pt [s,t,0] by multipliying [4x4] homogenous matrix by [s,t,0]
   *   (ignoring last row)
   * @param pt  point on plane
   * @param norm  normal for plane
   * @param dest  destination matrix, or null
   * @return destination matrix
   */
  public static Matrix getRotatedPlane(IVector pt, IVector norm, Matrix dest) {
    // construct a 2d parameterization on this plane
    Matrix mat = getRotationsFor(norm, null);
    Matrix m2 = getTranslate(pt, false);
    return Matrix.mult(m2, mat, dest);
  }

  /**
  * Construct 3d translation matrix
  * @param pt translation 
  * @param neg if true, negates translation
  * @return 3d matrix
  */
  public static Matrix getTranslate(IVector pt, boolean neg) {
    //    switch (pt.size()) {
    //    default:
    //      throw new IllegalArgumentException();
    //    case 2:
    //      {
    //        m = new Matrix(3);
    //        m.setIdentity();
    //        //Matrix.identity(3, null);
    //        double[] v = m.coeff();
    //        double sign = neg ? -1 : 1;
    //        v[PSIZE * 0 + PSIZE - 1] = pt.x() * sign;
    //        v[PSIZE * 1 + PSIZE - 1] = pt.y() * sign;
    //
    //      }
    //      break;
    //    case 3:
    //      {
    Matrix m = new Matrix(4);
    m.setIdentity();
    //        m = Matrix.identity(4, null);
    double[] v = m.coeff();
    double sign = neg ? -1 : 1;
    v[HSIZE * 0 + HSIZE - 1] = pt.x() * sign;
    v[HSIZE * 1 + HSIZE - 1] = pt.y() * sign;
    v[HSIZE * 2 + HSIZE - 1] = pt.z() * sign;
    return m;
  }

  /**
   * Construct matrix for transforming point on plane to 3d point
   * @param origin origin of plane, in world space
   * @param plRot planar rotation 
   * @param iRot incline rotation
   * @param dest destination matrix, or null
   * @return destination matrix
   */
  public static Matrix getRotatedPlane(IVector origin, double plRot,
      double iRot, Matrix dest) {
    Matrix mat = getRotation(plRot, iRot, null);
    Matrix m2 = getTranslate(origin, false);
    return Matrix.mult(m2, mat, dest);
  }

  /**
   * Construct matrix for transforming 3d point to plane
   * @param pt  point on plane
   * @param norm  normal for plane
   * @param rotations  if not null, rotations are stored here
   * @param dest  destination matrix, or null
   * @return destination matrix
   */
  public static Matrix getRotatedPlaneInv(IVector pt, IVector norm,
      double[] rotations, Matrix dest) {
    // construct a 2d parameterization on this plane
    Matrix mat = getRotationsForInv(norm, rotations);
    Matrix m2 = getTranslate(pt, true);
    return Matrix.mult(mat, m2, dest);
  }

  /**
   * Get matrix for transforming from 3d back to plane
   * @param origin origin of plane, in world space
   * @param plRot planar rotation 
   * @param iRot incline rotation
   * @param dest destination matrix, or null
   * @return destination matrix
   * @see #getRotatedPlane(IVector, double, double, Matrix)
   */
  public static Matrix getRotatedPlaneInv(IVector origin, double plRot,
      double iRot, Matrix dest) {

    Matrix mat = getRotationInv(plRot, iRot, null);
    Matrix m2 = getTranslate(origin, true);
    return Matrix.mult(mat, m2, dest);

  }
  /**
   * Transform 3d point.  Matrix must be 4x4 homogeneous matrix
   * @param pt 3d point
   * @param dest where to store result, or null 
   * @return result
   */
  public static FPoint3 apply(Matrix m, IVector pt, FPoint3 dest) {
    return apply(m, pt.x(), pt.y(), pt.z(), dest);
  }

  /**
   * Rotate 3D homogeneous matrix around X axis
   * @param m matrix to rotate
   * @param ang rotation angle
   */
  public static void rotateX(Matrix m, double ang) {
    Matrix.mult(getRotateX(ang), m, m);
  }

  /**
   * Rotate 3D homogeneous matrix around Y axis
   * @param m matrix to rotate
   * @param ang rotation angle
   */
  public static void rotateY(Matrix m, double ang) {
    Matrix.mult(getRotateY(ang), m, m);
  }

  /**
   * Rotate 3D homogeneous matrix around Z axis
   * @param m matrix to rotate
   * @param ang rotation angle
   */
  public static void rotateZ(Matrix m, double ang) {
    Matrix.mult(getRotateZ(ang), m, m);
  }

  private static double[] identityCoeff = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0,
      0, 0, 0, 1, };

  public static Matrix getIdentity() {

    return new Matrix(4, 4, identityCoeff);
    //    // TODO Auto-generated method stub
    //    return null;
  }

}
