package snap;

import base.*;
import testbed.*;
import java.util.*;

public class Main extends TestBed {

  /*! .enum  .private .prefix G_ 4000
    _ _ _  randomize _ labelsegs 
    randombwd _ _ includevert  _ gridtype gsquare ghex gtri _
    test tnone tisect trange tpixorder segpixisect tgenseg  hbsect
    _ _ _ _ _ _ _ _ hex2 hbsegpixisect
  */

  private static final int G_RANDOMIZE = 4003;//!
  private static final int G_RANDOMBWD = 4006;//!
  private static final int G_INCLUDEVERT = 4009;//!
  private static final int G_GRIDTYPE = 4011;//!
  private static final int G_GSQUARE = 4012;//!
  private static final int G_GHEX = 4013;//!
  private static final int G_GTRI = 4014;//!
  private static final int G_TEST = 4016;//!
  private static final int G_TNONE = 4017;//!
  private static final int G_TISECT = 4018;//!
  private static final int G_TRANGE = 4019;//!
  private static final int G_TPIXORDER = 4020;//!
  private static final int G_SEGPIXISECT = 4021;//!
  private static final int G_TGENSEG = 4022;//!
  private static final int G_HBSECT = 4023;//!
  private static final int G_HEX2 = 4032;//!
  private static final int G_HBSEGPIXISECT = 4033;//!
  /* !*/

  //  private static final boolean BUILDSEGS = false;
  //  private static boolean r(int k) {
  //    double rad = MyMath.radians(k);
  //    double a = MyMath.normalizeAngle(rad);
  //    if (a >= Math.PI / 2)
  //      a -= Math.PI;
  //    if (a < -Math.PI / 2)
  //      a += Math.PI;
  //
  //    boolean f = (a >= MyMath.radians(-60) && a <= MyMath.radians(60));
  //    if (db)
  //      Streams.out.println("k=" + k + ", a=" + a + ", f=" + f);
  //
  //    return f;
  //  }
  //  private static boolean db = false;
  //
  //  /**
  //   * main() method
  //   * 
  //   * @param args
  //   */
  //  public static void main(String[] args) {
  //    outer: for (int i = -59; i < 59; i += 5) {
  //      for (int j = -121; j < 120; j += 3) {
  //
  //        if (j >= -60 && j < 0)
  //          j += 120;
  //
  //        //          db = (i == -59 && j == -121);
  //        boolean f = false;
  //        for (int k = 0; k < 3; k++) {
  //          f |= r(i + k * 60) && r(j + k * 60);
  //          if (f)
  //            break;
  //        }
  //        //          if (db) 
  //        if (!f) {
  //          Streams.out.println("i=" + i + " j=" + j);
  //          break outer;
  //        }
  //
  //      }
  //    }
  //  }
  /**
   * main() method
   * 
   * @param args
   */
  public static void main(String[] args) {

    // Construct an object for the application and pass args to it
    new Main().doMainGUI(args);
  }

  // -------------------------------------------------------
  // TestBed overrides
  // -------------------------------------------------------

  public void initEditor() {
    Editor.addObjectType(SnapEdSegment.FACTORY);
  }

  public void processAction(TBAction a) {
    if (a.code == TBAction.CTRLVALUE) {
      switch (a.ctrlId) {
      case G_GRIDTYPE:
      case G_GSQUARE:
      case G_GHEX:
      case G_GTRI:
        prepareGrid();
        break;
      case G_RANDOMIZE:
        doRandomize(1);
        break;
      case G_RANDOMBWD:
        doRandomize(-1);
        break;
      }
    }
  }

  public static void doRandomize(int step) {
    Editor.replaceAllObjects(Generator.generate(step));
  }

  public void setParameters() {
    parms.includeEditOper = false; // we have our own
    parms.appTitle = "Snap Rounding";
    parms.menuTitle = "SnapRound";
    parms.fileExt = "seg";
  }

  public void addControls() {
    C.sOpen();
    {
      C.sOpen();
      {
        C.sOpenComboBox(G_GRIDTYPE, "Grid type:", "Select type of grid to use",
            false);
        C.sChoice(G_GSQUARE, "Square");
        C.sChoice(G_GHEX, "Hexagonal");
        C.sChoice(G_GTRI, "Triangular");
        C.sCloseComboBox();
      }
      C.sNewColumn();
      C.sCheckBox(G_INCLUDEVERT, "Include vertical segs", null, true);
      C.sClose();
    }

    C.sOpen();
    C.sButton(G_RANDOMIZE, "Random+",
        "Generate a set of random segments (incrementing seed)");
    C.sNewColumn();
    C.sButton(G_RANDOMBWD, "Random-",
        "Generate a set of random segments (decrementing seed)");
    C.sNewColumn();
    C.sClose();
    C.sClose();
  }
  //  public static boolean debugMode() {return parms.debug;}
  public void addOperations() {
    addOper(defaultOper);
    addOper(new SnapOper());
    addOper(new BundleOper());
    addOper(new Generator());

  }

