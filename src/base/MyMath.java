package base;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Various utility math functions
 */
public class MyMath {
  /**
   * Square a value
   * @param amt
   * @return amt^2
   */
  public static double sq(double amt) {
    return amt * amt;
  }

  /**
   * Convert an angle from degrees to radians
   * @param degrees angle in degrees
   * @return angle in radians
   */
  public static double radians(double degrees) {
    return degrees * ((2 * Math.PI) / 360);
  }

  /**
   * Convert an angle from radians to degrees
   * @param radians angle in radians
   * @return angle in degrees
   */
  public static double degrees(double radians) {
    return radians * (360 / (2 * Math.PI));
  }

  /**
   * Generate a random value
   * @param n range
   * @return random value in range [0,n)
   */
  public static double rnd(double n) {
    double d = random.nextDouble();
    return d * n;
  }

  /**
   * Seed the random number generator
   * @param seed 
   */
  public static Random seed(int seed) {
    if (seed == 0)
      random = new Random();
    else
      random = new Random(seed);
    return random;
  }

  /**
   * Generate a random integer
   * @param m range
   * @return random integer in range [0,m)
   */
  public static int rnd(int m) {
    int n = mod(random.nextInt(), m);
    return n;
  }

  /**
   * Clamp a value into range
   * @param value
   * @param min
   * @param max
   * @return clamped value
   */
  public static int clamp(int value, int min, int max) {
    if (value < min)
      value = min;
    else if (value > max)
      value = max;
    return value;
  }

  /**
   * Clamp a value into range
   * @param value
   * @param min
   * @param max
   * @return clamped value
   */
  public static double clamp(double value, double min, double max) {
    if (value < min)
      value = min;
    else if (value > max)
      value = max;
    return value;
  }
  
  /**
   * Clamp a value into range
   * @param value
   * @param min
   * @param max
   * @return clamped value
   */
  public static float clamp(float value, float min, float max) {
    if (value < min)
      value = min;
    else if (value > max)
      value = max;
    return value;
  }

  //  /**
  //   * Determine which side of a line a point is on
  //   * @param a : first point on line
  //   * @param b : second point on line
  //   * @param pt : point to test
  //   * @return 0 if pt is on the line containing the ray from a to b,
  //   *  1 if it's to the left of this ray, -1 if it's to the right
  //   */
  //  public static int sideOfLine(Point a, Point b, Point pt) {
  //    return sideOfLine(a.x, a.y, b.x, b.y, pt.x, pt.y);
  //  }
  /**
   * Determine which side of a line a point is on
   * @param a : first point on line
   * @param b : second point on line
   * @param pt : point to test
   * @return 0 if pt is on the line containing the ray from a to b,
   *  1 if it's to the left of this ray, -1 if it's to the right
   */
  public static int sideOfLine(IPoint2 a, IPoint2 b, IPoint2 pt) {
    return sideOfLine(a.x, a.y, b.x, b.y, pt.x, pt.y);
  }

  /**
   * Determine which side of a line a point is on; floating point version
   * @param ax
   * @param ay first point on line
   * @param bx
   * @param by second point on line
   * @param px
   * @param py point to test
   * @return 0 if the point is on the line containing the ray from a to b,
   *  positive value if it's to the left of this ray, negative if it's to the right
   */
  public static double sideOfLine(double ax, double ay, double bx, double by,
      double px, double py) {
    double area2 = ((bx - ax)) * (py - ay) - ((px - ax)) * (by - ay);
    return area2;
  }
  /**
   * Determine which side of a line a point is on; floating point version
   * @param a first point on line
   * @param b second point on line
   * @param p point to test
   * @return 0 if the point is on the line containing the ray from a to b,
   *  positive value if it's to the left of this ray, negative if it's to the right
   */
  public static double sideOfLine(FPoint2 a, FPoint2 b, FPoint2 p) {
    return sideOfLine(a.x, a.y, b.x, b.y, p.x, p.y);
  }

  /**
   * Determine which side of a line a point is on
   * @param ax 
   * @param ay first point on line
   * @param bx
   * @param by   second point on line
   * @param px
   * @param py point to test
   * @return 0 if the point is on the line containing the ray from a to b,
   *  1 if it's to the left of this ray, -1 if it's to the right
   */
  public static int sideOfLine(int ax, int ay, int bx, int by, int px, int py) {
    long area2 = ((long) (bx - ax)) * (py - ay) - ((long) (px - ax))
        * (by - ay);
    int pos = 0;
    if (area2 < 0)
      pos = -1;
    if (area2 > 0)
      pos = 1;
    return pos;
  }

