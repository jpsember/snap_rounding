package snap;

import base.*;
import testbed.*;

public class BlackBoxHexBundle extends BlackBox {
  private static final boolean db = false;

  public static final BlackBoxHexBundle S = new BlackBoxHexBundle();

  private BlackBoxHexBundle() {
  }

  public int firstPixelColumnIntersectingStrip(int stripX) {
    return MyMath.floor(stripX, 4);
  }

  public int lastPixelColumnIntersectingStrip(int stripX) {
    return firstPixelColumnIntersectingStrip(stripX);
  }

  public boolean segmentIntersectsPixel(int segx0, int segy0, int segx1,
      int segy1, int px2, int py2) {

    boolean out = false;
    do {
      // normalize so x0 <= x1
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

      if (!pixelWithinSegmentBounds(segx0, segy0, segx1, segy1, px2, py2))
        break;

      int scaledPixelX = px2 * 3, scaledPixelY = py2;
      int scaledSegX0 = segx0 * 3, scaledSegY0 = segy0;
      int scaledSegX1 = segx1 * 3, scaledSegY1 = segy1;

      for (int diagPass = 0; diagPass < 3; diagPass++) {
        int diagX0, diagY0, diagX1, diagY1;
        if (diagPass == 0) {
          diagX0 = scaledPixelX - 2;
          diagY0 = scaledPixelY;
          diagX1 = scaledPixelX + 2;
          diagY1 = scaledPixelY;
        } else if (diagPass == 1) {
          diagX0 = scaledPixelX - 1;
          diagY0 = scaledPixelY - 1;
          diagX1 = scaledPixelX + 1;
          diagY1 = scaledPixelY + 1;
        } else {
          diagX0 = scaledPixelX - 1;
          diagY0 = scaledPixelY + 1;
          diagX1 = scaledPixelX + 1;
          diagY1 = scaledPixelY - 1;
        }

        int s1 = MyMath.sideOfLine(scaledSegX0, scaledSegY0, //
            scaledSegX1, scaledSegY1, //
            diagX1, diagY1);
        int s0 = MyMath.sideOfLine(scaledSegX0, scaledSegY0, scaledSegX1,
            scaledSegY1, diagX0, diagY0);
        // only some corners can be on the line

        if (orientation == 0) {
          if ((s0 == 0 && diagPass == 2) || s1 == 0)
            continue;
        } else {
          if ((s0 == 0 && diagPass != 1))
            continue;
          if ((s1 == 0 && diagPass != 2))
            continue;
        }

        if (s0 != s1) {
          out = true;
          break;
        }
      }
    } while (false);
    return out;
  }

  public int compareSnapPoints(Segment s, IPoint2 pixelA, IPoint2 pixelB) {
    throw new UnsupportedOperationException();
  }

  public boolean isValidPixel(int x, int y) {
    return ((x + y) & 1) == 0;
  }

  public int maxStripsPerPixel() {
    return 4;
  }
  private static boolean even(int x, int y) {
    return ((x + y) & 1) == 0;
  }