  //  private static int px, py;

  public void paintView() {

    //    if (V.hasViewChanged())
    prepareGrid();

    //    grid = buildGrid();
    segList = null;

    super.paintView();
  }

  protected void doInit() {
    super.doInit();
    //    buildGrid();
    prepareGrid();
  }

  // -------------------------------------------------------

  private static Grid buildGrid() {
    int gType = C.vi(G_GRIDTYPE);
    blackBox(gType - G_GSQUARE);

    Grid g = blackBox().getGrid();

    g.setSize(Editor.gridSize(), V.logicalSize());
    V.setGrid(g);
    return g;
  }

  private static void prepareGrid() {
    grid = buildGrid();
    if (blackBox != null) {
      for (Iterator it = Editor
          .readObjects(SnapEdSegment.FACTORY, false, false).iterator(); it
          .hasNext();) {
        SnapEdSegment s = (SnapEdSegment) it.next();
        s.useGrid(grid);
      }
    }

  }

  private static void testSegmentsIntersection(Segment a, Segment b) {
    boolean pr = false;
    if (pr)
      Streams.out.print(a.x0() + "," + a.y0() + "," + a.x1() + "," + a.y1()
          + "," + b.x0() + "," + b.y0() + "," + b.x1() + "," + b.y1() + ",");
    BlackBox ab = blackBox().construct(a, b);
    switch (ab.state()) {
    case BlackBox.COLLINEAR:
      if (pr)
        Streams.out.print("TRUE,");
      break;
    case BlackBox.PARALLEL:
      if (pr)
        Streams.out.print("FALSE,");
      break;
    default:
      {
        IPoint2 pt = ab.getIntersectionPixel(false);
        if (pr)
          Streams.out.print(pt.x + "," + pt.y + ",");

        if (!ab.abWithinSegments())
          break;

        Main.grid().highlightCell(pt);
      }
      break;
    }
    if (pr)
      Streams.out.println(" //");
  }

  private static void testHBundleSegmentsIntersection(Segment a, Segment b) {
    boolean pr = false;
    if (pr)
      Streams.out.print(a.x0() + "," + a.y0() + "," + a.x1() + "," + a.y1()
          + "," + b.x0() + "," + b.y0() + "," + b.x1() + "," + b.y1() + ",");

    BlackBoxHexBundle.S.setOrientation(C.vb(G_HEX2) ? 1 : 0);

    BlackBoxHexBundle ab = (BlackBoxHexBundle) BlackBoxHexBundle.S.construct(a,
        b);

    switch (ab.state()) {
    case BlackBox.COLLINEAR:
      if (pr)
        Streams.out.print("TRUE,");
      break;
    case BlackBox.PARALLEL:
      if (pr)
        Streams.out.print("FALSE,");
      break;
    default:
      {
        IPoint2 pt = ab.getIntersectionPixel(false);
        if (pr)
          Streams.out.print(pt.x + "," + pt.y + ",");

        if (!ab.abWithinSegments())
          break;

        Grid g = Main.grid();
        g.highlightCell(pt);

        ab.highlightStripPixel();
      }
      break;
    }
    if (pr)
      Streams.out.println(" //");
  }

  private static void testSegmentIsectsPixel(Segment s, boolean hexBundle) {
    BlackBox b = blackBox();
    if (b instanceof BlackBoxHexStrip && hexBundle) {
      b = BlackBoxHexBundle.S;
    }
    b.setOrientation(C.vb(G_HEX2) ? 1 : 0);

    final int TRIM = 2;
    for (int x = s.x0() - TRIM; x <= s.x1() + TRIM; x++) {
      for (int y = Math.min(s.y0(), s.y1()) - TRIM; y <= Math.max(s.y0(), s
          .y1())
          + TRIM; y++) {
        if (!b.isValidPixel(x, y))
          continue;

        if (b.segmentIntersectsPixel(s, x, y))
          grid().highlightCell(x, y);
      }
    }
  }