  /**
   * Normalize an angle by replacing it, if necessary, with an
   * equivalent angle in the range [-PI,PI)
   *
   * @param a  angle to normalize
   * @return an equivalent angle in [-PI,PI)
   */
  public static double normalizeAngle(double a) {
    return mod(a + Math.PI, Math.PI * 2) - Math.PI;
  }

  /**
   * Interpolate between two angles, taking shortest way around circle
   * @param a1
   * @param a2
   * @param t
   * @return
   */
  public static double interpAngle(double a1, double a2, double t) {
    a1 = normalizeAngle(a1);
    a2 = normalizeAngle(a2);
    double adiff = normalizeAngle(a2 - a1);
    return normalizeAngle(a1 + adiff * t);
  }

  /**
   * Normalize an angle by replacing it, if necessary, with an
   * equivalent angle in the range [0,2*PI)
   *
   * @param a  angle to normalize
   * @return an equivalent angle in [0,2*PI)
   */
  public static double normalizeAnglePositive(double a) {
    double r = normalizeAngle(a);
    if (r < 0)
      r += Math.PI * 2;
    return r;
  }
  /**
   * Calculate modulus of a value, with proper treatment of negative values
   * @param value
   * @param divisor
   * @return value - v', where v' is the largest multiple of divisor not
   *  greater than value
   */
  public static int mod(int value, int divisor) {
    value = value % divisor;
    if (value < 0)
      value += divisor;
    return value;
  }

  /**
   * Integer division, which behaves appropriately for negative numbers.
   * @param numerator
   * @param denominator
   * @return floor of numerator / denominator
   */
  public static int floor(int numerator, int denominator) {
    if (denominator < 0) {
      denominator = -denominator;
      numerator = -numerator;
    }
    int res;
    if (numerator < 0) {
      res = -(-numerator + denominator - 1) / denominator;
    } else {
      res = numerator / denominator;
    }
    return res;
  }

  /**
   * Calculate modulus of a value, with proper treatment of negative values
   * @param value
   * @param divisor
   * @return value - v', where v' is the largest multiple of divisor not
   *  greater than value
   */
  public static final double mod(double value, double divisor) {
    return (value - divisor * Math.floor(value / divisor));
  }

  /**
  * Calculate the angle that a vector makes with the x-axis
  * @param pt0 : start point
  * @param pt1 : end point
  * @return normalized angle [-PI...PI) for vector (pt1 - pt0)
  */
  public static double polarAngle(FPoint2 pt0, FPoint2 pt1) {
    return polarAngle(pt0.x, pt0.y, pt1.x, pt1.y);
    //    return Math.atan2(pt1.y - pt0.y, pt1.x - pt0.x);
  }

  /**
   * Calculate the angle that a vector makes with the x-axis
   * @param x0,y0  start point
   * @param x1,y1 end point
   * @return normalized angle [-PI...PI) for vector 
   */
  public static double polarAngle(double x0, double y0, double x1, double y1) {
    return Math.atan2(y1 - y0, x1 - x0);
  }

  /**
   * Calculate the angle that a vector makes with the x-axis
   * @param pt end point (start is origin)
   * @return normalized angle [-PI...PI)  
   */
  public static double polarAngle(FPoint2 pt) {
    return Math.atan2(pt.y, pt.x);
  }

  /**
  * Calculate the circumcenter of three points.
  * @param a first point
  * @param b second point
  * @param c third point
  * @return tuple <radius, center>
  */
  public static DArray calcCircumCenter(FPoint2 a, FPoint2 b, FPoint2 c) {

    double rad = 0;
    FPoint2 dest = new FPoint2();

    if (Math.abs(b.x - a.x) > Math.abs(b.y - a.y)) {
      dest.y = (((c.x * c.x - a.x * a.x) + (c.y * c.y - a.y * a.y))
          * (b.x - a.x) + (a.x - c.x)
          * ((b.y * b.y - a.y * a.y) + (b.x * b.x - a.x * a.x)))
          / (2 * (c.y - a.y) * (b.x - a.x) + 2 * (c.x - a.x) * (a.y - b.y));
      dest.x = ((b.y * b.y - a.y * a.y) + (b.x * b.x - a.x * a.x) + 2
          * (a.y - b.y) * dest.y)
          / (2 * (b.x - a.x));
    } else {
      dest.x = (((c.y * c.y - a.y * a.y) + (c.x * c.x - a.x * a.x))
          * (b.y - a.y) + (a.y - c.y)
          * ((b.x * b.x - a.x * a.x) + (b.y * b.y - a.y * a.y)))
          / (2 * (c.x - a.x) * (b.y - a.y) + 2 * (c.y - a.y) * (a.x - b.x));
      dest.y = ((b.x * b.x - a.x * a.x) + (b.y * b.y - a.y * a.y) + 2
          * (a.x - b.x) * dest.x)
          / (2 * (b.y - a.y));
    }
    rad = FPoint2.distance(a, dest);

    return DArray.build(new Double(rad), dest);
  }