  public Range getClipRangeWithinPixelColumn(Segment s, int pc) {

    final boolean db = false;

    Segment scaledSeg = toStripSpace(s);
    if (db)
      Streams.out.println("getClipRangeWithinPixelColumn\n s =" + s.str()
          + " pc=" + pc + "\n s2=" + scaledSeg.str());

    if (db && T.update())
      T.msg("rangeOf " + s.toString(true) + " in " + pc);

    int rangeMin, rangeMax;

    if (isVertical(scaledSeg)) {
      rangeMin = scaledSeg.y0();
      rangeMax = scaledSeg.y1();
      if (db && T.update())
        T.msg(" (segment is vertical)");
    } else {

      // construct vertical line at left edge of column

      int xL = pc * 3 - 2, xR = xL + 4;
      IPoint2 squareIsectLeft = (IPoint2) BlackBox
          .pixelOfIntersection(xL, 0, xL,
              1, //
              scaledSeg.x0(), scaledSeg.y0(), scaledSeg.x1(), scaledSeg.y1(),
              false);
      IPoint2 squareIsectRight = (IPoint2) BlackBox
          .pixelOfIntersection(xR, 0, xR,
              1, //
              scaledSeg.x0(), scaledSeg.y0(), scaledSeg.x1(), scaledSeg.y1(),
              false);
      if (db)
        Streams.out.println(" iL=" + squareIsectLeft + " iR="
            + squareIsectRight);

      boolean parity = even(pc, squareIsectLeft.y);
      // determine y-coordinates of hex pixel containing this (square) pixel
      // of intersection
      int yLeft = squareIsectLeft.y, yRight = squareIsectRight.y;
      if (!parity)
        yLeft++;
      if (!even(yRight, pc))
        yRight++;

      if (db)
        Streams.out.println("iL=" + squareIsectLeft + " iR=" + squareIsectRight
            + " yLeft=" + yLeft + " yRight=" + yRight);

      if (db)
        Streams.out.println("pc=" + pc + " parity=" + parity);

      int add = s.hasNegativeSlope() ? -2 : 2;

      if (!segmentIntersectsPixel(s, pc, yLeft))
        yLeft += add;
      else if (segmentIntersectsPixel(s, pc, yLeft - add))
        yLeft -= add;
      if (!segmentIntersectsPixel(s, pc, yRight))
        yRight -= add;
      else if (segmentIntersectsPixel(s, pc, yRight + add))
        yRight += add;

      // clamp the range to fit within the vertical extent of the
      // original segment
      int sMax = Math.max(scaledSeg.y0(), scaledSeg.y1()), sMin = Math.min(
          scaledSeg.y0(), scaledSeg.y1());
      // expand bounds by 1 on each side to handle shifts into neighboring
      // pixels
      if (db)
        Streams.out.println(" clamp to range, sMin=" + sMin + ", sMax=" + sMax);

      int minY = Math.min(yLeft, yRight);
      int maxY = Math.max(yLeft, yRight);
      if (minY < sMin - 1)
        minY = sMin;
      if (maxY < sMin - 1)
        maxY = sMin;
      if (maxY > sMax + 1)
        maxY = sMax;
      if (minY > sMax + 1)
        minY = sMax;
      rangeMin = minY;
      rangeMax = maxY;
    }

    Range dest = new ScalarRange(rangeMin, rangeMax);

    if (db && T.update())
      T.msg(" setting range to " + dest);
    return dest;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int firstStripInPixel(int pixelColumnNumber) {
    return pixelColumnNumber * 4;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int lastStripInPixel(int pixelColumnNumber) {
    return firstStripInPixel(pixelColumnNumber) + 3;
  }

  public BlackBox construct(Segment a, Segment b) {
    return new BlackBoxHexBundle(a, b, orientation);
  }

  public IPoint2 toStripSpace(int x, int y) {
    return new IPoint2(x * 4 + 2, y);
  }

  private static Segment toStripSpace(Segment s) {
    return new Segment(s.x0() * 3, s.y0(), s.x1() * 3, s.y1());
  }

  private BlackBoxHexBundle(Segment a, Segment b, int orientation) {
    super(a, b);
    this.orientation = orientation;
    final boolean db = false;
    boolean spec = false;
    Segment ta = new Segment(a.x0() * 3, a.y0(), a.x1() * 3, a.y1());
    Segment tb = new Segment(b.x0() * 3, b.y0(), b.x1() * 3, b.y1());
    if (db)
      Streams.out.println("BlackBoxHexBundle a=" + a.str() + " b=" + b.str()
          + "\n ta=" + ta.str() + " tb=" + tb.str() + "\n orientation="
          + orientation);

    Object obj = BlackBox.pixelOfIntersection(ta.x0(), ta.y0(), ta.x1(), ta
        .y1(), tb.x0(), tb.y0(), tb.x1(), tb.y1(), false);

    if (!(obj instanceof IPoint2)) {
      state = ((Boolean) obj).booleanValue() ? COLLINEAR : PARALLEL;
    } else {
      IPoint2 ipt = (IPoint2) obj;
      if (db)
        Streams.out.println(" square pixel of intersection: "
            + ipt);

      // Determine which tile contains the intersection point.
      // Each tile has its bottom left point at the center of pixel
      // 6m, 2n for integers m, n.

      // Find tx, ty such that
      //
      //  3tx <= ix < 3tx + 3
      //  ty  <= iy < ty + 2
      //
      // where (tx + ty) is even
      //

      int remX = MyMath.mod(ipt.x, 6);
      int remY = MyMath.mod(ipt.y, 2);

      int tileX = (ipt.x - remX) / 3;
      int tileY = (ipt.y - remY);

      if (db)
        Streams.out.println(" tile: " + tileX + "," + tileY + " rem:" + remX
            + "," + remY);

      if (remX >= 3) {
        remX -= 3;
        tileX++;
        if (remY == 1) {
          remY = 0;
          tileY++;
        } else {
          remY = 1;
          tileY--;
        }
        if (db)
          Streams.out.println(" shifted to new tile:\n" + " tile: " + tileX
              + "," + tileY + " rem:" + remX + "," + remY);
      }

      // We now have the intersection point in some square pixel
      // with remainder 0..2, 0..1

      int qx;

      if (remY == 0) {
        switch (remX) {
        default: //case 0:
          qx = tileX * 4 + 2;
          break;
        case 1:
          // Determine which diagonal half of the intersection pixel contains
          // the intersection point.
          if (sideOfRationalLine(ta, tb, ipt.x + 1, ipt.y, ipt.x, ipt.y + 1) ) {
            qx = (tileX + 1) * 4;
            tileY++;
            tileX++;
          } else {
            qx = tileX * 4 + 3;
          }
          break;
        case 2:
          qx = (tileX + 1) * 4 + 1;
          tileY++;
          tileX++;
          break;
        }
      } else {
        switch (remX) {
        default: //case 0:
          qx = tileX * 4 + 2;
          tileY += 2;
          break;
        case 1:
          {
            boolean side;
            if (orientation == 0)
              side = sideOfRationalLine(ta, tb, ipt.x, ipt.y, ipt.x + 1,
                ipt.y + 1);
            else
              side = !sideOfRationalLine(ta, tb, ipt.x+1, ipt.y+1, ipt.x ,
                  ipt.y );
            if (db)
              Streams.out.println(" side of line=" + side);

            if (side ) {
              tileX++;
              qx = tileX * 4;
              tileY++;
            } else {
              qx = tileX * 4 + 3;
              tileY += 2;
            }
          }
          break;
        case 2:
          tileX++;
          qx = tileX * 4 + 1;
          tileY++;
          break;
        }
      }

      ab = new IPoint2(qx, tileY);

      // determine original (untransformed) pixel containing this
      // intersection point.
      abOrig = new IPoint2(tileX, tileY);
      if (db)
        Streams.out.println(" abOrig=" + abOrig);

    }
    if (db)
      Streams.out.println(this);
    if (spec)
      Streams.out.println("abOrig=" + abOrig + " ab=" + ab);

  }

  public boolean abWithinSegments() {
    boolean ret = false;
    if (state == NOT_PARALLEL) {
      ret = pixelWithinSegmentBounds(a.x0(), a.y0(), a.x1(), a.y1(), abOrig.x,
          abOrig.y)
          && pixelWithinSegmentBounds(b.x0(), b.y0(), b.x1(), b.y1(), abOrig.x,
              abOrig.y);
    }
    return ret;

  }

  private boolean pixelWithinSegmentBounds(int x0, int y0, int x1, int y1,
      int px, int py) {
    final boolean db = false;

    int fx = BlackBoxHexStrip.withinBounds(x0, x1, px);
    int fy = BlackBoxHexStrip.withinBounds(y0, y1, py);
    if (db && T.update())
      T.msg("pixelWithinSegmentBounds seg=" + x0 + " " + y0 + " " + x1 + " "
          + y1 + " pix=" + px + " " + py + "\n fx=" + fx + " fy=" + fy);

    return (fx + fy <= 1);
  }

  public Grid getGrid( ) {
    throw new UnsupportedOperationException();
  }

  public IPoint2 snapInGridSpace(FPoint2 gridPt) {
    throw new UnsupportedOperationException();
  }

  public IPoint2 toSweepSpace(IPoint2 pt) {
    int x0, y0;
    IPoint2 ret;

    switch (orientation) {
    default:
      x0 = pt.x;
      y0 = pt.y;
      break;
    case 1:
      x0 = (pt.x - pt.y) / 2;
      y0 = (3 * pt.x + pt.y) / 2;
      break;
    }
    ret = new IPoint2(x0, y0);
    if (db)
      Streams.out.println("toNormalized " + pt + ": " + ret);
    return ret;
  }

  public IPoint2 fromSweepSpace(int ptX, int ptY) {
    final boolean db = false;

    int x0, y0;
    IPoint2 ret;

    switch (orientation) {
    default:
      x0 = ptX;
      y0 = ptY;
      break;
    case 1:
      x0 = (ptX + ptY) / 2;
      y0 = (ptX * -3 + ptY) / 2;
      break;
    }
    ret = new IPoint2(x0, y0);
    if (db)
      Streams.out.println("toSweep " + ptX + "," + ptY + ": " + ret);
    return ret;
  }

  public void highlightStripPixel() {

    IPoint2 p1 = getIntersectionPixel(false);
    IPoint2 p2 = getIntersectionPixel(true);

    Grid g = Main.grid();
//    // vp vp = TestBed.view();

    FPoint2 v0 = g.toView(p1.x - 1, p1.y - 1);
    FPoint2 v1 = g.toView(p1.x, p1.y);

    int modx = MyMath.mod(p2.x, 4);

    double y0 = v0.y;
    double y1 = v1.y + (v1.y - v0.y);
    double s = (v1.x - v0.x) / 3;
    double x0 = v1.x;
    double ym = (y0 + y1) * .5;

    switch (modx) {
    case 0:
      V.drawLine(x0 - s * 2, ym, x0 - s, y1);
      V.drawLine(x0 - s, y1, x0 - s, y0);
      V.drawLine(x0 - s, y0, x0 - s * 2, ym);
      break;
    case 1:
      V.drawRect(x0 - s, y0, s, y1 - y0);
      break;
    case 2:
      V.drawRect(x0, y0, s, y1 - y0);
      break;
    case 3:
      V.drawLine(x0 + s * 2, ym, x0 + s, y1);
      V.drawLine(x0 + s, y1, x0 + s, y0);
      V.drawLine(x0 + s, y0, x0 + s * 2, ym);
      break;
    }
  }
  private static int[] ef = { //
  16 + 32, 64, 128, 2 + 4,//
      8 + 16, 256, 512, 1 + 2, };

  public void renderSweepLine(int stripX) {

    final boolean db = false;

    if (db)
      Streams.out.println("renderSwpLine " + stripX + " or=" + orientation());

    int mod = stripX & 3;
    int vx = (stripX - mod) / 4;

//    vp V = TestBed.view();
    Grid g = Main.grid();
//    IPoint2 sz = new IPoint2(g.width(), g.height());

    int y0, y1;

    int odd = vx & 1;
    int sy = g.height() & ~1;

    y0 = -sy * 2 + odd;
    y1 = sy * 2 + odd;
    IPoint2 p0 = fromSweepSpace(vx, y0);
    IPoint2 p1 = fromSweepSpace(vx, y1);

    if (db)
      Streams.out.println("plotting sweep line from " + p0 + " to " + p1);

    int eflags = ef[orientation * 4 + mod];

    V.pushColor(MyColor.get(MyColor.GREEN, .28));

    int cx = p0.x, cy = p0.y;

    while (true) {

      if (cx >= 0 && cy >= 0 && cx < g.width() && cy < g.height()) {
        final double T = 1.0 / 3;

        FPoint2 v0 = g.toView(cx - T, cy - 1);
        FPoint2 v1 = g.toView(cx + T, cy - 1);
        FPoint2 v2 = g.toView(cx + 2 * T, cy);
        FPoint2 v3 = g.toView(cx + T, cy + 1);
        FPoint2 v4 = g.toView(cx - T, cy + 1);
        FPoint2 v5 = g.toView(cx - 2 * T, cy);

        if ((eflags & (1 << 0)) != 0)
          V.drawLine(v0, v1);
        if ((eflags & (1 << 1)) != 0)
          V.drawLine(v1, v2);
        if ((eflags & (1 << 2)) != 0)
          V.drawLine(v2, v3);
        if ((eflags & (1 << 3)) != 0)
          V.drawLine(v3, v4);
        if ((eflags & (1 << 4)) != 0)
          V.drawLine(v4, v5);
        if ((eflags & (1 << 5)) != 0)
          V.drawLine(v5, v0);
        if ((eflags & (1 << 6)) != 0)
          V.drawLine(v0, v4);
        if ((eflags & (1 << 7)) != 0)
          V.drawLine(v1, v3);
        if ((eflags & (1 << 8)) != 0)
          V.drawLine(v5, v3);
        if ((eflags & (1 << 9)) != 0)
          V.drawLine(v0, v2);
      }

      if (orientation == 0) {
        cy += 2;
      } else {
        cx++;
        cy++;
      }
      if (cx == p1.x && cy == p1.y)
        break;
    }
    V.popColor();
  }

}
