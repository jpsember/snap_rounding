package snap;

import base.*;
import testbed.*;
import java.util.*;

class Generator implements TestBedOperation {

  /*! .enum  .private 1300
  count go _ seglen cubicn slanted _ oper circn circk _ circr _
  _ hulln hullk hullr seed  
  */

  private static final int COUNT = 1300;//!
  private static final int GO = 1301;//!
  private static final int SEGLEN = 1303;//!
  private static final int CUBICN = 1304;//!
  private static final int SLANTED = 1305;//!
  private static final int OPER = 1307;//!
  private static final int CIRCN = 1308;//!
  private static final int CIRCK = 1309;//!
  private static final int CIRCR = 1311;//!
  private static final int HULLN = 1314;//!
  private static final int HULLK = 1315;//!
  private static final int HULLR = 1316;//!
  private static final int SEED = 1317;//!
  /* !*/

  public void processAction(TBAction a) {
    if (a.code == TBAction.CTRLVALUE) {
      DArray items = null;
      switch (a.ctrlId) {
      default:
        if (a.ctrlId < 1300 || a.ctrlId > 1399)
          break;
        items = generate(0);
        break;
      }
      if (items != null) {
        Editor.replaceAllObjects(items);
      }
    }
  }

  private static IPoint2 rndPoint(Random r, int sizex, int sizey, double tx,
      double ty) {
    return new IPoint2(tx + r.nextInt(sizex), ty + r.nextInt(sizey));
  }

  private static DArray segs;
  private static Map map;
  private static void clearSegs() {
    map = new HashMap();
    segs = new DArray();
  }
  private static void addSeg(IVector p0, IVector p1) {
    IPoint2 ip0 = IPoint2.construct(p0);
    IPoint2 ip1 = IPoint2.construct(p1);
    String str = ip0.toString() + " " + ip1.toString();
    if (!map.containsKey(str)) {
      map.put(str, str);
      segs.add(new SnapEdSegment(ip0, ip1));
    }
  }

  private static DArray generateCirc() {

    clearSegs();

    int n = C.vi(CIRCN);
    int k = C.vi(CIRCK);

    //    Grid g = Main.grid();

    double radius = C.vi(CIRCR) / 50.0;
    k = Math.min(k, n - 2);

    IPoint2[] pts = new IPoint2[n];
    for (int i = 0; i < n; i++) {
      FPoint2 p = MyMath.ptOnEllipse(gOrigin, MyMath.radians((i * 360.0) / n),
          radius * gOrigin.x, radius * gOrigin.y);
      pts[i] = new IPoint2(p.x, p.y);
    }
    int k2 = Math.max(k, 1);
    for (int i = 0; i < n; i++) {
      if (k == 0 && i >= n / 2)
        break;
      for (int s = 0; s < k2; s++) {
        int j = MyMath.mod(i + n / 2 + s - k / 2, n);

        addSeg(pts[i], pts[j]);
      }
    }

    return segs;
  }

  private static IPoint2 rndEllipsePt(Random r, double rad) {
    return new IPoint2(MyMath.ptOnEllipse( //
        gOrigin, //
        r.nextDouble() * Math.PI * 2, //
        rad * gOrigin.x, //
        rad * gOrigin.y));
  }