  /**
   * Calculate point on boundary of circle
   * @param origin origin of circle
   * @param angle angle of rotation
   * @param radius radius of circle
   * @param dest destination point, or null
   * @return destination point
   */
  public static FPoint2 ptOnCircle(FPoint2 origin, double angle, double radius,
      FPoint2 dest) {
    if (dest == null)
      dest = new FPoint2();
    dest.setLocation(origin.x + Math.cos(angle) * radius, origin.y
        + Math.sin(angle) * radius);
    return dest;
  }

  /**
   * Calculate point on boundary of circle
   * @param origin origin of circle
   * @param angle angle of rotation
   * @param radius radius of circle
   * @return point
   */
  public static FPoint2 ptOnCircle(FPoint2 origin, double angle, double radius) {
    return ptOnCircle(origin, angle, radius, null);
  }

  /**
   * Clip a line segment to a rectangle
   * @param p0
   * @param p1 endpoints of line segment
   * @param r rectangle
   * @return true if some portion of segment remains after clipping
   */
  public static boolean clipSegmentToRect(FPoint2 p0, FPoint2 p1, FRect r) {

    final boolean db = false;

    if (db)
      Streams.out
          .println("clipSegmentToRect " + p0 + " ... " + p1 + " to " + r);

    boolean valid = true;
    while (true) {
      int f0 = clipFlags(p0, r), f1 = clipFlags(p1, r);

      if (db)
        Streams.out.println(" f0=" + Tools.fBits(f0, 4) + " f1="
            + Tools.fBits(f1, 4));

      if ((f0 & f1) != 0) {
        valid = false;
        break;
      }
      if (f0 == 0 && f1 == 0) {
        break;
      }

      FPoint2 pt = (f0 == 0) ? p1 : p0;
      int cf = (f0 == 0) ? f1 : f0;

      for (int s = 0; s < 4; s++) {

        if ((cf & (1 << s)) == 0) {
          continue;
        }

        if (db)
          Streams.out.println(" clipping to side " + s);

        switch (s) {
        case 0:
        case 1:
          {
            double cx = (s == 0) ? r.x : r.endX();
            double t = (cx - p0.x) / (p1.x - p0.x);
            pt.setLocation(cx, (p1.y - p0.y) * t + p0.y);
          }
          break;

        case 2:
        case 3:
          {
            double cy = (s == 2) ? r.y : r.endY();
            double t = (cy - p0.y) / (p1.y - p0.y);
            pt.setLocation((p1.x - p0.x) * t + p0.x, cy);
          }
          break;
        }
        if (db)
          Streams.out.println(" now " + pt);

        break;
      }
    }
    return valid;
  }

  private static int clipFlags(FPoint2 pt, FRect r) {
    int f = 0;
    if (pt.x < r.x) {
      f |= 1;
    }
    if (pt.x > r.endX()) {
      f |= 2;
    }
    if (pt.y < r.y) {
      f |= 4;
    }
    if (pt.y > r.endY()) {
      f |= 8;
    }
    return f;
  }

  /**
   * Determine intersection point (if one exists) between two line segments
   * @param p1 
   * @param p2 endpoints of first segment
   * @param q1
   * @param q2 endpoints of second segment
   * @param iParam if not null, parameters of intersection point returned here
   * @return FPoint2 if they properly intersect (if parallel or coincident,
   *  or if they intersect outside of either segment range, returns null)
   */
  public static FPoint2 lineSegmentIntersection(FPoint2 p1, FPoint2 p2,
      FPoint2 q1, FPoint2 q2, double[] iParam) {
    return lineSegmentIntersection(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y, q2.x,
        q2.y, iParam);

  }
  /**
   * Determine intersection point (if one exists) between two lines
   * @param p1 
   * @param p2 points on first line
   * @param q1
   * @param q2 points on second line
   * @param iParam if not null, parameters of intersection point returned here
   * @return FPoint2 if they properly intersect (if parallel or coincident,
   *   returns null)
   */
  public static FPoint2 linesIntersection(FPoint2 p1, FPoint2 p2, FPoint2 q1,
      FPoint2 q2, double[] iParam) {
    return linesIntersection(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y, q2.x, q2.y,
        iParam);
  }

