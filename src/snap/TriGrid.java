package snap;

import base.*;
import testbed.*;

public class TriGrid extends Grid {
  private static final double YOF = 1.0 / 6.0;

  private static final double ROOT3 = Math.sqrt(3);

  private static final boolean db = false;

  private static double[] initMatrixScript = { //
  3, 3,//
      1, 0, 0, //
      0, ROOT3, 0, //
      0, 0, 1 //
  };
  public int height() {
    return sizeInCells.y;
  }

  public void setSize(int size, FPoint2 logSize) {
//    this.debLS = new FPoint2(logSize);
    double scaleFactor = 88.0 / size;
    final boolean db = false;
    sizeInCells = new IPoint2(Math.round(size * logSize.x / 100), Math
        .round(size / ROOT3 * logSize.y / 100));

    scale = scaleFactor;

    toView = new Matrix(initMatrixScript);
    toView = toView.scale( scale, scale, null);

    // translate to center grid within 100x100 window;
    toView.set(0, 2, (scale + logSize.x - scale * sizeInCells.x) * .5);
    toView.set(1, 2, (scale + logSize.y - scale * sizeInCells.y * ROOT3) * .5);

    u = toView.get(0, 0);
    v1 = toView.get(1, 1) * 2.0 / 3.0;
    v2 = v1 / 2;

    toGrid = toView.invert(null);

    if (db) {
      Streams.out.println("toView=\n" + toView + "toGrid=\n" + toGrid);
      for (int i = 0; i < 8; i++) {
        IPoint2 iorig = new IPoint2(i - 4, i - 4);
        FPoint2 view = toView(iorig, null);
        FPoint2 vorig = new FPoint2(i * .2 - 1, i * .2 - 1);
        IPoint2 grid = toGrid(vorig.x, vorig.y, null);
        Streams.out.println("int " + iorig + " => " + view);
        Streams.out.println("view" + vorig + " => " + grid);
      }
    }
  }

  public int width() {
   return sizeInCells.x;
  }

//  /**
//   * Constructor
//   * 
//   * @param size : width in cells
//   */
//  public TriGrid(int size, FPoint2 logSize) {
//    setSize(size,logSize);
//  }
  

//  /**
//   * Get size of grid, in terms of the number of cells visible horizontally
//   * and vertically.
//   * @return IPoint2
//   */
//  public IPoint2 size() {
//    return sizeInCells;
//  }

  private static boolean even(double i) {
    return even((int) (Math.round(i)));
  }

  private static boolean even(int i) {
    return (i & 1) == 0;
  }

  /**
   * Render grid
   * @param V : vp
   * @param withLabels : true to label cells
   */
  public void render(  boolean withLabels) {

    if (db)
      Streams.out.println("Grid.render, toView=\n" + toView);

     double tscl = scale * .24;
    if (tscl < .3) return;
    

    if (tscl < .5)
      withLabels = false;
    V.pushColor(MyColor.get(MyColor.BLUE, 1.13));
    V.pushStroke(Globals.STRK_THIN);

   for (int y = 0; y < sizeInCells.y; y++) {

      for (int x = 0; x < sizeInCells.x; x++) {

        FPoint2 vc = toView(x, y, null);
        if (even(x + y)) {
          FPoint2 p0 = new FPoint2(vc.x - u, vc.y - v2), p1 = new FPoint2(vc.x
              + u, vc.y - v2), p2 = new FPoint2(vc.x, vc.y + v1);

          V.drawLine(p0, p1);
          V.drawLine(p1, p2);
          V.drawLine(p2, p0);
        } else {
          FPoint2 p0 = new FPoint2(vc.x - u, vc.y + v2), p1 = new FPoint2(vc.x
              + u, vc.y + v2), p2 = new FPoint2(vc.x, vc.y - v1);

          if (x == 0)
            V.drawLine(p0, p2);
          if (x == sizeInCells.x - 1)
            V.drawLine(p1, p2);
          if (y == sizeInCells.y - 1)
            V.drawLine(p0, p1);
        }
      }
    }

    if (withLabels) {
      for (int y = 0; y < sizeInCells.y; y++) {

        if (tscl < .9 && (y & 1) != 0)
          continue;

        V.pushScale((y % 2 == 0) ? .7 : .7);
        V.draw(Integer.toString(y), toView(-1.0, y, null));
        V.draw(Integer.toString(y), toView(sizeInCells.x - .1, y, null));
        V.popScale();

      }

      double fy = toView(.5, -.5, null).y;
      double fy2 = toView((sizeInCells.y & 1), sizeInCells.y, null).y;

      for (int x = 0; x < sizeInCells.x; x++) {
        if (tscl < .9 && (x & 1) != 0)
          continue;
        V.pushScale((x % 2 == 0) ? .8 : .6);
        FPoint2 tv = toView(x, -.5, null);
        FPoint2 tv2 = toView(x, sizeInCells.y, null);
        V.draw(Integer.toString(x), tv.x, fy);
        V.draw(Integer.toString(x), tv2.x, fy2);
        V.popScale();
      }
    }

    V.popStroke();
    V.popColor();

  }

  private static boolean even(IPoint2 pt) {
    return even(pt.x + pt.y);
  }

