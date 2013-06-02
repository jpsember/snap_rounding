package snap;

import base.*;
import testbed.*;

public class BlackBoxTriStrip extends BlackBox {

  public static final BlackBoxTriStrip S = new BlackBoxTriStrip();

  private BlackBoxTriStrip() {
  }

  public int firstPixelColumnIntersectingStrip(int stripX) {
    int div = MyMath.floor(stripX, 2);
    return div;
  }

  public int lastPixelColumnIntersectingStrip(int stripX) {
    int div = MyMath.floor(stripX, 2);
    return div + 1;
  }

  private static boolean segsCross(IPoint2 a0, IPoint2 a1, IPoint2 b0,
      IPoint2 b1) {
    boolean r = false;
    do {
      int s0 = MyMath.sideOfLine(a0, a1, b0);
      int s1 = MyMath.sideOfLine(a0, a1, b1);
      if (s0 * s1 != -1)
        break;
      s0 = MyMath.sideOfLine(b0, b1, a0);
      s1 = MyMath.sideOfLine(b0, b1, a1);
      if (s0 * s1 != -1)
        break;

      r = true;
    } while (false);
    return r;
  }

  public boolean segmentIntersectsPixel(int segx0, int segy0, int segx1,
      int segy1, int pixelX, int pixelY) {

    final boolean db = false;

    if (db)
      Streams.out.println("\n\n" + S + ".segmentIntersectsPixel "
          + new Segment(segx0, segy0, segx1, segy1).str() + " pixel="
          + new IPoint2(pixelX, pixelY));

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
      // special case for endpoint of seg at this pixel
      if ((segx0 == pixelX && segy0 == pixelY)
          || (segx1 == pixelX && segy1 == pixelY)) {
        out = true;
        if (db)
          Streams.out.println("special case for endpoint of seg at pixel");
        break;
      }

      if (!pixelWithinSegmentBounds(segx0, segy0, segx1, segy1, pixelX, pixelY)) {
        if (db)
          Streams.out.println("pixel not within seg bounds");
        break;
      }

      IPoint2 scaledPixel = toStripSpace(pixelX, pixelY);
      IPoint2 scaledSeg0 = toStripSpace(segx0, segy0);
      IPoint2 scaledSeg1 = toStripSpace(segx1, segy1);

      if (db)
        Streams.out.println("scaledPixel=" + scaledPixel + "\nscaledSeg0="
            + scaledSeg0 + "\nscaledSeg1=" + scaledSeg1);

      IPoint2 ipt1;
      Object obj;

      if (even(pixelX, pixelY)) {

        obj = BlackBox.pixelOfSegmentsIntersection(
        //
            scaledSeg0.x, scaledSeg0.y, scaledSeg1.x, scaledSeg1.y,
            //
            scaledPixel.x - 2, scaledPixel.y - 1,
            //
            scaledPixel.x, scaledPixel.y + 2);
        if (db)
          Streams.out.println("pixel is even, pixOfInt=" + obj);
        if (obj != null && obj instanceof IPoint2) {
          ipt1 = (IPoint2) obj;
          if (ipt1.x >= scaledPixel.x - 2 && ipt1.x < scaledPixel.x) {
            out = true;
            break;
          }
        }
        obj = BlackBox.pixelOfSegmentsIntersection(scaledSeg0.x, scaledSeg0.y,
            scaledSeg1.x, scaledSeg1.y, scaledPixel.x - 2, scaledPixel.y - 1,
            scaledPixel.x + 2, scaledPixel.y - 1);

        if (obj != null && obj instanceof IPoint2) {
          ipt1 = (IPoint2) obj;
          if (
          //ptWithinSegBounds(ipt1,scaledSeg0,scaledSeg1) &&
          ipt1.x >= scaledPixel.x - 2 && ipt1.x < scaledPixel.x + 2) {
            out = true;
            break;
          }
        }
      } else {

        // this is an inverted triangle.

        IPoint2 v0 = new IPoint2(scaledPixel.x, scaledPixel.y - 2);
        IPoint2 v1 = new IPoint2(scaledPixel.x - 2, scaledPixel.y + 1);
        IPoint2 v2 = new IPoint2(scaledPixel.x + 2, scaledPixel.y + 1);
        if (db)
          Streams.out.println("v0=" + v0 + " v1=" + v1 + " v2=" + v2);

        // The only points on the boundary of the triangle that
        // belong to the pixel are those interior to the bottom left
        // (v0..v1) side.  Note that no segment can be collinear with
        // these points. 
        // A Segment that doesn't have an endpoint at this pixel
        // intersects this pixel iff it intersects the interior of one of the
        // three sides.

        if (segsCross(scaledSeg0, scaledSeg1, v0, v1)
            || segsCross(scaledSeg0, scaledSeg1, v1, v2)
            || segsCross(scaledSeg0, scaledSeg1, v2, v0)) {
          out = true;
          break;
        }

      }
    } while (false);
    return out;
  }

  public int compareSnapPoints(Segment s, IPoint2 pixelA, IPoint2 pixelB) {
    int ret;

    int dx = s.x1() - s.x0(), dy = Math.abs(s.y1() - s.y0());
    pixelA = toStripSpace(pixelA);
    pixelB = toStripSpace(pixelB);

    if (!s.hasNegativeSlope()) {
      if (dx > dy) {
        ret = pixelA.x - pixelB.x;
        if (ret == 0)
          ret = pixelA.y - pixelB.y;
      } else {
        ret = pixelA.y - pixelB.y;
        if (ret == 0)
          ret = pixelA.x - pixelB.x;
      }
    } else {
      if (dx > dy) {
        ret = pixelA.x - pixelB.x;
        if (ret == 0)
          ret = pixelB.y - pixelA.y;
      } else {
        ret = pixelB.y - pixelA.y;
        if (ret == 0)
          ret = pixelA.x - pixelB.x;
      }
    }
    return ret;
  }

  public boolean isValidPixel(int x, int y) {
    return true;
  }

  public int maxStripsPerPixel() {
    return 4;
  }

  public Range getClipRangeWithinPixelColumn(Segment s, int pc) {
    final boolean db = false;

    Segment ss = toStripSpace(s);
    if (db)
      Streams.out.println("\n\ngetClipRangeWithinPixelColumn\n s =" + s.str()
          + " pc=" + pc + "\n s2=" + ss.str());

    if (db)
      Streams.out.println("rangeOf " + s.str() + " in " + pc);

    int rangeMin = 0, rangeMax = 0;

    if (isVertical(ss)) { //ss.isVertical()) {
      rangeMin = s.y0();
      rangeMax = s.y1();
      if (db && T.update())
        T.msg(" (segment is vertical)");
    } else {

      // construct vertical line at left edge of column

      int xL = pc * 2 - 2, xR = xL + 4;
      IPoint2 squareIsectLeft = (IPoint2) BlackBox.pixelOfIntersection( //
          xL, 0, xL, 1, //
          ss.x0(), ss.y0(), ss.x1(), ss.y1(), false);
      IPoint2 squareIsectRight = (IPoint2) BlackBox.pixelOfIntersection(xR, 0,
          xR, 1, //
          ss.x0(), ss.y0(), ss.x1(), ss.y1(), false);
      if (db)
        Streams.out.println(" iL=" + squareIsectLeft + " iR="
            + squareIsectRight);

      // determine y-coordinates of pixel containing this (square) pixel
      // of intersection
      int yLeft = squareIsectLeft.y, yRight = squareIsectRight.y;

      if (db)
        Streams.out.println("iL=" + squareIsectLeft + " iR=" + squareIsectRight
            + " yLeft=" + yLeft + " yRight=" + yRight);

      if (!ss.hasNegativeSlope()) {
        rangeMin = MyMath.floor(yLeft, 3);
        if (!segmentIntersectsPixel(s, pc, rangeMin)) {
          if (db)
            Streams.out.println(" seg doesn't intersect pixel y=" + rangeMin
                + ", incrementing");
          rangeMin++;
        }
        rangeMax = MyMath.floor(yRight, 3);
        if (!segmentIntersectsPixel(s, pc, rangeMax)) {
          if (db)
            Streams.out.println(" seg doesn't intersect pixel y=" + rangeMax
                + ", decrementing");

          rangeMax--;
        }

        if (rangeMax < rangeMin)
          return null;

        rangeMin = MyMath.clamp(rangeMin, s.y0(), s.y1());
        rangeMax = MyMath.clamp(rangeMax, s.y0(), s.y1());

      } else {
        rangeMin = MyMath.floor(yRight, 3);
        if (!segmentIntersectsPixel(s, pc, rangeMin))
          rangeMin++;
        rangeMax = MyMath.floor(yLeft, 3);
        if (!segmentIntersectsPixel(s, pc, rangeMax))
          rangeMax--;
        if (rangeMax < rangeMin)
          return null;

        rangeMin = MyMath.clamp(rangeMin, s.y1(), s.y0());
        rangeMax = MyMath.clamp(rangeMax, s.y1(), s.y0());
      }

    }
    Range dest = new ScalarRange(rangeMin, rangeMax);
    if (db)
      Streams.out.println(" setting range to " + dest);
    return dest;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int firstStripInPixel(int pixelColumnNumber) {
    return pixelColumnNumber * 2 - 2;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int lastStripInPixel(int pixelColumnNumber) {
    return pixelColumnNumber * 2 + 1;
  }

  public Segment toStripSpace(Segment s) {
    IPoint2 t0 = toStripSpace(s.x0(), s.y0());
    IPoint2 t1 = toStripSpace(s.x1(), s.y1());
    return new Segment(t0.x, t0.y, t1.x, t1.y);
  }

  public BlackBox construct(Segment a, Segment b) {
    return new BlackBoxTriStrip(a, b);
  }

  private static boolean even(int x, int y) {
    return ((x + y) & 1) == 0;
  }

  public IPoint2 toStripSpace(int x, int y) {
    int tx = x * 2;
    int ty = y * 3 + (even(x, y) ? 1 : 2);
    return new IPoint2(tx, ty);
  }

  public void renderSweepLine(int stripX) {
//    vp V = TestBed.view();
    Grid g = Main.grid();
    double vx = stripX / 2.0;
    V.pushColor(MyColor.get(MyColor.GREEN, .28));
    V.drawLine(g.toView(vx, 0, null), g.toView(vx, g.height() - 1, null));
    V.popColor();
  }

  public IPoint2 snapInGridSpace(FPoint2 gridPt) {
    return TriGrid.snapInGridSpace(gridPt);
  }
  private BlackBoxTriStrip(Segment a, Segment b) {
    super(a, b);
    final boolean db = false;

    Segment ta = toStripSpace(a), tb = toStripSpace(b);
    if (db)
      Streams.out.println("BlackBoxTriStrip a=" + a.str() + " b=" + b.str()
          + "\n ta=" + ta.str() + " tb=" + tb.str());

    Object obj = BlackBox.pixelOfIntersection(ta.x0(), ta.y0(), ta.x1(), ta.y1(),
        tb.x0(), tb.y0(), tb.x1(), tb.y1(), false);
    if (db)
      Streams.out.println(" pixel of intersection= " + obj);

    if (!(obj instanceof IPoint2)) {
      state = ((Boolean) obj).booleanValue() ? COLLINEAR : PARALLEL;
    } else {
      IPoint2 ipt = (IPoint2) obj;
      if (db)
        Streams.out.println(" calculated point " + ipt);

      // Determine which tile contains the intersection point.
      // Each tile has its bottom left point at the center of pixel
      // 4m, 6n for integers m, n.

      // Find tx, ty such that
      //
      //  4tx <= ix < 4tx + 4
      //  3ty <= iy < ty + 3
      //
      // where (tx + ty) is even
      //

      int remX = MyMath.mod(ipt.x, 4);
      int remY = MyMath.mod(ipt.y, 6);

      int tileX = (ipt.x - remX) / 2;
      int tileY = (ipt.y - remY) / 3;

      if (db)
        Streams.out.println("rem=" + remX + "," + remY + " t=" + tileX + ","
            + tileY);

      if (remY >= 3) {
        remY -= 3;
        tileY++;
        if (remX >= 2) {
          remX -= 2;
          tileX++;
        } else {
          remX += 2;
          tileX--;
        }
      }

      int cx = tileX * 2;
      int cy = tileY * 3;

      if (db)
        Streams.out.println(" tx=" + tileX + " ty=" + tileY + " remx=" + remX
            + " remy=" + remY + " cx=" + cx + " cy=" + cy);

      // We now have the intersection point in some square pixel
      // with remainder 0..3, 0..2

      if (remX < 2) {

        // we are in pixel tx, ty, unless 
        // we are on or to the right of the line from cx+2,cy to cx, cy+3.

        if (sideOfRationalLine(ta, tb, cx + 2, cy, cx, cy + 3) ) {
          tileX++;
          if (db)
            Streams.out.println("tileX incr'd to " + tileX);
        }
      } else {
        tileX++;
        // we are in pixel tx+1,ty unless
        // we are on or to the right of the line from cx+2,cy to cx+4, cy+3.
        if (sideOfRationalLine(ta, tb, cx + 2, cy, cx + 4, cy + 3) )  
          tileX++;
      }

      ab = ipt;

      // determine original (untransformed) pixel containing this
      // intersection point.
      abOrig = new IPoint2(tileX, tileY);
    }
    if (db)
      Streams.out.println(this);
  }

  public boolean abWithinSegments() {
    boolean ret = false;
    if (state == NOT_PARALLEL)
      ret = pixelWithinSegmentBounds(a.x0(), a.y0(), a.x1(), a.y1(), abOrig.x, abOrig.y)
          && pixelWithinSegmentBounds(b.x0(), b.y0(), b.x1(), b.y1(), abOrig.x,
              abOrig.y);
    return ret;
  }

  private static boolean withinBounds(int r0, int r1, int c) {
    if (r1 < r0) {
      int tmp = r1;
      r1 = r0;
      r0 = tmp;
    }
    return c >= r0 && c <= r1;
  }

  /**
   * Determine if a pixel is within the minimum bounding box of a segment.
   * @param x0,y0,x1,y1 segment endpoints
   * @param px,py pixel coordinates
   * @return
   */
  private boolean pixelWithinSegmentBounds(int x0, int y0, int x1, int y1,
      int px, int py) {

    // special case for vertical segments:  allow x+1, if y within range
    if (x0 == x1 && px == x0 + 1) {
      return withinBounds(y0, y1, py);
    }

    return withinBounds(x0, x1, px) && withinBounds(y0, y1, py);

  }

  public Grid getGrid( ) {
    return new TriGrid( );
  }

}
