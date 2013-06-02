package snap;

import base.*;
import testbed.*;

public abstract class BlackBox {

  public static final int NOT_PARALLEL = 0, PARALLEL = 1, COLLINEAR = 2;

  /**
   * Set black box for transforming between view space and sweep space
   * (for display purposes only)
   */
  public static void setTransform(BlackBox b) {
    blackBox = b;
  }

  public Segment toSweep(Segment s) {
    Segment s2 = new Segment(toSweepSpace(s.pt(0)), toSweepSpace(s.pt(1)));
    s2.setId(s.id());
    return s2;
  }

  /**
   * Transform point from sweep space for display purposes
   * @param pt : point in sweep space
   * @return point in display space, if a black box was defined
   *   for this conversion
   */
  public static IPoint2 fromSweep(IPoint2 pt) {
    if (blackBox != null)
      pt = blackBox.fromSweepSpace(pt);
    return pt;
  }

  /**
   * Get black box for transforming between view space and sweep space
   * (for display purposes only)
   * @return BlackBox, or null if none set
   */
  public static BlackBox getTransform() {
    return blackBox;
  }

   public static BlackBox constructFor(Segment a, Segment b) {
    if (blackBox == null)
      throw new IllegalArgumentException("no black box defined");
    return blackBox.construct(a, b);
  }

  private static BlackBox blackBox;

  /**
   * Protected constructor; use factory method construct()
   */
  protected BlackBox() {
  }

  public boolean isVertical(Segment s) {
    return s.x0() == s.x1();
  }

  /**
   * Construct a Grid appropriate to this type of BlackBox
   * @return Grid
   */
  public abstract Grid getGrid( );

  /**
   * Snap a point in grid space to the coordinates of its containing pixel
   * @param gridPt : point, in grid space
   * @return coordinates of containing pixel
   */
  public IPoint2 snapInGridSpace(FPoint2 gridPt) {
    throw new UnsupportedOperationException();
  }

  /**
   * Compare two snap points of a segment
   * @param seg : segment
   * @param pixelA : first snap point
   * @param pixelB : second snap point
   * @return -1, 0, 1 if pixelA precedes, equals, or suceeds pixelB
   */
  public abstract int compareSnapPoints(Segment seg, IPoint2 pixelA,
      IPoint2 pixelB);

  public final boolean isValidPixel(IPoint2 pt) {
    return isValidPixel(pt.x, pt.y);
  }

  /**
   * Determine if a point represents a valid pixel on this type of grid
   * @param x
   * @param y
   * @return true if a pixel exists with coordinates x,y
   */
  public abstract boolean isValidPixel(int x, int y);

  /**
   * Determine if a segment intersects a pixel
   * @param s : Segment
   * @param px, py : pixel
   * @return true if intersects
   */
  public boolean segmentIntersectsPixel(Segment s, int px, int py) {
    return segmentIntersectsPixel(s.x0(), s.y0(), s.x1(), s.y1(), px, py);
  }

  /**
   * Determine if a segment intersects a pixel
   * @param s : Segment
   * @param pixel : pixel
   * @return true if intersects
   */
  public boolean segmentIntersectsPixel(Segment s, IPoint2 pixel) {
    return segmentIntersectsPixel(s, pixel.x, pixel.y);
  }

  /**
   * Calculate the maximum number of strips intersecting a pixel column
   * @return
   */
  public abstract int maxStripsPerPixel();

  /**
   * For debug purposes, render the sweep line
   * @param stripX : strip representing sweep line's position
   */
  public abstract void renderSweepLine(int stripX);

  public abstract boolean segmentIntersectsPixel(int x0, int y0, int x1,
      int y1, int px, int py);

  /**
   * Construct a BlackBox for two potentially intersecting segments.
   * This acts as a factory for producing a concrete subclass of a BlackBox.
   * @param a
   * @param b
   * @return BlackBox
   */
  public abstract BlackBox construct(Segment a, Segment b);

  /**
   * Determine leftmost pixel column intersecting a strip
   * @param stripX
   * @return first pixel column intersecting the strip
   */
  public abstract int firstPixelColumnIntersectingStrip(int stripX);

