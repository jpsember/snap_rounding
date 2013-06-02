package snap;

import base.*;
import testbed.*;

public class BlackBoxHexStrip extends BlackBox {

  public static final BlackBoxHexStrip S = new BlackBoxHexStrip();

  private BlackBoxHexStrip() {
  }

  
  
//  private static final boolean db = true;
//  @Override
//  public IPoint2 toSweep(IPoint2 pt) {
//    
//    IPoint2 ret = pt;
//    if (db) 
//      Streams.out.println("HS toSweep "+pt+": "+ret);
//          
//    return ret;
//  }
//
//  @Override
//  public IPoint2 toNormalized(IPoint2 pt) {
//    IPoint2 ret = pt;
//   if (db) 
//     Streams.out.println("HS toNormalized "+pt+": "+ret);
// return ret;
//   
//  }
// 
//  
  

  public void renderSweepLine(int stripX) {
//    vp V = TestBed.view();
    Grid g = Main.grid();
    double vx = stripX / 3.0;
    V.pushColor(MyColor.get(MyColor.GREEN, .28));
    Tools.unimp("");
//    if (!IPointUtils.isFlipped()) {
      V.drawLine(g.toView(vx, 0, null), g.toView(vx, g.height() - 1, null));
//    } else {
//      Tools.warn("flipped sweep not imp");
//    }
    V.popColor();
  }

  public int firstPixelColumnIntersectingStrip(int stripX) {
    int div = MyMath.floor(stripX, 3);
    int rem = MyMath.mod(stripX, 3);
    if (rem == 2)
      div++;
    return div;
  }

  public int lastPixelColumnIntersectingStrip(int stripX) {
    int div = MyMath.floor(stripX, 3);
    int rem = MyMath.mod(stripX, 3);
    if (rem > 0)
      div++;
    return div;
  }