  /**
   * Calculate width of integer in characters.  This is the number of digits
   * it will occupy, including negative sign if it is negative.
   * @param intValue
   * @return number of characters
   */
  public static int intChars(int intValue) {
    int r = 1;
    if (intValue < 0) {
      r++;
      intValue = -intValue;
    }

    int s = 10;
    while (intValue >= s && s <= Integer.MAX_VALUE / 10) {
      r++;
      s = s * 10;
    }
    return r;
  }

  /**
   * Determine intersection point (if one exists) between two lines
   * @param p1x
   * @param p1y 
   * @param p2x
   * @param p2y points on first line
   * @param q1x
   * @param q1y
   * @param q2x
   * @param q2y points on second line
   * @param param if not null, parameters of intersection point returned here
   * @return FPoint2 if they properly intersect (if parallel or coincident,
   *   returns null)
   */
  public static FPoint2 linesIntersection(double p1x, double p1y, double p2x,
      double p2y, double q1x, double q1y, double q2x, double q2y, double[] param) {
    final boolean db = false;
    FPoint2 out = null;

    final double EPS = 1e-15;

    do {
      if (db) {
        System.out.println("linesIntersection:\n " + new FPoint2(p1x, p1y)
            + " to " + new FPoint2(p2x, p2y) + "  with\n "
            + new FPoint2(q1x, q1y) + " to " + new FPoint2(q2x, q2y));
      }
      double denom = (q2y - q1y) * (p2x - p1x) - (q2x - q1x) * (p2y - p1y);
      double numer1 = (q2x - q1x) * (p1y - q1y) - (q2y - q1y) * (p1x - q1x);

      //double numer2 = (p2x - p1x)*(p1y - q1y) - (p2y -p1y)*(p1x-q1x);
      if (Math.abs(denom) < EPS) {
        break;
      }

      double ua = numer1 / denom;

      double numer2 = (p2x - p1x) * (p1y - q1y) - (p2y - p1y) * (p1x - q1x);

      double ub = numer2 / denom;
      if (db)
        Streams.out.println(" numer1=" + numer1 + " denom=" + denom
            + "\n numer2=" + numer2 + " ua=" + ua + " ub=" + ub);

      if (param != null) {
        param[0] = ua;
        param[1] = ub;
      }

      out = new FPoint2(p1x + ua * (p2x - p1x), p1y + ua * (p2y - p1y));

      if (db) {
        System.out.println(" ua=" + ua + "\n ipt=" + out);
      }
    } while (false);
    if (db)
      Streams.out.println(" returning out=" + out);

    return out;
  }

  /**
   * Determine intersection point (if one exists) between two line segments
   * @param p1x
   * @param p1y 
   * @param p2x
   * @param p2y endpoints of first segment
   * @param q1x
   * @param q1y
   * @param q2x
   * @param q2y endpoints of second segment
   * @param param if not null, parameters of intersection point returned here
   * @return FPoint2 if they properly intersect (if parallel or coincident,
   *  or if they intersect outside of either segment range, returns null)
   */
  public static FPoint2 lineSegmentIntersection(double p1x, double p1y,
      double p2x, double p2y, double q1x, double q1y, double q2x, double q2y,
      double[] param) {

    final double EPS = 1e-5;

    final boolean db = false;
    FPoint2 out = null;
    do {
      if (db) {
        System.out.println("lineIntersection:\n " + new FPoint2(p1x, p1y)
            + " to " + new FPoint2(p2x, p2y) + "  with\n "
            + new FPoint2(q1x, q1y) + " to " + new FPoint2(q2x, q2y));
      }
      double denom = (q2y - q1y) * (p2x - p1x) - (q2x - q1x) * (p2y - p1y);
      double numer1 = (q2x - q1x) * (p1y - q1y) - (q2y - q1y) * (p1x - q1x);
      //double numer2 = (p2x - p1x)*(p1y - q1y) - (p2y -p1y)*(p1x-q1x);
      if (Math.abs(denom) < EPS) {
        break;
      }

      double ua = numer1 / denom;

      double numer2 = (p2x - p1x) * (p1y - q1y) - (p2y - p1y) * (p1x - q1x);

      double ub = numer2 / denom;
      if (db)
        Streams.out.println(" numer1=" + numer1 + " denom=" + denom
            + "\n numer2=" + numer2 + " ua=" + ua + " ub=" + ub);

      if (param != null) {
        param[0] = ua;
        param[1] = ub;
      }
      if (ua < -EPS || ua > 1+EPS) {
        if (db)
          Streams.out.println(" ua not in range, breaking");

        break;
      }
      if (ub < -EPS || ub > 1+EPS) {
        if (db)
          Streams.out.println(" ub not in range, breaking");

        break;
      }

      //double ub = numer2/denom;
      out = new FPoint2(p1x + ua * (p2x - p1x), p1y + ua * (p2y - p1y));

      if (db) {
        System.out.println(" ua=" + ua + "\n ipt=" + out);
      }
    } while (false);
    if (db)
      Streams.out.println(" returning out=" + out);

    return out;
  }

