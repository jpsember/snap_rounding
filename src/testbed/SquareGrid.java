package testbed;

import java.awt.*;
import base.*;

public class SquareGrid extends Grid {

  public int height() {
    return sizeInCells.y;
  }

  public void setSize(int gridSize, FPoint2 logSize) {

    cellSize = gridSize;

    double lx = logSize.x;
    double ly = logSize.y;

    sizeInCells = new IPoint2( //
        Math.floor(lx / cellSize), //
        Math.floor(ly / cellSize));

    toView = new Matrix(3, 3);
    toView.setIdentity();

    toView.set(0, 0, cellSize);
    toView.set(1, 1, cellSize);

    toGrid = toView.invert(null);

  }

  public int width() {
    return sizeInCells.x;
  }

  public void render(boolean withLabels) {
    int stepY = 1;
    int lblMod = 1;
    while (sizeInCells.y > 40 * (stepY * lblMod))
      lblMod *= 2;

    V.pushColor(Color.gray); //MyColor.cLIGHTGRAY); //MyColor.get(MyColor.BLUE, 1.13));

    V.pushStroke(Globals.STRK_THIN);

    int tf = Globals.TX_CLAMP | Globals.TX_BGND;

    double inset = V.getScale() * 1.0;
    for (int y = 0; y <= sizeInCells.y; y += stepY) {
      FPoint2 p0 = toView(0, y, null);
      FPoint2 p1 = toView(sizeInCells.x, y, null);

      boolean full = (y % lblMod == 0);
      if (full) 
      V.drawLine(p0.x, p0.y, p1.x, p1.y);

      if (withLabels && y > 0 && y < sizeInCells.y && full) {
        V.pushScale(.8);
        String s = Integer.toString((int) Math.round(y * cellSize));
        V.draw(s, p0.x - inset, p0.y, tf);
        V.draw(s, p1.x + inset, p0.y, tf);
        V.popScale();
      }

    }
    for (int x = 0; x <= sizeInCells.x; x += stepY) {
      FPoint2 p0 = toView(x, 0, null);
      FPoint2 p1 = toView(x, sizeInCells.y, null);
      boolean full = (x % lblMod == 0);
     if (full)
       V.drawLine(p0.x, p0.y, p1.x, p1.y);
      if (withLabels && full) {
        V.pushScale(.8);
        String s = Integer.toString((int) Math.round(x * cellSize));
        V.draw(s, p0.x, p0.y - inset, tf);
        V.draw(s, p1.x, p1.y + inset, tf);
        V.popScale();
      }
    }
    V.popStroke();
    V.popColor();
  }

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

  public double cellSize() {
    return cellSize;
  }

  public IPoint2 toGrid(double viewX, double viewY, IPoint2 gridPt) {
    if (gridPt == null)
      gridPt = new IPoint2();

    double[] c = toGrid.coeff();

    double tx = c[0] * viewX + c[1] * viewY + c[2];
    double ty = c[3] * viewX + c[4] * viewY + c[5];

    // casting double->int takes floor of value.  We want to round it.

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
