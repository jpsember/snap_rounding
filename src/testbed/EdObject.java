package testbed;

import java.awt.*;
import base.*;

public abstract class EdObject implements Cloneable, Renderable {

  /**
   * Replace object's points with those of another object
   * @param src : source object
   */
  public void copyPointsFrom(EdObject src) {
    pts = new DArray();
    for (int i = 0; i < src.nPoints(); i++)
      pts.add(new FPoint2(src.getPoint(i)));
  }

  /**
   * Construct a string that uniquely describes this object
   * @return
   */
  public String getHash() {
    StringBuilder sb = new StringBuilder();
    sb.append(getFactory().getTag());
    sb.append(' ');
    for (int j = 0; j < nPoints(); j++)
      sb.append(getPoint(j));
    sb.append('\n');
    return sb.toString();
  }

  /**
   * Clone the object
   */
  public Object clone() {
    try {
      EdObject e = (EdObject) super.clone();

      e.copyPointsFrom(this);
      return e;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Toggle state of certain flags
   * @param flg flags to toggle
   */
  public void toggleFlags(int flg) {
    this.flags ^= flg;
  }

  public String toString() {
    String s = getLabel();
    if (s == null)
      s = super.toString();
    return s;
  }

  /**
   * Plot label for object, if one is defined
   * @param loc location of label
   */
  public void plotLabel(FPoint2 loc) {
    plotLabel(loc.x, loc.y);
  }

  /**
   * Plot label for object, if one is defined
   * @param x  
   * @param y location of label
   */
  public void plotLabel(double x, double y) {
    String s = getLabel();
    if (s != null) {
      Editor.plotLabel(s, x, y, false);
    }
  }

  /**
   * Plot label for object, if one is defined and 
   * the appropriate labels option is selected
   * @param vert true if vertex label vs object label
   * @param loc location of label
   */
  public void plotLabel(boolean vert, FPoint2 loc) {
    plotLabel(vert, loc.x, loc.y);
  }

  /**
   * Plot label for object, if one is defined and 
   * the 'show labels' option is selected
   * @param vert true if vertex label vs object label
   * @param x  
   * @param y location of label
   */
  public void plotLabel(boolean vert, double x, double y) {
    if (Editor.withLabels(vert))
      plotLabel(x, y);
    //    {
    //      String s = getLabel();
    //      if (s != null) {
    //        Editor.plotLabel( s, x, y, false);
    //      }
    //    }
  }

  /**
   * Determine if object is selected
   * @return true if so
   */
  public boolean isSelected() {
    return hasFlags(FLAG_SELECTED);
  }

  /**
   * Set object's selected state
   * @param f new state
   */
  public void setSelected(boolean f) {
    setFlags(FLAG_SELECTED, f);
  }

  /**
   * Get bounding rectangle of object.
   * Default implementation calculates minimum bounding rectangle of 
   * the object's points
   * @return FRect
   */
  public FRect getBounds() {
    FRect r = null;
    for (int i = 0;; i++) {
      FPoint2 pt = getPoint(i);
      if (pt == null)
        break;
      r = FRect.add(r, getPoint(i));
    }
    return r;
  }

  /**
   * Determine if object is in a complete state; i.e. if a polygon has at least
   * three vertices
   * @return true if so
   */
  public abstract boolean complete();

  /**
   * Delete a point, if it exists
   * @param ptIndex index of point to delete
   */
  public void deletePoint(int ptIndex) {
    if (pts.exists(ptIndex))
      pts.remove(ptIndex);
  }

  /**
   * Set point, snapping to grid if active
   * @param ptIndex index of point
   * @param point new location of point
   */
  public final void setPoint(int ptIndex, FPoint2 point) {
    setPoint(ptIndex, point, true, null);
  }

  /**
   * Set transformed location of point.  Default method calls
   * setPoint().  For discs, radius point should be calculated from others.
   * @param ptIndex
   * @param point
   */
  public void setTransformedPoint(int ptIndex, FPoint2 point) {
    setPoint(ptIndex, point);
  }

  /**
   * Set point, with optional snapping to grid
   * @param ptIndex index of point
   * @param point new location of point
   * @param useGrid if true, snaps to grid (if one is active)
   * @param action if not null, action that caused this edit
   */
  public void setPoint(int ptIndex, FPoint2 point, boolean useGrid,
      TBAction action) {
    if (!useGrid)
      point = new FPoint2(point);
    else
      point = V.snapToGrid(point);
    storePoint(ptIndex, point);
  }

  /**
   * Store a point, without copying it.
   * Grows set accordingly.  Performs no grid snapping.
   * @param ptIndex index of point
   * @param point location of point
   */
  private void storePoint(int ptIndex, FPoint2 point) {
    pts.growSet(ptIndex, point);
  }

  /**
   * Add a point at a particular location, shifting following points to make room
   * @param ptIndex location to insert point 
   * @param point
   */
  public void addPoint(int ptIndex, FPoint2 point) {
    pts.add(ptIndex, point);
  }

  /**
   * Add a point to the object; adds to end of current points
   * @param x
   * @param y : point to add
   */
  public void addPoint(double x, double y) {
    addPoint(x, y, true);
  }

  /**
   * Add a point to the object; adds to end of current points
   * @param x
   * @param y : point to add
   * @param useGrid : if true, snaps to grid if it is active
   */
  public void addPoint(double x, double y, boolean useGrid) {
    addPoint(new FPoint2(x, y), useGrid);
  }
  /**
   * Add a point to the object; adds to end of current points
   * @param pt : FPoint2 to add
   */
  public void addPoint(FPoint2 pt) {
    addPoint(pt, true);
  }

  /**
   * Add a point to the object; adds to end of current points
   * @param pt : FPoint2 to add
   * @param useGrid : if true, snaps to grid if it is active
   */
  public void addPoint(FPoint2 pt, boolean useGrid) {
    if (pt == null)
      throw new IllegalArgumentException();
    setPoint(nPoints(), pt, useGrid, null);
  }

  /**
   * Return points of object as an array
   * @return FPoint2[] array
   */
  public FPoint2[] getPoints() {
    return (FPoint2[]) pts.toArray(FPoint2.class);
  }

  /**
   * Get number of points of object
   * @return # points in object
   */
  public int nPoints() {
    return pts.size();
  }

  /**
   * Get location of a particular point
   * @param ptIndex  index of point
   * @return location, or null if that point doesn't exist
   */
  public FPoint2 getPoint(int ptIndex) {
    FPoint2 ret = null;
    if (ptIndex < pts.size())
      ret = pts.getFPoint2(ptIndex);
    return ret;
  }

  /**
   * Get location of a particular point, where index is taken modulo the 
   * number of points (useful for walking around a polygon's vertices, for 
   * instance)
   * @param ptIndex index of point; it is converted to modulo(nPoints())
   * @return location, or null if that point doesn't exist
   */
  public FPoint2 getPointMod(int ptIndex) {
    return getPoint(MyMath.mod(ptIndex, nPoints()));
  }

  /**
   * Determine Hausdorff distance of object from a point
   * @param pt
   * @return distance from point, or -1 if no points exist
   */
  public abstract double distFrom(FPoint2 pt);

  /**
   * Get factory responsible for making these objects
   * @return factory 
   */
  public abstract EdObjectFactory getFactory();

  /**
   * Determine distance of an object's point from a point
   * @param ptIndex  index of object's point
   * @param pt point to compare that point to
   * @return distance, or < 0 if no point ptIndex exists
   */
  public double distFrom(int ptIndex, FPoint2 pt) {
    double ret = -1;
    FPoint2 pt2 = getPoint(ptIndex);
    if (pt2 != null)
      ret = FPoint2.distance(pt, pt2);
    return ret;
  }

  /**
   * Add a large highlight to a point, if it exists
   * @param ptIndex point index
   */
  public void hlLarge(int ptIndex) {
    FPoint2 pt = getPoint(ptIndex);
    if (pt != null) {
      V.pushColor(Color.RED);
      V.drawRect(getDisplayBoundingRect(pt));
      V.popColor();
    }
  }

  /**
   * Add a small highlight to a point, if it exists
   * @param ptIndex point index
   */
  public void hlSmall(int ptIndex) {
    FPoint2 pt = getPoint(ptIndex);
    if (pt != null) {
      //      vp vp = TestBed.view();
      V.pushColor(Color.RED);
      V.drawRect(getDisplayBoundingRect(pt, V.getScale() * .4));
      V.popColor();
    }
  }

  /**
   * Construct a rectangle to display around a point, using current view scale
   * @param pt
   * @return
   */
  private static FRect getDisplayBoundingRect(FPoint2 pt) {
    double size = V.getScale();
    return getDisplayBoundingRect(pt, size);
  }

  /**
   * Construct a rectangle to display around a point, using arbitrary padding size
   * @param pt
   * @param padding : amount of padding to each side
   * @return
   */
  private static FRect getDisplayBoundingRect(FPoint2 pt, double padding) {
    return new FRect(pt.x - padding, pt.y - padding, padding * 2, padding * 2);
  }

  /**
   * Move entire object by a displacement
   * Default implementation just adjusts each point.
   * @param orig : a copy of the original object
   * @param delta : amount to move by
   */
  public void moveBy(EdObject orig, FPoint2 delta) {
    for (int i = 0;; i++) {
      FPoint2 pt = orig.getPoint(i);
      if (pt == null)
        break;
      setPoint(i, FPoint2.add(pt, delta, null));
    }
  }

  /**
   * Get next point to insert.
   * Either create a new point and return its index, or return -1 to indicate
   * this object is complete
   * @param a current TBAction (e.g., to examine modifier keys)
   * @param ptIndex : index of point being inserted
   * @param drift if not null, offset of current mouse loc from last event loc
   * @return index of point to continue editing with, 
   *    -1 if done,
   *    -2 to continue waiting
   */
  public int getNextPointToInsert(TBAction a, int ptIndex, FPoint2 drift) {
    int ret = -1;
    if (!complete())
      ret = nPoints();
    return ret;
  }

  /**
   * Snap object to grid.  Default implementation snaps every point
   * to the grid.
   * @param g grid
   */
  public void snapTo(Grid g) {
    for (int i = 0; i < nPoints(); i++)
      setPoint(i, g.snap(getPoint(i)));
  }

  /**
   * Clean up an object after editing is complete.
   * If it is damaged, leave in an incomplete state.
   * This is used to filter out duplicate vertices in polygons, for instance.
   */
  public void cleanUp() {
  }

  /**
   * Determine if object is active.  By default, objects are active.
   * User can flag objects as inactive, so they are excluded from some operations,
   * and/or appear different.
   * @return true if it's active
   */
  public boolean isActive() {
    return !hasFlags(FLAG_INACTIVE);
  }

  /**
   * Set object's inactive flag
   * A convenience, so that user may decide not to include it in 
   * certain operations if it is marked as such.
   * @param f true for inactive, false for active
   * @deprecated use setActive
   */
  public void setInactive(boolean f) {
    setFlags(FLAG_INACTIVE, f);
  }

  /**
   * Set object's active flag
   * @param f true for active, false for inactive
   */
  public void setActive(boolean f) {
    setFlags(FLAG_INACTIVE, !f);
  }

  /**
   * Replace existing flags with new ones
   * @param f new flags
   */
  public void setFlags(int f) {
    this.flags = f;
  }

  /**
   * Add or clear flags
   * @param flags flags to modify
   * @param value true to set, false to clear
   */
  public void setFlags(int flags, boolean value) {
    if (!value)
      clearFlags(flags);
    else
      addFlags(flags);
  }

  /**
   * Turn specific flags on
   * @param f flags to turn on
   */
  public void addFlags(int f) {
    setFlags(flags | f);
  }

  /**
   * Determine if a set of flags are set
   * @param f flags to test
   * @return true if every one of these flags is set
   */
  public boolean hasFlags(int f) {
    return (flags & f) == f;
  }

  /**
   * Turn specific flags off
   * @param f flags to turn off
   */
  public void clearFlags(int f) {
    setFlags(flags & ~f);
  }

  /**
   * Get current flags
   * @return flags
   */
  public int flags() {
    return flags;
  }

  /**
   * Get label that's been assigned to this object
   * @return label, or null if none exists
   */
  public final String getLabel() {
    return label;
  }

  /**
   * Label this object
   * @param lbl label to assign, or null
   */
  public final void setLabel(String lbl) {
    this.label = lbl;
  }

  /**
  * Render object within editor.
  * Override this to change highlighting behaviour for points.
  */
  public void render() {
    render(null, -1, -1);
    if (isSelected()) {
      for (int i = 0; i < nPoints(); i++)
        hlSmall(i);
    }
  }

  private static final int FLAG_SELECTED = (1 << 31);
  private static final int FLAG_INACTIVE = (1 << 30);
  public static final int FLAG_PLOTDASHED = (1 << 29);
  /**
   * Number of bits available for user flags.
   * For instance, any flag from 2^0 to 2^(USER_FLAG_BITS-1) are
   * available for user use.  The others are used for the object's
   * selected and active states.
   * Some of these may be used by other objects; for instance,
   * the EdPolygon uses one of these already.
   */
  public static final int USER_FLAG_BITS = 24;

  /**
   * Scale a point relative to the center of the view
   * @param pt point to scale
   * @param factor scaling factor
   */
  public static void scalePoint(FPoint2 pt, double factor) {
    FPoint2 ls = V.logicalSize();
    double mx = ls.x / 2, my = ls.y / 2;

    pt.setLocation((pt.x - mx) * factor + mx, (pt.y - my) * factor + my);
  }

  /**
   * Scale object.
   * Default implementation just scales all the object's points.
   * @param factor  scaling factor
   */
  public void scale(double factor) {
    for (int i = 0;; i++) {
      FPoint2 pt = getPoint(i);
      if (pt == null)
        break;
      scalePoint(pt, factor);
      setPoint(i, pt);
    }
  }

  //  /**
  //   * Translate object.
  //   * @param x
  //   * @param y translation amount
  //   */
  //  public void translate(double x, double y) {
  //    for (int i = 0;; i++) {
  //      FPoint2 pt = getPoint(i);
  //      if (pt == null)
  //        break;
  //      setPoint(i, new FPoint2(pt.x + x, pt.y + y));
  //    }
  //  }

  /**
   * Return the DArray used to store the points
   * @return DArray containing FPoint2's
   */
  public DArray getPts() {
    return pts;
  }

  private int flags;
  private DArray pts = new DArray();
  private String label;
}