  public boolean segmentIntersectsPixel(int segx0, int segy0, int segx1,
      int segy1, int px2, int py2) {

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

      if (!pixelWithinSegmentBounds(segx0, segy0, segx1, segy1, px2, py2))
        break;

      int scaledPixelX = px2 * 3, scaledPixelY = py2;
      int scaledSegX0 = segx0 * 3, scaledSegY0 = segy0, //
      scaledSegX1 = segx1 * 3, scaledSegY1 = segy1;

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
        if ((s0 == 0 && diagPass == 2) || s1 == 0)
          continue;

        if (s0 != s1) {
          out = true;
          break;
        }
      }
    } while (false);
    return out;
  }

  public int compareSnapPoints(Segment s, IPoint2 pixelA, IPoint2 pixelB) {
    int ret;
    if (!s.hasNegativeSlope()) {
      if ((s.x1() - s.x0()) * 3 > (s.y1() - s.y0())) {
        ret = pixelA.x - pixelB.x;
        if (ret == 0)
          ret = pixelA.y - pixelB.y;
      } else {
        ret = pixelA.y - pixelB.y;
        if (ret == 0)
          ret = pixelA.x - pixelB.x;
      }
    } else {
      if ((s.x1() - s.x0()) * 3 > (s.y0() - s.y1())) {
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

      //      if (true) {

      int add = s.hasNegativeSlope() ? -2 : 2;

      if (!segmentIntersectsPixel(s, pc, yLeft))
        yLeft += add;
      else if (segmentIntersectsPixel(s, pc, yLeft - add))
        yLeft -= add;
      if (!segmentIntersectsPixel(s, pc, yRight))
        yRight -= add;
      else if (segmentIntersectsPixel(s, pc, yRight + add))
        yRight += add;

      //      }
      //
      //      else { // OLD WAY
      //
      //        if (s.y1 > s.y0) {
      //          // did segment enter left side in top half of a pixel?
      //          if (parity) {
      //            // ensure line intersects top left diagonal pixel boundary
      //            boolean f = false;
      //            Object obj = BlackBox.pixelOfIntersection(xL, squareIsectLeft.y,
      //                xL + 1, squareIsectLeft.y + 1, scaledSeg.x0, scaledSeg.y0,
      //                scaledSeg.x1, scaledSeg.y1, false);
      //            if (db)
      //              Streams.out.println(" pixelOfInt= " + obj);
      //
      //            if (obj instanceof Boolean)
      //              f = ((Boolean) obj).booleanValue();
      //            else {
      //              IPoint2 diag = (IPoint2) obj;
      //              f = (diag.x == xL);
      //            }
      //            if (!f) {
      //              // move lower up by height of pixel
      //              yLeft += 2;
      //            }
      //            if (db)
      //              Streams.out
      //                  .println("left side top half, f=" + f + " yl=" + yLeft);
      //          }
      //        } else if (s.y1 < s.y0) {
      //          // did segment enter left side in bottom half of a pixel?
      //          if (!parity) {
      //            // ensure line intersects bottom left diagonal pixel boundary
      //            boolean f = false;
      //            Object obj = BlackBox.pixelOfIntersection(xL,
      //                squareIsectLeft.y + 1, xL + 1, squareIsectLeft.y, scaledSeg.x0,
      //                scaledSeg.y0, scaledSeg.x1, scaledSeg.y1, false);
      //            if (obj instanceof Boolean)
      //              f = ((Boolean) obj).booleanValue();
      //            else {
      //              IPoint2 diag = (IPoint2) obj;
      //              if (diag.x == xL)
      //                f = true;
      //              // special case for intersecting rightmost pixel of this diagonal,
      //              // which is actually contained in the pixel to the right
      //              if (diag.x == xL + 1 && diag.y == squareIsectLeft.y)
      //                f = true;
      //            }
      //            if (!f) {
      //              // lower by height of pixel
      //              yLeft -= 2;
      //            }
      //            if (db)
      //              Streams.out.println("left side bottom half, f=" + f + " yl="
      //                  + yLeft);
      //          }
      //        }
      //
      //        parity = ((pc + squareIsectRight.y) & 1) == 0;
      //        if (db)
      //          Streams.out.println("pc=" + pc + " parity=" + parity);
      //        if (s.y1 > s.y0) {
      //
      //          if (!segmentIntersectsPixel(s, pc, yRight))
      //            yRight -= 2;
      //
      //        } else {
      //          if (db)
      //            Streams.out
      //                .println(" did segment leave right side in top half of a pixel?");
      //
      //          if (!segmentIntersectsPixel(s, pc, yRight))
      //            yRight += 2;
      //
      //        }
      //      }

      // clamp the range to fit within the vertical extent of the
      // original segment
      int sMax = Math.max(scaledSeg.y0(), scaledSeg.y1()), sMin = Math.min(
          scaledSeg.y0(), scaledSeg.y1());
      if (true) {
        // expand bounds by 1 on each side to handle shifts into neighboring
        // pixels
        if (db)
          Streams.out.println(" clamp to range, sMin=" + sMin + ", sMax="
              + sMax);

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

      } else {
        rangeMin = MyMath.clamp(Math.min(yLeft, yRight), sMin, sMax);
        rangeMax = MyMath.clamp(Math.max(yLeft, yRight), sMin, sMax);
      }
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
    return pixelColumnNumber * 3 - 2;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int lastStripInPixel(int pixelColumnNumber) {
    return pixelColumnNumber * 3 + 1;
  }

  public Segment toStripSpace(Segment s) {
    return new Segment(transformFwd(s.x0()), s.y0(), transformFwd(s.x1()), s
        .y1());
  }

  public BlackBox construct(Segment a, Segment b) {
    return new BlackBoxHexStrip(a, b);
  }

  private static int transformFwd(int x) {
    return x * 3;
  }

  public IPoint2 toStripSpace(int x, int y) {
    return new IPoint2(transformFwd(x), y);
  }

  private BlackBoxHexStrip(Segment a, Segment b) {
    super(a, b);
    final boolean db = false;
    boolean spec = false;
    Segment ta = toStripSpace(a), tb = toStripSpace(b);
    if (db)
      Streams.out.println("BlackBoxHexStrip a=" + a.str() + " b=" + b.str()
          + "\n ta=" + ta.str() + " tb=" + tb.str());

    Object obj = BlackBox.pixelOfIntersection(ta.x0(), ta.y0(), ta.x1(), ta
        .y1(), tb.x0(), tb.y0(), tb.x1(), tb.y1(), false);

    if (!(obj instanceof IPoint2)) {
      state = ((Boolean) obj).booleanValue() ? COLLINEAR : PARALLEL;
    } else {
      IPoint2 ipt = (IPoint2) obj;
      if (db)
        Streams.out.println(" calculated point " + ipt);

      if (false) {
        Tools.warn("special test");
        if (Math.min(a.id(), b.id()) == 3 && Math.max(a.id(), b.id()) == 4) {
          spec = true;
          Streams.out.println("ipt was " + ipt);
          ipt.x = 1;
          ipt.y = 1;
        }
      }

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
        Streams.out.println("rem=" + remX + "," + remY + " t=" + tileX + ","
            + tileY);

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
      }

      if (db)
        Streams.out.println(" tx=" + tileX + " ty=" + tileY + " remx=" + remX
            + " remy=" + remY);

      // We now have the intersection point in some square pixel
      // with remainder 0..2, 0..1

      if (remY == 0) {
        boolean toRight = remX > 1;

        if (remX == 1) {
          // Determine which diagonal half of the intersection pixel contains
          // the intersection point.

          if (sideOfRationalLine(ta, tb, ipt.x + 1, ipt.y, ipt.x, ipt.y + 1) )
            toRight = true;

        }
        if (toRight) {
          tileX++;
          tileY++;
          if (db)
            Streams.out.println(" incremented tx,ty to " + tileX + "," + tileY);
        }
      } else {
        boolean toRight = remX > 1;
        if (remX == 1) {
          // Determine which diagonal half of the intersection pixel contains
          // the intersection point.

          toRight = sideOfRationalLine(ta, tb, ipt.x, ipt.y, ipt.x + 1,
              ipt.y + 1) ;
        }
        if (toRight) {
          tileX++;
          tileY++;
          if (db)
            Streams.out.println(" incremented tx,ty to " + tileX + "," + tileY);

        } else {
          tileY += 2;
          if (db)
            Streams.out.println(" incr'd ty to " + tileY);
        }
      }
      //      }
      if (spec) {

      }

      ab = ipt;

      // determine original (untransformed) pixel containing this
      // intersection point.
      abOrig = new IPoint2(tileX, tileY);
      if (db)
        Streams.out.println(" abOrig=" + abOrig);

      //      // see if point is within each segment.
      //      abWithinSegments = pixelWithinSegmentBounds(a.x0, a.y0, a.x1, a.y1,
      //          abOrig.x, abOrig.y)
      //          && pixelWithinSegmentBounds(b.x0, b.y0, b.x1, b.y1, abOrig.x,
      //              abOrig.y);
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

   static int withinBounds(int r0, int r1, int c) {
    if (r1 < r0) {
      int tmp = r1;
      r1 = r0;
      r0 = tmp;
    }
    if (c >= r0 && c <= r1)
      return 0;
    if (c >= r0 - 1 && c <= r1 + 1)
      return 1;
    return 2;
  }

  private boolean pixelWithinSegmentBounds(int x0, int y0, int x1, int y1,
      int px, int py) {
    final boolean db = false;

    int fx = withinBounds(x0, x1, px);
    int fy = withinBounds(y0, y1, py);
    if (db && T.update())
      T.msg("pixelWithinSegmentBounds seg=" + x0 + " " + y0 + " " + x1 + " "
          + y1 + " pix=" + px + " " + py + "\n fx=" + fx + " fy=" + fy);

    return (fx + fy <= 1);
  }

  public Grid getGrid( ) {
    return new HexGrid(); //size, ls);
  }

  public IPoint2 snapInGridSpace(FPoint2 gridPt) {
    return HexGrid.snapGridSpace(gridPt);
  }

  //  /**
  //   * Snap point to approximate closest center of hex pixel.
  //   * @param gridPt : position of point in grid space
  //   * @return approximate closest center of hex pixel 
  //   * @deprecated
  //   */
  //  public static IPoint2 snapToHex(FPoint2 gridPt) {
  //    
  //    if (true) {
  //      return HexGrid.snapGridSpace(gridPt);
  //    }
  //    
  //    double sx = gridPt.x * 3;
  //    double sy = gridPt.y;
  //    IPoint2 ret = null;
  //    double ox = MyMath.mod(sx, 6);
  //    double oy = MyMath.mod(sy, 2);
  //    double tx = sx - ox, ty = sy - oy;
  //
  //    if (oy >= 1) {
  //      if (ox >= 4.5) {
  //        ret = new IPoint2(tx + 6, ty + 2);
  //      } else if (ox >= 1.5) {
  //        ret = new IPoint2(tx + 3, ty + 1);
  //      } else {
  //        ret = new IPoint2(tx, ty + 2);
  //      }
  //    } else {
  //      if (ox >= 4.5) {
  //        ret = new IPoint2(tx + 6, ty);
  //      } else if (ox >= 1.5) {
  //        ret = new IPoint2(tx + 3, ty + 1);
  //      } else {
  //        ret = new IPoint2(tx, ty);
  //      }
  //    }
  //    ret.x /= 3;
  //    return ret;
  //  }
  //  
}