  /**
  * Test which side of a directed line a point is on.  Note that
  * if it detects the point is on the line, it doesn't say whether
  * it is on the segment defining the line.
  * @param s0   start of line segment
  * @param s1   end of line segment
  * @param pt   point to test
  * @return positive if to left, negative if to right, zero if on the line
  */
  public static double sideOfSegment(FPoint2 pt, FPoint2 s0, FPoint2 s1) {
    double px = pt.x - s0.x, py = pt.y - s0.y, sx = s1.x - s0.x, sy = s1.y
        - s0.y;
    return sx * py - sy * px;
  }

  public static int sideOfSegment(int px, int py, int s0x, int s0y, int s1x,
      int s1y) {
    px -= s0x;
    py -= s0y;
    s1x -= s0x;
    s1y -= s0y;
    return s1x * py - s1y * px;
  }

  /**
  * Calculate the parameter for a point on a line
  * @param pt FPoint2, assumed to be on line
  * @param s0 start point of line segment (t = 0.0)
  * @param s1 end point of line segment (t = 1.0)
  * @return t value associated with pt
  */
  public static double positionOnSegment(FPoint2 pt, FPoint2 s0, FPoint2 s1) {

    final boolean db = false;

    if (db)
      Streams.out.println("positionOnSegment pt=" + pt + ", seg=" + s0 + "..."
          + s1);

    double sx = s1.x - s0.x;
    double sy = s1.y - s0.y;

    double t = 0;

    double dotProd = (pt.x - s0.x) * sx + (pt.y - s0.y) * sy;
    if (dotProd != 0)
      t = dotProd / (sx * sx + sy * sy);

    return t;
  }

  /**
  * Determine distance of point from segment
  * @param pt FPoint2
  * @param l0 FPoint2
  * @param l1 FPoint2
  * @param ptOnSeg  if not null, closest point on segment to point is stored here
  * @return double
  */
  public static double ptDistanceToSegment(FPoint2 pt, FPoint2 l0, FPoint2 l1,
      FPoint2 ptOnSeg) {
    final boolean db = false;

    if (db)
      Streams.out.println("ptDistanceToSegment " + pt + " seg=[" + l0 + "..."
          + l1 + "]");

    double dist = 0;
    // calculate parameter for position on segment
    double t = positionOnSegment(pt, l0, l1);

    FPoint2 cpt = null;
    if (t < 0) {
      cpt = l0;
      dist = FPoint2.distance(pt, cpt);
      if (ptOnSeg != null)
        ptOnSeg.setLocation(cpt);
    } else if (t > 1) {
      cpt = l1;
      dist = FPoint2.distance(pt, cpt);
      if (ptOnSeg != null)
        ptOnSeg.setLocation(cpt);
    } else {
      dist = ptDistanceToLine(pt, l0, l1, ptOnSeg);
    }
    if (db)
      Streams.out.println(" t=" + t + " dist=" + dist);

    return dist;
  }