  /**
   * Determine rightmost pixel column intersecting a strip
   * @param stripX
   * @return last pixel column intersecting the strip
   */
  public abstract int lastPixelColumnIntersectingStrip(int stripX);

  /**
   * Determine the first strip intersecting a pixel
   * @param pixelColumnNumber
   * @return first strip intersecting pixel
   */
  public abstract int firstStripInPixel(int pixelColumnNumber);

  /**
   * Determine last strip intersecting a pixel
   * @param pixelColumnNumber
   * @return last strip intersecting pixel
   */
  public abstract int lastStripInPixel(int pixelColumnNumber);

  /**
   * Construct a black box
   * @param a, b : potentially intersecting segments
   */
  protected BlackBox(Segment a, Segment b) {
    this.a = a;
    this.b = b;
  }

  public abstract Range getClipRangeWithinPixelColumn(Segment s, int pc);

  /**
   * Transform a point to strip space
   * @param x,y point 
   * @return IPoint2 within strip space
   */
  public IPoint2 toStripSpace(IPoint2 pt) {
    return toStripSpace(pt.x, pt.y);
  }

  //  /**
  //   * Determine if origin of pixel is in the center
  //   * @return
  //   */
  //  public   boolean originCentered(){return true;}

  /**
   * Transform a point to strip space
   * @param x,y point 
   * @return IPoint2 within strip space
   */
  public abstract IPoint2 toStripSpace(int x, int y);

  /**
   * Divide two integers, round result.
   *
   * This produces the same value as
   *   (int)Math.round((double)numer / (double)denom),
   *
   * @param numer long
   * @param denom long
   * @return int
   */
  private static int iDivAndRound(long numer, long denom) {

    boolean neg = (numer < 0) ^ (denom < 0);

    numer = Math.abs(numer);
    denom = Math.abs(denom);

    int res;
    if (!neg) {
      res = (int) ((numer + (denom >> 1)) / denom);
    } else {
      res = -(int) (((numer + ((denom - 1) >> 1)) / denom));
    }
    return res;
  }

  /**
   * Divide two integers, take floor of result.
   *
   * This produces the same value as
   *  (int)Math.floor((double)numer / (double)denom);
   *
   * @param numer long
   * @param denom long
   * @return int
   */
  private static int iDivAndTrunc(long numer, long denom) {
    boolean neg = (numer < 0) ^ (denom < 0);

    numer = Math.abs(numer);
    denom = Math.abs(denom);

    int res;
    if (!neg) {
      res = (int) (numer / denom);
    } else {
      res = -(int) ((numer + denom - 1) / denom);
    }
    return res;
  }

  /**
   * Determine the lower of the two segments
   * 
   * @return Segment
   */
  public Segment lower() {

    Segment lower = null;

    if (state != COLLINEAR) {
      final boolean db = false;

      if (db) {
        Streams.out.println("lowerOf a=" + a.str());
      }
      if (db) {
        Streams.out.println(" and b=" + b.str());
      }

      switch (state) {
      case NOT_PARALLEL:
        {
          if (isVertical(b)) {
            lower = b;
            if (db) {
              Streams.out.println(" b is vertical, returning it");
            }
          } else {
            IPoint2 ta0 = toStripSpace(a.pt(0)), ta1 = toStripSpace(a.pt(1)), tb0 = toStripSpace(b
                .pt(0)), tb1 = toStripSpace(b.pt(1));

            // Choose an endpoint of b that is not at the same x-coord as a^b
            IPoint2 ept = (tb0.x != ab.x) ? tb0 : tb1;

            int k = MyMath.sideOfLine(ta0, ta1, ept);
            //ta.x0, ta.y0, ta.x1, ta.y1, x, y);
            lower = (k > 0) ^ (ept.x > ab.x) ? a : b;
          }
        }
        break;
      case PARALLEL:
        {
          IPoint2 ta0 = toStripSpace(a.pt(0)), ta1 = toStripSpace(a.pt(1)), tb0 = toStripSpace(b
              .pt(0));

          int k = MyMath.sideOfLine(ta0, ta1, tb0);
          if (db) {
            Streams.out.println(" sideOfSegment for b0 is " + k);
          }
          lower = (k > 0) ? a : b;
        }
        break;
      }
      if (db) {
        Streams.out.println(" returning segment " + lower.str());
      }
    }
    return lower;
  }