  private static void testSegmentRange(Segment s) {
    //    vp vp = TestBed.view();
    BlackBox b = blackBox();

    for (int x = s.x0();; x++) {
      int maxX = s.x1();
      if (b.isVertical(s) && b instanceof BlackBoxTriStrip)
        maxX++;
      if (x > maxX)
        break;

      Range r = b.getClipRangeWithinPixelColumn(s, x);
      if (r == null)
        continue;

      for (int y = r.y0() - 1; y <= r.y1() + 1; y++) {
        IPoint2 pt = new IPoint2(x, y);
        if (!b.isValidPixel(pt))
          continue;
        boolean f = b.segmentIntersectsPixel(s, x, y);
        if (f != r.contains(y)) {
          V.pushColor(MyColor.get(MyColor.RED));
          V.drawCircle(grid().toView(pt), 3);
          V.popColor();
        }
        if (r.contains(y))
          grid().highlightCell(pt);
      }
      if (false) // stop after first column?
        break;
    }
  }

  private static void testPixelOrdering(Segment s2) {

    if (s2.x0() == s2.x1() && s2.y0() == s2.y1())
      return;
    FPoint2 p0 = grid.toView(s2.pt(0)), p1 = grid.toView(s2.pt(1));
    double len = Math.sqrt((s2.x1() - s2.x0()) * (s2.x1() - s2.x0())
        + (s2.y1() - s2.y0()) * (s2.y1() - s2.y0()));

    DArray pixels = new DArray();
    BlackBox b = blackBox();
    for (int x = s2.x0(); x <= s2.x1(); x++) {
      Range r = b.getClipRangeWithinPixelColumn(s2, x);
      if (r == null)
        continue;
      for (int y = r.y0(); y <= r.y1(); y++) {
        IPoint2 p = new IPoint2(x, y);
        if (!b.isValidPixel(p))
          continue;
        pixels.add(p);
      }
    }

    pixels.permute(null);

    final Segment s = s2;

    if (true) {
      pixels.sort(new Comparator() {
        public int compare(Object arg0, Object arg1) {
          IPoint2 pt0 = (IPoint2) arg0, pt1 = (IPoint2) arg1;
          return Main.blackBox().compareSnapPoints(s, pt0, pt1);
        }
      });
    } else

      pixels.sort(new Comparator() {
        public int compare(Object arg0, Object arg1) {
          IPoint2 pt0 = (IPoint2) arg0, pt1 = (IPoint2) arg1;

          int t0 = (pt0.x - s.x0()) * (s.x1() - s.x0()) + (pt0.y - s.y0())
              * (s.y1() - s.y0());
          int t1 = (pt1.x - s.x0()) * (s.x1() - s.x0()) + (pt1.y - s.y0())
              * (s.y1() - s.y0());

          return t0 - t1;
        }
      });

    V.pushScale(.7);
    V.pushColor(MyColor.get(MyColor.RED));
    for (int i = 0; i < pixels.size(); i++) {
      IPoint2 p = (IPoint2) pixels.get(i);
      FPoint2 vpix = grid.toView(p);

      if (!Main.hexGrid()) {
        double t = (p.x - s.x0()) * (s.x1() - s.x0()) + (p.y - s.y0())
            * (s.y1() - s.y0());

        t = t / (len * len);
        FPoint2 tpt = FPoint2.interpolate(p0, p1, t);
        V.drawLine(vpix, tpt);
      }

      V.draw("" + i, vpix, TX_BGND);
    }
    V.popColor();
    V.popScale();
  }
  private static Segment filter(Segment s) {
    if (s == null || (s.x1() - s.x0() > 230) || Math.abs(s.y1() - s.y0()) > 230)
      s = null;
    return s;
  }