  /**
  * Determine distance of a point from a line
  * @param pt FPoint2
  * @param e0   one point on line
  * @param e1   second point on line
  * @param closestPt if not null, closest point on line is stored here
  * @return distance 
  */
  public static double ptDistanceToLine(FPoint2 pt, FPoint2 e0, FPoint2 e1,
      FPoint2 closestPt) {

    /*
     *  Let A = pt - l0
     *      B = l1 - l0
     *      
     *  then
     *  
     *      |A x B| = |A||B| sin t
     *      
     *  and the distance is |AxB| / |B|
     *  
     *  
     *  The closest point is
     *  
     *     l0 + (|A| cos t) / |B|
     */
    double bLength = FPoint2.distance(e0, e1);
    double dist;
    if (bLength == 0) {
      dist = FPoint2.distance(pt, e0);
      if (closestPt != null)
        closestPt.setLocation(e0.x, e0.y);
    } else {
      double ax = pt.x - e0.x;
      double ay = pt.y - e0.y;
      double bx = e1.x - e0.x;
      double by = e1.y - e0.y;

      double crossProd = bx * ay - by * ax;

      dist = Math.abs(crossProd / bLength);

      if (closestPt != null) {
        double scalarProd = ax * bx + ay * by;
        double t = scalarProd / (bLength * bLength);
        closestPt.set(e0.x + t * bx, e0.y + t * by);
      }
    }
    return dist;
  }

  /**
   * Snap a point to a grid
   * @param x
   * @param y point
   * @param size size of grid cells (assumed to be square)
   * @return point snapped to nearest cell corner
   */
  public static FPoint2 snapToGrid(double x, double y, double size) {
    FPoint2 ret = null;
    ret = new FPoint2(snapToGrid(x, size), snapToGrid(y, size));
    return ret;
  }

  /**
   * Snap a scalar to a grid
   * @param n scalaar
   * @param size size of grid cells (assumed to be square)
   * @return point snapped to nearest cell corner
   */
  public static double snapToGrid(double n, double size) {
    return size * Math.round(n / size);
  }

  /**
   * Snap a point to a grid
   * @param pt point
   * @param size size of grid cells (assumed to be square)
   * @return point snapped to nearest cell corner
   */
  public static FPoint2 snapToGrid(FPoint2 pt, double size) {
    return snapToGrid(pt.x, pt.y, size);
  }

  /**
   * Determine sign of an integer
   * @param i
   * @return -1,0,+1
   */
  public static int sign(int i) {
    if (i < 0)
      i = -1;
    else if (i > 0)
      i = 1;
    return i;
  }

  // ------------- Melkman's algorithm -------------------
  //
  // For a debugging version of this, see TestBinSearchOper.

  private static FPoint2 pt(List poly, int index) {
    return (FPoint2) poly.get(index);
  }
  private static int bottom(DQueue q, int dist) {
    return q.peekInt(dist, false);
  }
  private static int top(DQueue q, int dist) {
    return q.peekInt(dist, true);
  }

  public static boolean left(FPoint2 p0, FPoint2 p1, FPoint2 p2) {
    return MyMath.sideOfLine(p0, p1, p2) > 0;
  }
  public static boolean right(FPoint2 p0, FPoint2 p1, FPoint2 p2) {
    return MyMath.sideOfLine(p0, p1, p2) < 0;
  }

  /**
   * Compute the convex hull of a simple polygon, in linear time,
   * using Melkman's algorithm.
   * 
   * @param polygon list of FPoint2's
   * @return indexes of points on convex hull
   */
  public static DArray convexHullOfPoly(List polygon) {
    DQueue q = new DQueue();

    int size = polygon.size();
    if (size < 3) {
      for (int i = 0; i < polygon.size(); i++)
        q.push(i);
    } else {

      if (left(pt(polygon, 0), pt(polygon, 1), pt(polygon, 2))) {
        q.push(2);
        q.push(1);
        q.push(0);
        q.push(2);
      } else {
        q.push(2);
        q.push(0);
        q.push(1);
        q.push(2);
      }

      for (int j = 3; j < polygon.size(); j++) {
        FPoint2 vi = pt(polygon, j);
        FPoint2 dt1 = pt(polygon, top(q, 1));
        FPoint2 dt0 = pt(polygon, top(q, 0));
        FPoint2 db0 = pt(polygon, bottom(q, 0));
        FPoint2 db1 = pt(polygon, bottom(q, 1));

        if (left(dt1, dt0, vi) && left(vi, db0, db1))
          continue;

        while (!left(dt1, dt0, vi)) {
          q.popInt(true);
          dt0 = dt1;
          dt1 = pt(polygon, top(q, 1));
        }
        q.push(j, true);

        while (!left(vi, db0, db1)) {
          q.popInt(false);
          db0 = db1;
          db1 = pt(polygon, bottom(q, 1));
        }
        q.push(j, false);
      }
    }
    if (q.isEmpty()) {
      StringBuilder sb = new StringBuilder(
          "problem with convex hull of polygon:\n");
      for (Iterator it = polygon.iterator(); it.hasNext();) {
        FPoint2 pt = (FPoint2) it.next();
        sb.append(" " + pt.x + " " + pt.y + "\n");
      }
      Tools.warn(sb.toString());
    } else {
      // pop the duplicate vertex
      q.pop(false);
    }

    DArray ret = new DArray();
    while (!q.isEmpty()) {
      ret.add(q.pop(false));
    }
    return ret;
  }