  /**
   * Get string describing object
   * 
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.getClass().getName());
    sb.append(" o=" + orientation);
    if (a != null) {
      sb.append("\n a=" + a.str());
      sb.append("\n b=" + b.str());
      sb.append("\n state=" + state);
      sb.append("\n isect=" + getIntersectionPixel(false) + " "
          + getIntersectionPixel(true));
      sb.append("\n abWithin=" + Tools.f(abWithinSegments()));
      sb.append("\n lower=" + lower());
    }
    return sb.toString();
  }

  /**
   * Get first segment
   * @return
   */
  public Segment a() {
    return a;
  }

  /**
   * Get second segment
   * @return
   */
  public Segment b() {
    return b;
  }

  /**
   * Determine status of intersection 
   * @return NOT_PARALLEL, PARALLEL, or COLLINEAR
   */
  public int state() {
    return state;
  }

  /**
   * Determine if intersection pixel is within each segment
   * (as opposed to off one end, elsewhere on the line containing the segment)
   * @return
   */
  public abstract boolean abWithinSegments();

  /**
   * Get pixel containing intersection
   * @param transformedSegs : if true, gets pixel corresponding to transformed
   *  segments
   * @return pixel of intersection
   */
  public IPoint2 getIntersectionPixel(boolean transformedSegs) {
    return transformedSegs ? ab : abOrig;
  }

  /**
   * Calculate which side of a line an intersection between two segments is on.
   * @param a0,a1 : endpoints of first segment
   * @param b0,b1 : endpoints of second segment
   * @param line0,line1 : two points on line
   * @return true if intersection point is on or to the right of the line
   */
  public static boolean sideOfRationalLine(IPoint2 a0, IPoint2 a1, IPoint2 b0,
      IPoint2 b1, IPoint2 line0, IPoint2 line1) {

    // Let m, n be line0
    // and s, t be vector difference line1 - line0

    // Then we apply a translation and skew to align the ray with the
    // positive y-axis:

    // x' = (x - m) * t - (y - n) * s

    int m = line0.x, n = line0.y;
    int s = line1.x - line0.x, t = line1.y - line0.y;

    int ax0 = (a0.x - m) * t - (a0.y - n) * s;
    int ax1 = (a1.x - m) * t - (a1.y - n) * s;
    int bx0 = (b0.x - m) * t - (b0.y - n) * s;
    int bx1 = (b1.x - m) * t - (b1.y - n) * s;

    IPoint2 ipt = (IPoint2) pixelOfIntersection(ax0, a0.y, ax1, a1.y, bx0,
        b0.y, bx1, b1.y, false);
    return  (ipt.x >= 0);
  }

  /**
   * Calculate which side of a directed line an intersection between two segments is on.
   * @param sa, sb : line segments
   * @param ox, oy : startpoint of line
   * @param dx, dy : endpoint of line
   * @return true if intersection point is on or to the right of the line
   */
  public static boolean sideOfRationalLine(Segment sa, Segment sb, int ox, int oy,
      int dx, int dy) {

    final boolean db = false;
    if (db)
      Streams.out.println("sideOfRationalLine\n sa=" + sa + "\n sb=" + sb
          + "\n origin=" + new IPoint2(ox, oy) + "\n   dest="
          + new IPoint2(dx, dy));

    int a = dy - oy, b = dx - ox;
    int ax0 = a * (sa.x0() - ox) - b * (sa.y0() - oy);
    int ax1 = a * (sa.x1() - ox) - b * (sa.y1() - oy);
    int bx0 = a * (sb.x0() - ox) - b * (sb.y0() - oy);
    int bx1 = a * (sb.x1() - ox) - b * (sb.y1() - oy);

    if (db) 
      Streams.out.println(" skewed lines:\n "+IPoint2.toString(ax0,sa.y0(),ax1,sa.y1())+"\n "+
          IPoint2.toString(bx0,sb.y0(),bx1,sb.y1()));
          
    IPoint2 ipt = (IPoint2) pixelOfIntersection(ax0, sa.y0(), ax1, sa.y1(),
        bx0, sb.y0(), bx1, sb.y1(), false);

    if (db)
      Streams.out.println(" pixelOfIntersection=" + ipt + "\n sign="
          + MyMath.sign(ipt.x));

    return ipt.x >= 0;
  }
  