  private static TestBedOperation defaultOper = new TestBedOperation() {
    public void addControls() {
      {
        C.sOpenTab("Edit");

        C.sOpenComboBox(G_TEST, "Test function",
            "Selects black box testing operation", true);
        C.sChoice(G_TNONE, "None");
        C.sChoice(G_TISECT, "Seg/seg intersection");
        C.sChoice(G_HBSECT, "Seg/seg int (hex bndl)");
        C.sChoice(G_TRANGE, "Seg range within pixel column");
        C.sChoice(G_TPIXORDER, "Pixel ordering within segment");
        C.sChoice(G_SEGPIXISECT, "Seg/pixel intersection");
        C.sChoice(G_HBSEGPIXISECT, "Seg/pixel int (hex bndl)");

        C.sCloseComboBox();
        C.sCheckBox(G_HEX2, "Orientation #2", null, false);
        C.sCloseTab();
      }
      //      if (BUILDSEGS) {
      //        C.sOpen();
      //        C.sIntSpinner(G_SEGNUM, "Retain segs:",
      //            "Doesn't replace first n segments", 0, 12, 0, 2);
      //        C.sDoubleSpinner(G_TIX, "x:", "X-coordinate of intersection", 0, 10000,
      //            50, .01);
      //        C.sDoubleSpinner(G_TIY, "y:", "Y-coordinate of intersection", 0, 10000,
      //            50, .01);
      //        C.sDoubleSpinner(G_TM0, "slope A:", "slope of first seg", -10000,
      //            10000, 0, .01);
      //        C.sDoubleSpinner(G_TM1, "slope B:", "slope of second seg", -10000,
      //            10000, 1, .01);
      //        C.sIntSpinner(G_MTOLER, "slope tolerance (lg):",
      //            "Tolerance for slopes", -20, 0, -5, 1);
      //        C.sNewColumn();
      //        C.sButton(G_GEN, "Generate", "Attempt to generate pair of segs");
      //        C.sClose();
      //      }
    }

    public void runAlgorithm() {
    }
    public void paintView() {
      //      plotGrid();
      //      plotEditor();
      Editor.render();

      Segment[] segs = getSegments();
      do {
        if (C.vi(G_TEST) == G_TNONE)
          break;

        if (segs.length < 1)
          break;
        Segment s = filter(segs[0]);
        Segment s2 = filter((segs.length >= 2) ? segs[1] : null);
        if (s == null)
          break;

        V.draw(s.str(), 0, 100, TX_CLAMP);
        if (s2 != null)
          V.draw(s2.str(), 100, 100, TX_CLAMP);

        switch (C.vi(G_TEST)) {
        case G_TISECT:
          if (s2 == null)
            break;
          testSegmentsIntersection(s, s2);
          break;
        case G_TRANGE:
          testSegmentRange(s);
          break;
        case G_TPIXORDER:
          testPixelOrdering(s);
          break;
        case G_SEGPIXISECT:
          testSegmentIsectsPixel(s, false);
          break;
        case G_HBSEGPIXISECT:
          testSegmentIsectsPixel(s, true);
          break;
        case G_TGENSEG:
          for (int k = 0; k < segs.length - 1; k += 2) {
            testSegmentsIntersection(segs[k], segs[k + 1]);
          }
          break;
        case G_HBSECT:
          if (s2 == null)
            break;
          testHBundleSegmentsIntersection(s, s2);
          break;
        }
      } while (false);
    }

    public void processAction(TBAction a) {
      if (a.code == TBAction.CTRLVALUE)
        switch (a.ctrlId) {
        //        case G_GEN:
        //          generateIntersectingSegs();
        //          break;
        }
    }
  };

  //  private static void generateIntersectingSegs() {
  //    DArray newEditorItems = new DArray();
  //    {
  //      int ret = C.vi(G_SEGNUM);
  //      if (ret < newEditorItems.size())
  //        newEditorItems.removeRange(ret, newEditorItems.size());
  //    }
  //
  //    FPoint2 intersectPt = new FPoint2(C.vd(G_TIX), C.vd(G_TIY));
  //    double m0 = C.vd(G_TM0), m1 = C.vd(G_TM1);
  //    if (m0 > m1) {
  //      double t = m0;
  //      m0 = m1;
  //      m1 = t;
  //    }
  //
  //    double mToler = Math.pow(2.0, C.vi(G_MTOLER));
  //
  //    do {
  //      IPoint2[] e0 = findSegment(blackBox(), intersectPt, m0, mToler);
  //      if (e0 == null)
  //        break;
  //      IPoint2[] e1 = findSegment(blackBox(), intersectPt, m1, mToler);
  //      if (e1 == null)
  //        break;
  //
  //      Segment a = new Segment(e0[0], e0[1]);
  //      Segment b = new Segment(e1[0], e1[1]);
  //
  //      BlackBox bb = blackBox().construct(a, b);
  //      if (bb.state() == BlackBox.NOT_PARALLEL)
  //        Streams.out.println("isect=\n" + bb);
  //      newEditorItems.add(new EdSegment(a));
  //      newEditorItems.add(new EdSegment(b));
  //      break;
  //    } while (false);
  //
  //    Editor.replaceAllObjects(newEditorItems);
  //
  //  }