  /**
   * Calculate convex hull of a set of points
   * @param pt1  DArray of IVectors
   * @return array of ints, indexes of points on hull, in ccw order, 
   *   lexicographically lowest point first
   */
  public static DArray convexHull(DArray pt1) {
    final boolean db = false;
    if (db)
      Streams.out.println("\n\nConvex hull, Jarvis March");

    // perform Jarvis march.

    // Create array of PtRecs, so we track original indices of points
    PtArray a = new PtArray();
    for (int i = 0; i < pt1.size(); i++)
      a.add((IVector) pt1.get(i), i);

    PtArray hpts = new PtArray();

    if (a.prepareMarch()) {
      hpts.push(a.get(0));
      if (db)
        Streams.out.println(" lowest point is " + a.get(0));

      for (int j = 1; j <= a.size(); j++) {
        PtRec p = a.get(j == a.size() ? 0 : j);

        if (db)
          Streams.out.println(" marched to point " + j + "= " + p);

        while (hpts.size() >= 2) {
          PtRec p0 = hpts.peek(1);
          PtRec p1 = hpts.peek(0);
          double k = MyMath.sideOfLine(p0.pt(), p1.pt(), p.pt());
          if (db)
            Streams.out
                .println("side of line " + p0 + " .. " + p1 + " is " + k);
          if (k > 1e-8)
            break;
          hpts.pop();
        }

        if (false) { // testing
          if (hpts.size() >= 10) {
            PtRec p0 = hpts.peek(2);
            PtRec p1 = hpts.peek(1);
            PtRec p2 = hpts.peek(0);
            double a0 = MyMath.polarAngle(p0.pt(), p1.pt());
            double a1 = MyMath.polarAngle(p1.pt(), p2.pt());
            if (Math.abs(MyMath.normalizeAngle(a1 - a0)) > Math.PI / 10) {
              Streams.out.println("warning: bends from\n " + p0 + "\n " + p1
                  + "\n " + p2);
            }
          }
        }

        if (j < a.size())
          hpts.push(p);
      }
    }
    DArray ret = new DArray(hpts.size());
    //    int[] k = new int[hpts.size()];
    for (int i = 0; i < hpts.size(); i++)
      ret.addInt(hpts.get(i).index);

    if (db)
      Streams.out.println("conv hull of " + pt1.size() + " is " + ret);
    return ret;
  }
  private static class PtArray implements Comparator {
    private DArray pts = new DArray();
    public void add(IVector v, int ind) {
      pts.add(new PtRec(v, ind));
    }
    public void push(PtRec p) {
      pts.push(p);
    }
    public PtRec pop() {
      return (PtRec) pts.pop();
    }
    public PtRec peek(int dl) {
      return (PtRec) pts.peek(dl);
    }

    private PtRec lowest;
    public int compare(Object o1, Object o2) {
      PtRec p1 = (PtRec) o1;
      PtRec p2 = (PtRec) o2;
      double d = polarAngle(p1) - polarAngle(p2);
      if (d == 0) {
        double d1 = FPoint2.distanceSquared(p1.pt(), lowest.pt());
        double d2 = FPoint2.distanceSquared(p2.pt(), lowest.pt());
        d = d1 - d2;
      }
      return (int) Math.signum(d);
    }
//    public PtRec lowest() {
//      return lowest;
//    }

    private double polarAngle(PtRec p) {
      double r = 0;
      if (p != lowest) {
        r = Math.atan2(p.v.y() - lowest.v.y(), p.v.x() - lowest.v.x());
      }
      return r;
    }

    public boolean prepareMarch() {
      for (int i = 0; i < size(); i++) {
        PtRec p = get(i);
        if (i == 0 || p.v.y() < lowest.v.y()
            || (p.v.y() == lowest.v.y() && p.v.x() < lowest.v.x())) {
          lowest = p;
        }
      }
      sort();
      return size() > 0;
    }

    public int size() {
      return pts.size();
    }
    public PtRec get(int i) {
      return (PtRec) pts.get(i);
    }

