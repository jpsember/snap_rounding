package snap;

import testbed.*;
import base.*;

public class HexGrid extends Grid {

  public int height() {
   return sizeInCells.y;
  }

  public void setSize(int pixelsWide, FPoint2 logSize) {

    final boolean db = false;

    int size = pixelsWide;

    sizeInCells = new IPoint2(Math.round(size * logSize.x / 100), Math
        .round(size * Math.sqrt(3) * logSize.y / 100));

    scale = 88.0 / size; //scaleFactor;

    toView = new Matrix(3);
    toView.setIdentity();
    toView.set(1, 1, 1 / Math.sqrt(3));

    toView = toView.scale(  scale, scale, null);

    // translate to center grid within logical window;
    toView.set(0, 2, (scale + logSize.x - scale * sizeInCells.x) * .5);
    toView.set(1, 2,
        (scale + logSize.y - scale * sizeInCells.y / Math.sqrt(3)) * .5);

    u = toView.get(0, 0) * 2 / 3.0;
    v = toView.get(1, 1);

    toGrid = toView.invert(null);

    if (db) {

      int[] t = { 3, 11 };

      Streams.out.println("toView=\n" + toView + "toGrid=\n" + toGrid);
      for (int i = 0; i < t.length;) {
        IPoint2 iorig = new IPoint2(t[i++], t[i++]);
        FPoint2 view = toView(iorig, null);
        IPoint2 grid = toGrid(view.x, view.y, null);
        Streams.out.println("int " + iorig + " => " + view + " => " + grid);
      }
    }
  }

  public int width() {
    return sizeInCells.x;
  }

  public HexGrid(){}
  /**
   * Constructor
   * 
   * @param size : width in cells
   * @deprecated
   */
  public HexGrid(int size, FPoint2 logSize) {
    setSize(size, logSize);
//    
//    final boolean db = false;
//
//    sizeInCells = new IPoint2(Math.round(size * logSize.x / 100), Math
//        .round(size * Math.sqrt(3) * logSize.y / 100));
//
//    scale = 88.0 / size; //scaleFactor;
//
//    toView = new Matrix(3);
//    toView.setIdentity();
//    toView.set(1, 1, 1 / Math.sqrt(3));
//
//    toView = Matrix.scale(toView, scale, scale, null);
//
//    // translate to center grid within logical window;
//    toView.set(0, 2, (scale + logSize.x - scale * sizeInCells.x) * .5);
//    toView.set(1, 2,
//        (scale + logSize.y - scale * sizeInCells.y / Math.sqrt(3)) * .5);
//
//    u = toView.get(0, 0) * 2 / 3.0;
//    v = toView.get(1, 1);
//
//    toGrid = toView.invert(null);
//
//    if (db) {
//
//      int[] t = { 3, 11 };
//
//      Streams.out.println("toView=\n" + toView + "toGrid=\n" + toGrid);
//      for (int i = 0; i < t.length;) {
//        IPoint2 iorig = new IPoint2(t[i++], t[i++]);
//        FPoint2 view = toView(iorig, null);
//        IPoint2 grid = toGrid(view.x, view.y, null);
//        Streams.out.println("int " + iorig + " => " + view + " => " + grid);
//      }
//    }
  }

//  /**
//   * Get size of grid, in terms of the number of cells visible horizontally
//   * and vertically.
//   * @return IPoint2
//   */
//  public IPoint2 size() {
//    return sizeInCells;
//  }

  /**
   * Render grid
   * @param V : vp
   * @param withLabels : true to label cells
   */
  public void render(boolean withLabels) {

    final boolean db = false;

    if (db)
      Streams.out.println("Grid.render, toView=\n" + toView);

    double tscl = scale * .24;

    if (tscl < .3)
      return;

    if (tscl < .5)
      withLabels = false;

    V.pushColor(MyColor.get(MyColor.BLUE, 1.13));
    V.pushStroke(Globals.STRK_THIN);

    for (int y = 0; y < sizeInCells.y; y++) {
      for (int x = 0; x < sizeInCells.x; x++) {
        if ((x + y) % 2 != 0)
          continue;

        FPoint2 vc = toView(x, y, null);

        double x0 = vc.x - u * .5, y0 = vc.y + v;
        double x1 = vc.x - u, y1 = vc.y;
        double x2 = x0, y2 = vc.y - v;
        double x3 = x2 + u, y3 = y2;
        double x4 = vc.x + u, y4 = y1;
        double x5 = x3, y5 = y0;
        V.drawLine(x0, y0, x1, y1);
        V.drawLine(x1, y1, x2, y2);
        V.drawLine(x2, y2, x3, y3);

        if (y >= sizeInCells.y - 2)
          V.drawLine(x0, y0, x5, y5);

        if (x >= sizeInCells.x - 1 || y == 0)
          V.drawLine(x3, y3, x4, y4);

        if (x >= sizeInCells.x - 1 || y >= sizeInCells.y - 1)
          V.drawLine(x4, y4, x5, y5);

      }
    }

    if (withLabels) {
      for (int y = 0; y < sizeInCells.y; y++) {

        if (tscl < .9 && (y & 1) != 0)
          continue;

        V.pushScale((y % 2 == 0) ? .7 : .6);
        V.draw(Integer.toString(y), toView(-1.0, y, null));
        V.draw(Integer.toString(y), toView(sizeInCells.x - .1, y, null));
        V.popScale();

      }
      for (int x = 0; x < sizeInCells.x; x++) {
        if (tscl < .9 && (x & 1) != 0)
          continue;
        V.pushScale((x % 2 == 0) ? .8 : .6);
        V.draw(Integer.toString(x), toView(x, -1.4, null));
        V.draw(Integer.toString(x), toView(x, sizeInCells.y - .6, null));
        V.popScale();
      }
    }

    V.popStroke();
    V.popColor();

  }