  //  /**
  //   * Find a segment that approximately contains a point, 
  //   * and whose slope is approximately of a desired value
  //   * @param ipt : point on line
  //   * @param mDes : slope of line
  //   * @param mToler : tolerance of slope
  //   * @return IPoint2[] endpoints of segment (left + right), or null if unsuccessful
  //   */
  //  private static IPoint2[] findSegment(BlackBox blackBox, FPoint2 ipt,
  //      double mDes, double mToler) {
  //    IPoint2[] ret = null;
  //
  //    do {
  //      IPoint2 pt0 = findPixel(blackBox, ipt, mDes, mToler, false);
  //      if (pt0 == null)
  //        break;
  //      IPoint2 pt1 = findPixel(blackBox, ipt, mDes, mToler, true);
  //      if (pt1 == null)
  //        break;
  //      ret = new IPoint2[2];
  //      ret[0] = pt0;
  //      ret[1] = pt1;
  //    } while (false);
  //    return ret;
  //  }
  //
  //
  //  /**
  //   * Find a pixel that is on a line with a particular slope that contains 
  //   * a point
  //   * @param ipt : point on line
  //   * @param mDes : slope of line
  //   * @param mToler : tolerance of slope
  //   * @param toRight : if true, pixel is to right of ipt; else, to left
  //   * @return pixel
  //   */
  //  private static IPoint2 findPixel(BlackBox blackBox, FPoint2 ipt, double mDes,
  //      double mToler, boolean toRight) {
  //    final boolean db = false;
  //
  //    if (db)
  //      Streams.out.println("findPixel ipt=" + ipt + " mDes=" + Tools.f(mDes)
  //          + " mToler=" + mToler);
  //
  //    Random r = new Random(1965);
  //    double dist = r.nextDouble() * 20 + 2000;
  //    double rSide = toRight ? 1 : -1;
  //    IPoint2 ret = null;
  //    int pass = 0;
  //    for (; pass < 10000; pass++) {
  //
  //      dist += (r.nextDouble() * 4 + .2);
  //
  //      FPoint2 tpt = new FPoint2(ipt.x + rSide * dist, ipt.y + rSide * dist
  //          * mDes);
  //
  //      IPoint2 i2 = blackBox.snapInGridSpace(tpt);
  //
  //      double mCurr = (i2.y - ipt.y) / (i2.x - ipt.x);
  //
  //      if (db)
  //        Streams.out.println("Pass " + pass + " gridpt=" + i2 + " slope="
  //            + mCurr + " diff=" + Math.abs(mCurr - mDes));
  //
  //      if (Math.abs(mCurr - mDes) > mToler)
  //        continue;
  //      ret = i2;
  //      break;
  //    }
  //    if (db)
  //      Streams.out.println(" returning " + ret);
  //
  //    return ret;
  //  }

  //  static void plotGrid() {
  //    if (C.vb(G_PLOTGRID))
  //      grid.render(  C.vb(G_LABELGRID));
  //  }

  //    static void plotEditor() {
  //      
  //      Editor.render();
  //    }

  static boolean labelSegments() {
    return Editor.withLabels(false) &&
    //C.vb(G_LABELSEGS) && 
        getSegments().length < 30;
  }

  /**
   * Construct list of segments
   */
  public static Segment[] getSegments() {
    if (segList == null) {
      segList = SnapEdSegment.getSegments(blackBox, Editor.readObjects(
          SnapEdSegment.FACTORY, false, true).iterator(), C.vb(G_INCLUDEVERT));
    }
    return segList;
  }

  private static BlackBox blackBox(int gridType) {
    switch (gridType + G_GSQUARE) {
    default:
      throw new UnsupportedOperationException();
    case G_GSQUARE:
      blackBox = BlackBoxSquareStrip.S;
      break;
    case G_GHEX:
      blackBox = BlackBoxHexStrip.S;
      break;
    case G_GTRI:
      blackBox = BlackBoxTriStrip.S;
      break;
    }
    return blackBox;
  }

  public static BlackBox blackBox() {
    if (blackBox == null) {
      blackBox(C.vi(G_GRIDTYPE) - G_GSQUARE);
    }
    return blackBox;
  }

  public static boolean hexGrid() {
    return grid instanceof HexGrid;
  }

  public static Grid grid() {
    if (grid == null)
      grid = buildGrid();
    return grid;
  }

  public static boolean highDetail() {
    return grid().cellSize() > 3;
  }

  private static Segment[] segList;
  private static Grid grid;
  //  private static FPoint2 logSize;
  private static BlackBox blackBox;

  public static boolean includeVert() {
    return C.vb(G_INCLUDEVERT);
  }
}