    private void sort() {
      pts.sort(this);

      // filter duplicates
      DArray pts2 = new DArray();
      for (int i = 0; i < pts.size(); i++) {
        PtRec pi = get(i);

        if (i == 0 || !pi.pt().equals(get(i - 1).pt())) {
          pts2.add(pi);
        }
      }
      pts = pts2;
    }
  }

  private static class PtRec {
    public PtRec(IVector v, int ind) {
      this.v = v;
      this.index = ind;
    }
    public FPoint2 pt() {
      return new FPoint2(v.x(), v.y());
    }
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("#" + index);
      sb.append(" " + v);
      return sb.toString();
    }
    public IVector v;
    public int index;
  }

  /**
   * Generate a random point within a disc
   * @param origin
   * @param radius
   * @param dest destination point, or null
   * @return destination point
   */
  public static FPoint2 rndPtInDisc(FPoint2 origin, double radius, FPoint2 dest) {
    double ang = random.nextDouble() * Math.PI * 2;
    double rr = Math.sqrt(random.nextDouble()) * radius;
    return MyMath.ptOnCircle(origin, ang, rr, dest);
  }

  /**
   * Calculate point on boundary of an axis-aligned ellipse
   * @param origin origin of ellipse
   * @param angle angle of rotation 
   * @param radiusA half the width of the ellipse
   * @param radiusB half the height of the ellipse
   * @return point
   */
  public static FPoint2 ptOnEllipse(FPoint2 origin, double angle,
      double radiusA, double radiusB) {
    return new FPoint2(origin.x + Math.cos(angle) * radiusA, origin.y
        + Math.sin(angle) * radiusB);
  }

  /**
   * Determine the (integer) sign of a value
   * @param i value
   * @return integer sign -1,0,1
   */
  public static int sign(double i) {
    if (i < 0)
      return -1;
    else if (i > 0)
      return 1;
    return 0;
  }

  /**
   * Construct a random permutation of the first n integers
   * @param length
   * @param r random number generator, or null to use MyMath's
   */
  public static int[] permutation(int length, Random r) {
    if (r == null)
      r = random;
    int[] a = new int[length];
    for (int i = 0; i < length; i++) {
      a[i] = i;
    }
    for (int i = 0; i < length; i++) {
      int j = r.nextInt(length);
      int temp = a[i];
      a[i] = a[j];
      a[j] = temp;
    }
    return a;
  }

  /**
   * Determine inverse of a permutation of n integers.  The inverse
   * permutation contains the new position of each integer.  For example,
   * The inverse of [3,1,4,2,0,5] is [4,1,3,0,2,5], because 0 has moved to the
   * 4th position, 1 has mved to the 1st position, 2 has moved to the 3rd, etc.
   * @param p permutation to invert, a permutation of integers 0..n-1
   * @return inverse permutation
   */
  public static int[] invPermutation(int[] p) {
    int[] inv = new int[p.length];
    for (int i = 0; i < p.length; i++) {
      inv[p[i]] = i;
    }
    return inv;
  }

  /**
   * Calculate position of polygon vertex after adding padding
   * @param v0
   * @param v1
   * @param v2 vertices
   * @param rad amount of padding  
   * @return location of point shifted to right by a reasonable amount
   */
  public static FPoint2 offsetVertex(FPoint2 p0, FPoint2 p1, FPoint2 p2,
      double rad) {
    double th0 = MyMath.polarAngle(p0, p1);
    double th1 = MyMath.polarAngle(p1, p2);

    FPoint2 sa = MyMath.ptOnCircle(p0, th0 - Math.PI / 2, rad);
    FPoint2 sb = MyMath.ptOnCircle(p1, th0 - Math.PI / 2, rad);
    FPoint2 ta = MyMath.ptOnCircle(p1, th1 - Math.PI / 2, rad);
    FPoint2 tb = MyMath.ptOnCircle(p2, th1 - Math.PI / 2, rad);
    FPoint2 isect = MyMath.linesIntersection(sa, sb, ta, tb, null);
    if (isect == null)
      isect = sb;

    // stop point from shooting out of range
    double maxDist = Math.min(FPoint2.distance(p0, p1), FPoint2
        .distance(p1, p2))
        * .5 + rad;
    double dist = FPoint2.distance(isect, p1);
    if (dist > maxDist)
      isect = FPoint2.interpolate(p1, isect, maxDist / dist);
    return isect;

  }

  // seeded random number generator
  private static Random random = new Random(1965);

}