  /**
   * Calculate the pixel of intersection of two line segments.
   * If both of a line's sample points are the same, the results are undefined.
   * 
   * @param a0,a1 : points on first line
   * @param b0,b1 : points on second line
   * @return Boolean, if lines were parallel; else,
   *   IPoint2 containing pixel of intersection, if within both segments;
   *   else null
   */
  public static Object pixelOfSegmentsIntersection(IPoint2 a0, IPoint2 a1,
      IPoint2 b0, IPoint2 b1) {
    return pixelOfSegmentsIntersection(a0.x, a0.y, a1.x, a1.y, b0.x, b0.y,
        b1.x, b1.y);
  }

  /**
   * Calculate the pixel of intersection of two line segments.
   * If both of a line's sample points are the same, the results are undefined.
   * 
   * @param x0,y0,x1,y1 : points on first line
   * @param y2,y2,x3,y3 : points on second line
   * @return Boolean, if lines were parallel; else,
   *   IPoint2 containing pixel of intersection, if within both segments;
   *   else null
   */
  public static Object pixelOfSegmentsIntersection(int x0, int y0, int x1,
      int y1, int x2, int y2, int x3, int y3) {
    Object obj = pixelOfIntersection(x0, y0, x1, y1, x2, y2, x3, y3, false);
    do {
      if (!(obj instanceof IPoint2))
        break;
      // test if endpoints of one seg are to same side of other seg
      int r0 = MyMath.sideOfLine(x0, y0, x1, y1, x2, y2);
      int r1 = MyMath.sideOfLine(x0, y0, x1, y1, x3, y3);
      if (r0 != 0 && r1 != 0 && r0 == r1) {
        obj = null;
        break;
      }
      r0 = MyMath.sideOfLine(x2, y2, x3, y3, x0, y0);
      r1 = MyMath.sideOfLine(x2, y2, x3, y3, x1, y1);
      if (r0 != 0 && r1 != 0 && r0 == r1) {
        obj = null;
        break;
      }
    } while (false);
    return obj;
  }

  /**
   * Calculate the pixel of intersection of two lines.
   * If both of a line's sample points are the same, the results are undefined.
   * 
   * @param x0,y0,x1,y1 : points on first line
   * @param y2,y2,x3,y3 : points on second line
   * @param originInCenter : if true, 
   *    pixels have their origins at their centers:  that is, 
   *    a pixel with coordinates s, t contains points
   *      [s-.5 .. s+.5),[t-.5 .. t+.5).
   *    if false, origins are at the bottom left, and points are
   *      [s .. s + 1), [t .. t+1).
   * @return Object, an IPoint2 containing pixel of intersection
   *   if lines weren't parallel; otherwise, Boolean, true if lines
   *   were collinear
   */
  public static Object pixelOfIntersection(int x0, int y0, int x1, int y1,
      int x2, int y2, int x3, int y3, boolean originInCenter) {

    long xa, ya, xb, yb;

    xa = x1 - x0;
    ya = y1 - y0;
    xb = x3 - x2;
    yb = y3 - y2;

    long determinant = ya * xb - xa * yb;
    if (determinant == 0) {
      // determine if they are collinear, by seeing if the
      // cross product between segment a and the segment from a.0 to b.0 is
      // zero.
      return Boolean.valueOf((x2 - x0) * ya - xa * (y2 - y0) == 0);
    }

    long f1 = xb * y2 - yb * x2;
    long f2 = xa * y0 - ya * x0;
    long denom = xb * ya - xa * yb;

    long numerX = xa * f1 - xb * f2;
    long numerY = ya * f1 - yb * f2;

    IPoint2 ret;
    if (!originInCenter) {
      ret = new IPoint2(iDivAndTrunc(numerX, denom),
          iDivAndTrunc(numerY, denom));
    } else
      ret = new IPoint2(iDivAndRound(numerX, denom),
          iDivAndRound(numerY, denom));

    return ret;
  }

