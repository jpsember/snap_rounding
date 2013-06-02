package testbed;

import base.*;

public abstract class Grid {

  /**
   * Set size of grid
   * @param gridSize size parameter passed from TestBed control panel
   * @param viewSize size of view
   */
  public abstract void setSize(int gridSize, FPoint2 viewSize);

  /**
   * Round view space point to grid space
   * @param viewX
   * @param viewY
   * @param gridPt : where to store grid space point; if null, constructs it
   * @return gridPt
   */
  public abstract IPoint2 toGrid(double viewX, double viewY, IPoint2 gridPt);

  /**
   * Transform grid point to view space
   * @param x : x-coordinate of grid point
   * @param y : y-coordinate of grid point
   * @param dest : where to store view space point; if null, constructs it
   * @return dest
   */
  public abstract FPoint2 toView(double x, double y, FPoint2 dest);

  /**
  * Render grid
  * @param withLabels : true to label cells
  */
  public abstract void render(boolean withLabels);

  /**
   * Get size of cell in pixels.
   * Used to make rendering decisions.
   * @return approximate size of cell, in pixels
   */
  public abstract double cellSize();

  public void highlightCell(int x, int y) {
    highlightCell(new IPoint2(x, y));
  }

  /**
   * Highlight a cell
   * @param cell : location of cell
   */
  public abstract void highlightCell(IPoint2 cell);

  /**
   * Get width of grid 
   * @return # cells wide
   */
  public abstract int width();
  /**
   * Get height of grid 
   * @return # cells tall
   */
  public abstract int height();

  /**
   * Snap a point to a cell center
   * @param point : point in view space
   * @return point snapped to cell center, in view space
   */
  public FPoint2 snap(FPoint2 point) {
    IPoint2 grid = toGrid(point, null);
    FPoint2 view = toView(grid, null);
    return view;
  }

  /**
   * Round view space point to grid space
   * @param view : view space point
   * @param gridPt : where to store grid space point; if null, constructs it
   * @return gridPt
   */
  public IPoint2 toGrid(IVector view, IPoint2 gridPt) {
    return toGrid(view.x(), view.y(), gridPt);
  }

  public IPoint2 toGrid(FPoint2 view) {
    return toGrid(view, null);
  }

  public FPoint2 toView(FPoint2 gridPt) {
    return toView(gridPt.x, gridPt.y);
  }

  public FPoint2 toView(IPoint2 gridPt) {
    return toView(gridPt, null);
  }
  public FPoint2 toView(double x, double y) {
    return toView(x, y, null);
  }

  public FPoint2 toView(IPoint2 gridPt, FPoint2 dest) {
    return toView(gridPt.x, gridPt.y, dest);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" width=" + width() + " height=" + height());
    return sb.toString();
  }
}
