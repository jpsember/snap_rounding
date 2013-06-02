package snap;

import base.*;
import testbed.*;

public class BlackBoxSquareStrip extends BlackBox {
  public static final BlackBoxSquareStrip S = new BlackBoxSquareStrip();

  private BlackBoxSquareStrip() {
  }

  public BlackBox construct(Segment a, Segment b) {
    return new BlackBoxSquareStrip(a, b);
  }

  public boolean isValidPixel(int x, int y) {
    return true;
  }

  public int maxStripsPerPixel() {
    return 2;
  }

  public IPoint2 toStripSpace(int x, int y) {
    return new IPoint2(transformFwd(x), transformFwd(y));
  }

  public int compareSnapPoints(Segment seg, IPoint2 pixelA, IPoint2 pixelB) {
    int ret;
    if (!seg.hasNegativeSlope()) {
      ret = pixelA.x - pixelB.x;
      if (ret == 0)
        ret = pixelA.y - pixelB.y;
    } else {
      ret = pixelA.x - pixelB.x;
      if (ret == 0)
        ret = -(pixelA.y - pixelB.y);
    }
    return ret;
  }

  //  public void renderPixel(int x, int y) {
  //    Streams.out.println("renderPixel "+x+","+y+"; or="+orientation);
  //    switch (orientation) {
  //    case 1:
  //      {
  //        int tmp = x;
  //        x = y;
  //        y = tmp;
  //      }
  //      break;
  //    }
  //    super.renderPixel(x,y);
  //  }

  public IPoint2 toSweepSpace(IPoint2 pt) {
    switch (orientation) {
    case 1:
      pt = new IPoint2(pt.y, pt.x);
      break;
    }
    return pt;
  }
  /**
   * Transform point from left->right sweep to original orientation
   * @param pt
   * @return
   */
  public IPoint2 fromSweepSpace(int x, int y) {
    IPoint2 ret = null;

    switch (orientation) {
    default:
      ret = new IPoint2(x, y);
      break;
    case 1:
      ret = new IPoint2(y, x);
      break;
    }
    return ret;
  }

  public void renderSweepLine(int stripX) {
    //    vp V = TestBed.view();
    Grid g = Main.grid();

    double vx = stripX / 2.0;
    V.pushColor(MyColor.get(MyColor.GREEN, .28));

    //    Streams.out.println("render sweep line, or="+orientation+"\n"+Tools.st());
    switch (orientation()) {
    default:
      V.drawLine(g.toView(vx, 0, null), g.toView(vx, g.height() - 1, null));
      break;
    case 1:
      V.drawLine(g.toView(0, vx, null), g.toView(g.width() - 1, vx, null));
      break;
    }
    V.popColor();
  }

  public boolean segmentIntersectsPixel(int segx0, int segy0, int segx1,
      int segy1, int px2, int py2) {
    return segmentIntersectsPixel(segx0, segy0, segx1, segy1, px2, py2, true);
  }

  public Range getClipRangeWithinPixelColumn(Segment s, int pc) {
    final boolean db = false;

    if (db)
      Streams.out.println("getClipRangeWithinPixelColumn\n s=" + s.str()
          + " pc=" + pc);

    int r0, r1;
    if (isVertical(s)) {
      r0 = s.y0();
      r1 = s.y1();
      if (db)
        Streams.out.println(" (segment is vertical)");
    } else {
      IPoint2 sc0 = toStripSpace(s.pt(0)), sc1 = toStripSpace(s.pt(1));

      int stripLeft = firstStripInPixel(pc); //transformFwd(pc) - 1;
      int stripRight = firstStripInPixel(pc + 1); //transformFwd(pc + 1) - 1;

      int sx0 = sc0.x, sy0 = sc0.y, sx1 = sc1.x, sy1 = sc1.y;

      IPoint2 pt0 = (IPoint2) BlackBox.pixelOfIntersection(stripLeft, 0,
          stripLeft, 1, sx0, sy0, sx1, sy1, false);
      IPoint2 pt1 = (IPoint2) BlackBox.pixelOfIntersection(stripRight, 0,
          stripRight, 1, sx0, sy0, sx1, sy1, false);

      r0 = transformBwd(pt0.y);
      r1 = transformBwd(pt1.y);

      // new, simpler non-optimized way
      int add = s.hasNegativeSlope() ? -1 : 1;
      if (!segmentIntersectsPixel(s, pc, r0))
        r0 += add;
      else if (segmentIntersectsPixel(s, pc, r0 - add))
        r0 -= add;
      if (!segmentIntersectsPixel(s, pc, r1))
        r1 -= add;
      else if (segmentIntersectsPixel(s, pc, r1 + add))
        r1 += add;

      // clamp the range to fit within the vertical extent of the
      // original segment
      int sMax = Math.max(s.y0(), s.y1()), sMin = Math.min(s.y0(), s.y1());

      r0 = MyMath.clamp(r0, sMin, sMax);
      r1 = MyMath.clamp(r1, sMin, sMax);
    }
    Range dest = new ScalarRange(r0, r1);
    if (db)
      Streams.out.println(" setting range to " + dest);
    return dest;
  }

  private static int pixelContainingStrip(int stripX) {
    return transformBwd(stripX);
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int firstStripInPixel(int pixelColumnNumber) {
    return transformFwd(pixelColumnNumber) - 1;
  }

  /**
   * @param pixelColumnNumber
   * @return
   */
  public int lastStripInPixel(int pixelColumnNumber) {
    return transformFwd(pixelColumnNumber);
  }

  public int firstPixelColumnIntersectingStrip(int stripX) {
    return pixelContainingStrip(stripX);
  }

  public int lastPixelColumnIntersectingStrip(int stripX) {
    return pixelContainingStrip(stripX);
  }

  private static int transformFwd(int x) {
    return x * 2;
  }

  private static int transformBwd(int x) {
    return MyMath.floor(x + 1, 2);
  }

  /**
   * Construct a black box
   * @param a
   * @param b
   */
  private BlackBoxSquareStrip(Segment a, Segment b) {
    super(a, b);

    final boolean db = false;

    IPoint2 a0 = toStripSpace(a.pt(0)), a1 = toStripSpace(a.pt(1)), b0 = toStripSpace(b
        .pt(0)), b1 = toStripSpace(b.pt(1));


    Object obj = BlackBox.pixelOfIntersection(a0.x, a0.y, a1.x, a1.y, b0.x,
        b0.y, b1.x, b1.y, false);
    if (db)
      Streams.out.println(" obj ret=" + obj);

    if (!(obj instanceof IPoint2)) {
      state = ((Boolean) obj).booleanValue() ? COLLINEAR : PARALLEL;
    } else {
      IPoint2 ipt = (IPoint2) obj;
      if (db)
        Streams.out.println(" calculated point " + ab);
      ab = new IPoint2(ipt.x, ipt.y);
      abOrig = new IPoint2(transformBwd(ipt.x), transformBwd(ipt.y));

      if (db)
        Streams.out.println(" calculated point " + ab);

    }
    if (db)
      Streams.out.println("" + this);
  }

  public boolean abWithinSegments() {
    boolean ret = false;
    if (state == NOT_PARALLEL) {
      ret = a.boundsContains(abOrig) && b.boundsContains(abOrig);
    }
    return ret;
  }

  public IPoint2 snapInGridSpace(FPoint2 gridPt) {
    return new IPoint2(Math.floor(gridPt.x + .5), Math.floor(gridPt.y + .5));
  }

  public Grid getGrid() {
    Grid g = new SnapSquareGrid();
    return g;
  }

}