  /**
   * Determine if a segment intersects a pixel
   * @param segx0,segy0,segx1,segy1 : segment endpoints
   * @param px2,py2 : pixel coordinates
   * @param originInCenter : if true, assumes pixel origin is in its center,
   *  vs being in the bottom left
   * @return true if segment intersects pixel
   */
  public static boolean segmentIntersectsPixel(int segx0, int segy0, int segx1,
      int segy1, int px2, int py2, boolean originInCenter) {
    final boolean db = false;

    boolean out = false;
    do {

      if (segx0 > segx1) {
        int tmp = segx0;
        segx0 = segx1;
        segx1 = tmp;
        tmp = segy0;
        segy0 = segy1;
        segy1 = tmp;
      }
      // special case for point segments
      if (segx0 == px2 && segy0 == py2) {
        out = true;
        break;
      }
      if (segx0 > px2 //
          || segx1 < px2 //
          || Math.min(segy0, segy1) > py2 || Math.max(segy0, segy1) < py2)
        break;

      int scaledPixelX = px2 << 1, scaledPixelY = py2 << 1;
      int scaledSegX0 = segx0 << 1, scaledSegY0 = segy0 << 1, //
      scaledSegX1 = segx1 << 1, scaledSegY1 = segy1 << 1;

      if (db) {
        Streams.out.println("segmentIntersectsPixel, "
            + segx0
            + ","
            + segy0
            + " ... "
            + segx1
            + ","
            + segy1
            + "\n pixel="
            + px2
            + ","
            + py2
            + "\n stretched seg is "
            + new Segment(scaledSegX0, scaledSegY0, scaledSegX1, scaledSegY1)
                .toString(true));
      }

      for (int diagPass = 0; diagPass < 2; diagPass++) {
        int diagX0, diagY0, diagX1, diagY1;
        if (diagPass == 0) {
          if (originInCenter) {
            diagX0 = scaledPixelX - 1;
            diagY0 = scaledPixelY - 1;
            diagX1 = scaledPixelX + 1;
            diagY1 = scaledPixelY + 1;
          } else {
            diagX0 = scaledPixelX;
            diagY0 = scaledPixelY;
            diagX1 = scaledPixelX + 2;
            diagY1 = scaledPixelY + 2;
          }
        } else {
          if (originInCenter) {
            diagX0 = scaledPixelX - 1;
            diagY0 = scaledPixelY + 1;
            diagX1 = scaledPixelX + 1;
            diagY1 = scaledPixelY - 1;
          } else {
            diagX0 = scaledPixelX;
            diagY0 = scaledPixelY + 2;
            diagX1 = scaledPixelX + 2;
            diagY1 = scaledPixelY;
          }
        }

        int s1 = MyMath.sideOfLine(scaledSegX0, scaledSegY0, //
            scaledSegX1, scaledSegY1, //
            diagX1, diagY1);
        if (db) {
          Streams.out.println(" testing stretched seg with point "
              + new IPoint2(diagX1, diagY1) + " side=" + s1);
        }

        int s0 = MyMath.sideOfLine(scaledSegX0, scaledSegY0, scaledSegX1,
            scaledSegY1, diagX0, diagY0);
        if (db) {
          Streams.out.println(" testing stretched seg with point "
              + new IPoint2(diagX0, diagY0) + " side=" + s0);
        }

        // only corner a can be on the line
        if ((s0 == 0 && diagPass != 0) || s1 == 0)
          continue;

        if (s0 != s1) {
          out = true;
          break;
        }
      }
    } while (false);
    if (db)
      Streams.out.println(" returning " + out);

    return out;
  }

  public void setOrientation(int f) {
    this.orientation = f;
  }
  public int orientation() {
    return orientation;
  }
  protected int orientation;

  /**
   * Transform a point so we are always sweeping left to right
   */
  public IPoint2 toSweepSpace(IPoint2 pt) {
    return pt;
  }

  /**
   * Transform point from left->right sweep to original orientation
   * @param pt
   * @return
   */
  public IPoint2 fromSweepSpace(IPoint2 pt) {
    return fromSweepSpace(pt.x, pt.y);
  }

  public IPoint2 fromSweepSpace(int x, int y) {
    return new IPoint2(x, y);
  }

  protected Segment a;

  protected Segment b;

  protected int state;

  protected IPoint2 ab;

  protected IPoint2 abOrig;
}