  private static DArray generateHull(Random r) {

    int n = C.vi(HULLN);
    int k = C.vi(HULLK);

    double radius = C.vi(HULLR) / 50.0;

    DArray pts = new DArray();

    for (int i = 0; i < n; i++)
      pts.add(rndEllipsePt(r, radius * r.nextDouble()));
    DArray ch = MyMath.convexHull(pts);

    clearSegs();
    if (ch.size() > 0) {
      for (int i = 0; i < k; i++) {
        addSeg((IPoint2) pts.get(ch.getInt(r.nextInt(ch.size()))),
            (IPoint2) pts.get(ch.getInt(r.nextInt(ch.size()))));
      }

    }
    return segs;
  }
  private static DArray generateCubic() {
    int cnt = (C.vi(CUBICN) & ~1);

    int rowSize = (cnt * cnt) / 4;
    int chunkSize = (cnt / 2);

    boolean slanted = C.vb(SLANTED);
    int height = slanted ? rowSize / 2 : 1;

    // determine offsets to center it within the current grid
    Grid g = Main.grid();

    int xc = (g.width() - rowSize) / 2, yc = (g.height() - height) / 2;

    //    DArray ar = new DArray();

    // Add horizontal segs
    double x0 = xc - 1, x1 = xc + rowSize;

    double ysep = 1.0 / chunkSize;

    double htolerance = .1;

    while (true) {
      clearSegs();
      try {
        for (int j = 0; j < chunkSize; j++) {
          double y0 = yc - .5 + j * ysep + ysep * .5;

          MyLine orig = new MyLine(x0, y0, x1, y0);
          if (slanted)
            slant(orig, xc, yc);

          MyLine ln = MyLine.constructApprox(orig, ysep * htolerance);

          addSeg(ln.endPoint(0), ln.endPoint(1));
        }

        // Add slanted segs
        int x = 0;
        while (x < rowSize) {
          MyLine orig = new MyLine(xc + x - .5, yc - .5, xc + x + chunkSize
              - .5, yc + .5);
          if (slanted)
            slant(orig, xc, yc);

          MyLine ln = MyLine.constructApprox(orig, ysep * .1);

          addSeg(ln.endPoint(0), ln.endPoint(1));
          x += chunkSize;
        }
        break;
      } catch (ArithmeticException e) {
        htolerance += .02;
        if (htolerance > .49) {
          Main.showError(e.toString());
          break;
        }
      }
    }
    return segs;
  }

  private static void slant(MyLine ln, int x0, int y0) {
    ln.set(ln.x1, ln.y1 + (ln.x1 - x0) * .5, ln.x2, ln.y2 + (ln.x2 - x0) * .5);
  }

  private static int startSeed = (int) System.currentTimeMillis();

  public static int lastSeed() {
    return startSeed;
  }

  private static Random getRand(int step) {
    int seed = C.vi(SEED);
    if (seed == 0) {
      if (step != 0) {
        startSeed += step;
        seed = MyMath.mod(startSeed, 1000000);
      } else {
        seed = (int) System.currentTimeMillis();
      }
    }
    Random r = new Random(seed);
    return r;
  }

  private static Grid grid;
  private static FPoint2 gOrigin;

  public static DArray generate(int step) {
    grid = Main.grid();
    gOrigin = new FPoint2(grid.width() / 2, grid.height() / 2);

    DArray items = null;
    switch (C.vi(OPER)) {
    case 0:
      items = generateRandom(getRand(step));
      break;
    case 1:
      items = generateCubic();
      break;
    case 2:
      items = generateCirc();
      break;
    case 3:
      items = generateHull(getRand(step));
      break;
    }
    return items;
  }

  private static DArray generateRandom(Random r) {
    int cnt = C.vi(COUNT);
    int segLen = C.vi(SEGLEN);

    clearSegs();
    int rLenX = Math.max(1, grid.width() * segLen / 100);
    int rLenY = Math.max(1, grid.height() * segLen / 100);

    while (segs.size() != cnt) {
      int rSizeX = 0;
      if (rLenX > 1)
        rSizeX = r.nextInt(rLenX);
      rSizeX = Math.max(3, rSizeX);
      int fillX = grid.width() - rSizeX;

      int rSizeY = 0;
      if (rLenY > 1)
        rSizeY = r.nextInt(rLenY);
      rSizeY = Math.max(3, rSizeY);

      int fillY = grid.height() - rSizeY;

      int tx = 0, ty = 0;
      if (fillX > 0)
        tx = r.nextInt(1 + (int) fillX);
      if (fillY > 0)
        ty = r.nextInt(1 + (int) fillY);

      IPoint2 p0 = rndPoint(r, rSizeX, rSizeY, tx, ty);
      IPoint2 p1 = rndPoint(r, rSizeX, rSizeY, tx, ty);

      if (!Main.includeVert() && p1.x == p0.x)
        continue;

      addSeg(p0, p1);
    }
    return segs;
  }