  /**
   * Highlight a cell
   * @param cell : location of cell
   */
  public void highlightCell(IPoint2 cell) {
    FPoint2 vc = toView(cell);
    FPoint2 p0, p1, p2;
    double su = u * .8, sv2 = v2 * .8, sv1 = v1 * .8;
    if (even(cell)) {
      p0 = new FPoint2(vc.x - su, vc.y - sv2);
      p1 = new FPoint2(vc.x + su, vc.y - sv2);
      p2 = new FPoint2(vc.x, vc.y + sv1);
    } else {
      p0 = new FPoint2(vc.x - su, vc.y + sv2);
      p1 = new FPoint2(vc.x + su, vc.y + sv2);
      p2 = new FPoint2(vc.x, vc.y - sv1);
    }
//    vp vp = Main.view();
    V.drawLine(p0, p1);
    V.drawLine(p1, p2);
    V.drawLine(p2, p0);
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

    FPoint2 gp = toGrid.apply( viewX, viewY , null);

    if (db)
      Streams.out.println("toGrid, gridPt=" + gp + ", even="
          + even(gp.x + gp.y));
    if (even(gp.x + gp.y))
      gp.y += YOF;
    else
      gp.y -= YOF;

    gridPt.setLocation((int) Math.round(gp.x), (int) Math.round(gp.y));

    if (db)
      Streams.out.println("toGrid " + new FPoint2(viewX, viewY) + " => "
          + gridPt);

    return gridPt;
  }

  /**
   * Transform grid point to view space
   * @param x,y : grid point
   * @param dest : where to store view space point; if null, constructs it
   * @return dest
   */
  public FPoint2 toView(double x, double y, FPoint2 dest) {

    if (even(x + y))
      y -= YOF;
    else
      y += YOF;
    dest = toView.apply( x, y , dest);
    if (db)
      Streams.out.println("toView " + new FPoint2(x, y) + " => " + dest);
    return dest;
  }

  public static IPoint2 snapInGridSpace(FPoint2 gs) {
    // we are now in grid space, unadjusted from center of pixel.
    // determine closest upright triangle whose center is not to the right or above
    // us.

    double tx = Math.floor(gs.x);
    double ty = Math.floor(gs.y);

    if (!even(tx + ty)) {
      tx--;
    }
    if (gs.y - ty >= .5) {
      ty++;
      // increment or decrement tx, so difference remains from [0..2)
      if (gs.x - tx >= 1.0)
        tx++;
      else
        tx--;
    }

    if (gs.x - tx < 1) {
      // we are either in tx,ty or, if we're to the right of
      // the line from tx+1,ty-.5 to tx,ty+.5, we're in tx+1,ty
      if (MyMath.sideOfLine(tx, ty + .5, tx + 1, ty - .5, gs.x, gs.y) < 0) {
      } else {
        tx++;
      }
    } else {
      // we are either in tx+1,ty or, if we're to the right of the
      // line from tx+1,ty-.5 to tx+2,ty+.5, we're in tx+2,ty
      if (MyMath.sideOfLine(tx + 1, ty - .5, tx + 2, ty + .5, gs.x, gs.y) < 0) {
        tx += 2;
      } else {
        tx++;
      }
    }
    return new IPoint2(tx, ty);

  }

  
  public FPoint2 snap(FPoint2 wpoint) {

    FPoint2 gs = toGrid.apply( wpoint, null);
FPoint2 ret = toView(snapInGridSpace(gs),null);
//    // we are now in grid space, unadjusted from center of pixel.
//    // determine closest upright triangle whose center is not to the right or above
//    // us.
//
//    double tx = Math.floor(gs.x);
//    double ty = Math.floor(gs.y);
//    if (db) {
//      Streams.out.println("snap " + wpoint + " gs=" + gs + " t="
//          + new FPoint2(tx, ty));
//    }
//
//    if (!even(tx + ty)) {
//      tx--;
//    }
//    if (gs.y - ty >= .5) {
//      ty++;
//      // increment or decrement tx, so difference remains from [0..2)
//      if (gs.x - tx >= 1.0)
//        tx++;
//      else
//        tx--;
//    }
//
//    if (gs.x - tx < 1) {
//      // we are either in tx,ty or, if we're to the right of
//      // the line from tx+1,ty-.5 to tx,ty+.5, we're in tx+1,ty
//      if (MyMath.sideOfLine(tx, ty + .5, tx + 1, ty - .5, gs.x, gs.y) < 0) {
//      } else {
//        tx++;
//      }
//    } else {
//      // we are either in tx+1,ty or, if we're to the right of the
//      // line from tx+1,ty-.5 to tx+2,ty+.5, we're in tx+2,ty
//      if (MyMath.sideOfLine(tx + 1, ty - .5, tx + 2, ty + .5, gs.x, gs.y) < 0) {
//        tx += 2;
//      } else {
//        tx++;
//      }
//    }
//
//    FPoint2 ret = toView(tx, ty, null);
//    if (db) {
//      Streams.out.println(" snap, grid=" + new FPoint2(tx, ty) + " => " + ret);
//
//      Streams.out.println(" toGrid=" + toGrid(ret) + "\n toView="
//          + toView(toGrid(ret)));
//    }
//
    return ret;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString() + "\n");
//    sb.append(" logSize=" + debLS + "\n");
//    sb.append(" toView=\n" + toView);
//    sb.append(" toGrid=\n" + toGrid);
    return sb.toString();
  }

  // transform matrix grid->view
  private Matrix toView;

  private IPoint2 sizeInCells;

  // width of cell in view space 
  private double scale;

  // precalculated constants for rendering
  private double u, v1, v2;

  // transform matrix, view->grid
  private Matrix toGrid;


}
