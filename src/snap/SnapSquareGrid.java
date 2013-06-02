package snap;

import testbed.*;
import base.*;

public class SnapSquareGrid extends Grid {

  public int height() {
    return sizeInCells.y;
  }

  public void setSize(int maxCells, FPoint2 logSize) {
    final boolean db = false;

    // leave some space around the border of the grid, for labels
    final double INSET = .88;
    double lx = logSize.x * INSET;
    double ly = logSize.y * INSET;

    cellSize = Math.max(lx, ly) / maxCells;

    sizeInCells = new IPoint2( //
        Math.round(lx / cellSize), //
        Math.round(ly / cellSize));

    toView = new Matrix(3, 3);
    toView.setIdentity();

    toView.set(0, 0, cellSize);
    toView.set(1, 1, cellSize);

    // translate to center of view;
    // add half cell so 0,0 is mapped to center of cell
    toView.set(0, 2, logSize.x * .5 - (cellSize * .5) * sizeInCells.x
        + cellSize * .5);
    toView.set(1, 2, logSize.y * .5 - (cellSize * .5) * sizeInCells.y
        + cellSize * .5);

    toGrid = toView.invert(null);

    if (db)
      Streams.out.println("SquareGrid, size=" + maxCells + ", scale="
          + cellSize + ", logSize=" + logSize);

    if (db && true) {
      int[] k = { 0, 0, 8, 2, 1, 7, };

      Streams.out.println("toView=\n" + toView + "toGrid=\n" + toGrid);
      for (int i = 0; i < k.length; i += 2) {
        IPoint2 iorig = new IPoint2(k[i], k[i + 1]);
        FPoint2 view = toView(iorig, null);
        Streams.out.println("int " + iorig + " => " + view);
      }
    }

  }

  public int width() {
    return sizeInCells.x;
  }

  public void render(boolean withLabels) {

    final boolean db = false;

//    if (db)
//      Streams.out.println("Grid.render, toView=\n" + toView);

    double scaleh = cellSize() * .5;

    int stepY = height() / Math.min(height(), 100);

    double tscl = cellSize()* .24;

    if (tscl < .1)
      return;

    if (tscl < .3)
      withLabels = false;

    V.pushColor(MyColor.get(MyColor.BLUE, 1.13));

    V.pushStroke(Globals.STRK_THIN);

    for (int y = 0; y <= height(); y += stepY) {
      FPoint2 p0 = toView(0, y, null);
      FPoint2 p1 = toView(width(), y, null);

      if (db)
        Streams.out.println(" draw line " + p0 + " to " + p1);

      V.drawLine(p0.x - scaleh, p0.y - scaleh, p1.x - scaleh, p1.y - scaleh);

      if (withLabels && y <height()) {
        V.pushScale(.7);
        String s = Integer.toString(y);

        V.draw(s, p0.x - 4 * tscl, p0.y);
        V.draw(s, p1.x - cellSize() + 4 * tscl, p0.y);
        V.popScale();
      }

    }
    int stepX = width() / Math.min(width(), 100);
    for (int x = 0; x <= width(); x += stepX) {
      FPoint2 p0 = toView(x, 0, null);
      FPoint2 p1 = toView(x, height(), null);
      V.drawLine(p0.x - scaleh, p0.y - scaleh, p1.x - scaleh, p1.y - scaleh);
      if (withLabels && x < width()) {
        V.pushScale(.7); //tscl);
        String s = Integer.toString(x);
        V.draw(s, p0.x, p0.y - cellSize() + .4 * tscl);
        V.draw(s, p1.x, p1.y - .4 * tscl);
        V.popScale();
      }
    }
    V.popStroke();
    V.popColor();
  }

  /**
   * Highlight a cell
   * @param cell : location of cell
   */
  public void highlightCell(IPoint2 cell) {
    FPoint2 v0 = toView(cell.x, cell.y, null);
    FPoint2 v1 = toView(cell.x + 1, cell.y + 1, null);
    double tx = cellSize * .5;
    v0.subtract(tx, tx);
    v1.subtract(tx, tx);
    FRect r = new FRect(v0, v1);
    r.inset(V.getScale() * .2);
    V.drawRect(r);
  }

  /**
   * Get size of cell in pixels.
   * Used to make rendering decisions.
   * @return approximate size of cell, in pixels
   */
  public double cellSize() {
    return cellSize;
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

    return gridPt;
  }

  public FPoint2 toView(double x, double y, FPoint2 dest) {
    if (dest == null)
      dest = new FPoint2();
    double[] c = toView.coeff();
    dest.x = c[0] * x + c[1] * y + c[2];
    dest.y = c[3] * x + c[4] * y + c[5];
    return dest;
  }

  // transform matrix grid->view
  private Matrix toView;

  // size of grid, in cells
  private IPoint2 sizeInCells;

  // width of cell in view space 
  private double cellSize;

  // transform matrix, view->grid
  private Matrix toGrid;
}