  private static final double[] vertMultipliers = { -.5, 1, -1, 0, -.5, -1, .5,
      -1, 1, 0, .5, 1, };

  /**
   * Highlight a cell
   * @param cell : location of cell
   */
  public void highlightCell(IPoint2 cell) {

    FPoint2 vc = toView(cell, null);
    //    // vp vp = TestBed.view();
    double us = u * .8, vs = v * .8;

    FPoint2 prev = null;
    for (int i = 0; i <= 6; i++) {
      int j = i % 6;
      FPoint2 c = new FPoint2(vc.x + vertMultipliers[j * 2 + 0] * us, vc.y
          + vertMultipliers[j * 2 + 1] * vs);
      if (prev != null)
        V.drawLine(prev, c);
      prev = c;
    }
  }

  public static IPoint2 snapGridSpace(FPoint2 vp) {
    double cx = Math.floor(vp.x), cy = Math.floor(vp.y);
    int ix = (int) cx;
    int iy = (int) cy;
    if (((ix + iy) & 1) != 0) {
      cx -= 1.0;
      ix--;
    }

    FPoint2 v = new FPoint2();

    if (vp.y < cy + 1.0) {
      if (vp.y < -3 * (vp.x - cx - 2 / 3.0) + cy)
        v.setLocation(cx, cy);
      else if (vp.y > 3 * (vp.x - cx - 4 / 3.0) + cy)
        v.setLocation(cx + 1, cy + 1);
      else
        v.setLocation(cx + 2, cy);
    } else {
      if (vp.y > 3 * (vp.x - cx) + cy)
        v.setLocation(cx, cy + 2);
      else if (vp.y <= -3 * (vp.x - cx - 6 / 3.0) + cy)
        v.setLocation(cx + 1, cy + 1);
      else
        v.setLocation(cx + 2, cy + 2);
    }
    return new IPoint2(v.x, v.y);
  }

  public FPoint2 snap(FPoint2 pt) {

    final boolean db = false;
    FPoint2 vp = toGrid.apply(pt,null);

    double cx = Math.floor(vp.x), cy = Math.floor(vp.y);
    int ix = (int) cx;
    int iy = (int) cy;
    if (((ix + iy) & 1) != 0) {
      cx -= 1.0;
      ix--;
    }

    FPoint2 v = new FPoint2();

    if (vp.y < cy + 1.0) {
      if (vp.y < -3 * (vp.x - cx - 2 / 3.0) + cy)
        v.setLocation(cx, cy);
      else if (vp.y > 3 * (vp.x - cx - 4 / 3.0) + cy)
        v.setLocation(cx + 1, cy + 1);
      else
        v.setLocation(cx + 2, cy);
    } else {
      if (vp.y > 3 * (vp.x - cx) + cy)
        v.setLocation(cx, cy + 2);
      else if (vp.y <= -3 * (vp.x - cx - 6 / 3.0) + cy)
        v.setLocation(cx + 1, cy + 1);
      else
        v.setLocation(cx + 2, cy + 2);
    }
    if (db)
      Streams.out.println("snap vpoint=" + vp + " ix=" + ix + " iy=" + iy
          + " v=" + v);

//    v = 
      toView.apply(v,v);
    if (db)
      Streams.out.println(" to view=" + v + ", back to grid=" + toGrid(v));

    return v;
  }

  /**
   * Get size of cell in pixels.
   * Used to make rendering decisions.
   * @return approximate size of cell, in pixels
   */
  public double cellSize() {
    return scale;
  }

  /**
   * Round view space point to grid space
   * @param viewX
   * @param viewY
   * @param gridPt : where to store grid space point; if null, constructs it
   * @return gridPt
   */
  public IPoint2 toGrid(double viewX, double viewY, IPoint2 gridPt) {

    if (gridPt == null)
      gridPt = new IPoint2();

    double[] c = toGrid.coeff();

    // casting double->int takes floor of value.  We want to round it.

    double tx = c[0] * viewX + c[1] * viewY + c[2];
    double ty = c[3] * viewX + c[4] * viewY + c[5];

    gridPt.setLocation((int) Math.round(tx), (int) Math.round(ty));

    //    Streams.out.println("toGrid "+new FPoint2(viewX,viewY)+" tf="+new FPoint2(tx,ty)+" grd="+gridPt);
    //    if (Math.abs(tx - 7.8) < .1 && Math.abs(ty - 11) < .1) 
    //     Streams.out.println(Tools.st());
    return gridPt;
  }

  /**
   * Transform grid point to view space
   * @param x,y : grid point
   * @param dest : where to store view space point; if null, constructs it
   * @return dest
   */
  public FPoint2 toView(double x, double y, FPoint2 dest) {
    if (dest == null)
      dest = new FPoint2();
    double[] c = toView.coeff();
    dest.x = c[0] * x + c[1] * y + c[2];
    dest.y = c[3] * x + c[4] * y + c[5];
    //    Streams.out.println("toView "+new FPoint2(x,y)+" => "+dest);
    return dest;
  }

  // transform matrix grid->view
  private Matrix toView;

  private IPoint2 sizeInCells;

  // width of cell in view space 
  private double scale;

  // precalculated constants for rendering
  private double u, v;

  // transform matrix, view->grid
  private Matrix toGrid;

}