  public void addControls() {
    C.sOpenTab("Generator");
    C.sStaticText("Generates random segments");
    //    C.sButton(TEST, "Test", null);
    {
      C.sOpenTabSet(OPER);
      C.sOpenTab("Random");
      C.sOpen();
      C.sIntSpinner(COUNT, "Count:", null, 0, 4000, 100, 5);
      //      C.sIntSlider(COUNT, "Count", null, 0, 4000, 100, 5, false);
      //      C.sIntSlider(SEED, "Random seed", null, 0, 10000, 100, 50, false);
      C.sNewColumn();
      C.sIntSlider(SEGLEN, "Segment length", null, 0, 100, 50, 10);
      C.sClose();
      C.sCloseTab();

      C.sOpenTab("Worst-case O(n^3)");
      C.sStaticText("Attempts to generate segments that produce the worst-case"
          + " total number of polyline fragments, by choosing integer "
          + "endpoints that cause"
          + " portions of the segments to fall within certain sub-pixel "
          + "ranges.  See paper by Halperin & Packer.");
      {
        C.sOpen();
        C.sCheckBox(SLANTED, "Slanted", null, false);
        C.sNewColumn();
        C.sIntSpinner(CUBICN, "# segments", null, 6, 300, 6, 2);
        //        C.sNewColumn();
        //        C.sButton(GO2, "Generate", null);
        C.sClose();
      }
      C.sCloseTab();

      C.sOpenTab("Circle");
      C
          .sStaticText("Generates points in a circle, and connects each point to the k furthest points from it");
      {
        C.sOpen();
        C.sIntSlider(CIRCN, "n", "Number of points", 2, 1000, 20, 1);
        C.sIntSlider(CIRCK, "k", "Number of furthest neighbors", 0, 10, 2, 1);
        C.sIntSlider(CIRCR, "radius", null, 5, 50, 40, 1);
        C.sClose();
      }
      C.sCloseTab();

      C.sOpenTab("Hull");
      C
          .sStaticText("Generates convex hull of n random points, and k random segments between hull points");
      {
        C.sOpen();
        C.sIntSlider(HULLN, "n", null, 2, 1000, 20, 1);
        C.sIntSlider(HULLK, "k", null, 1, 1000, 50, 1);
        C.sIntSlider(HULLR, "radius", null, 5, 50, 40, 1);
        C.sClose();
      }
      C.sCloseTab();

      C.sCloseTabSet();
      C.sButton(GO, "Generate", null);
      C.sIntSpinner(SEED, "Seed:", "Specify random number seed", 0, 1000000, 0,
          10);
    }
    C.sCloseTab();
  }

  public void runAlgorithm() {
  }
  public void paintView() {
    Editor.render();
  }

  private static class MyLine {
    public MyLine(FPoint2 endPt1, FPoint2 endPt2) {
      this(endPt1.x, endPt1.y, endPt2.x, endPt2.y);
    }

    public MyLine() {
    }

    public MyLine(double x1, double y1, double x2, double y2) {
      set(x1, y1, x2, y2);
    }

    public void render() {
      renderEndpoints();
      //      V.drawLine(Main.grid.toWorld(endPoint(0)), Main.grid.toWorld(endPoint(1)));
    }

    public void renderEndpoints() {
      //      for (int i = 0; i < 2; i++) {
      //        FPoint2 gpt = Main.grid.toWorld(endPoint(i));
      //        V.drawCircle(gpt, V.scale * 6);
      //      }

    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("Line ");
      sb.append(endPoint(0));
      sb.append(" .. ");
      sb.append(endPoint(1));
      return sb.toString();
    }

    public FPoint2 endPoint(int index) {
      if (epts == null) {
        epts = new FPoint2[2];
      }
      FPoint2 pt = epts[index];
      if (pt == null) {
        pt = new FPoint2(index == 0 ? x1 : x2, index == 0 ? y1 : y2);
        epts[index] = pt;
      }
      return pt;
    }

    public FPoint2 midPoint() {
      return new FPoint2((x1 + x2) * .5, (y1 + y2) * .5);
    }

    public double dx() {
      return x2 - x1;
    }

    public double dy() {
      return y2 - y1;
    }

    public static int approxPasses;

    private static double minDistance;

    /**
     * Construct an approximation to a line, one that has integer endpoints
     * 
     * @param orig
     * @param tolerance
     * @return
     */
    private static MyLine constructApprox0(MyLine orig, double tolerance) {
      // calculate midpoint of original line to use as reference
      FPoint2 midPoint = orig.midPoint();
      double dx = orig.dx();
      double dy = orig.dy();

      double seekParam1 = .7, seekParam2 = .6;
      if (orig.length() == 0) {
        dx = 6.0;
      } else if (orig.length() < 5.0) {
        double s = 5.0 / orig.length();
        dx *= s;
        dy *= s;
      }

      double distance = 0;
      MyLine ln = new MyLine();
      MyLine lnFound = null;
      minDistance = -1;

      int pass = 0;
      outer: for (;; pass++) {

        double x2 = Math.round(midPoint.x - (dx * seekParam1));
        double y2 = Math.round(midPoint.y - (dy * seekParam1));

        double x3 = Math.round(midPoint.x + (dx * seekParam2));
        double y3 = Math.round(midPoint.y + (dy * seekParam2));

        seekParam1 *= 1.1;
        seekParam2 *= 1.11;

        for (int i = -1; i <= 1; i++) {
          for (int j = -1; j <= 1; j++) {

            for (int k = -1; k <= 1; k++) {
              for (int l = -1; l <= 1; l++) {
                ln.set(x2 + i, y2 + j, x3 + k, y3 + l);

                double d1 = ln.distanceFrom(orig.endPoint(0));
                double d2 = ln.distanceFrom(orig.endPoint(1));
                distance = Math.max(d1, d2);
                if (minDistance < 0 || distance < minDistance)
                  minDistance = distance;

                if (distance < tolerance) {
                  lnFound = ln;
                  break outer;
                }
              }
            }
          }
        }
      }
      approxPasses = pass;

      return lnFound;
    }

    /**
     * Construct an approximation to a line, one that has integer endpoints
     * 
     * @param orig
     * @param tolerance
     * @return
     */
    public static MyLine constructApprox(MyLine orig, double tolerance) {
      MyLine ln = constructApprox0(orig, tolerance);
      // if (ln == null)
      // constructApprox0(orig,tolerance,true);
      if (ln == null)
        throw new ArithmeticException("Failed to construct approximation to "
            + orig + "\n minimum distance= " + minDistance);
      return ln;
    }

    public void set(double x1, double y1, double x2, double y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      eqn = null;
      epts = null;
      length = -1;
    }

    public double[] eqn() {
      if (eqn == null) {
        eqn = new double[3];
        eqn[0] = y2 - y1;
        eqn[1] = -(x2 - x1);
        eqn[2] = (x2 - x1) * y1 - (y2 - y1) * x1;
      }
      return eqn;
    }

    public IPoint2 intEndPoint(int index) {
      return new IPoint2(endPoint(index));
    }

    public double distanceFrom(FPoint2 a) {
      return MyMath.ptDistanceToLine(a, endPoint(0), endPoint(1), null);
    }

    public double length() {
      if (length < 0)
        length = FPoint2.distance(x1, y1, x2, y2);
      return length;
    }

    public double x1, y1, x2, y2;

    private FPoint2[] epts;

    private double[] eqn;

    private double length;
  }

}
