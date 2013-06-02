package snap;

import java.util.*;
import testbed.*;
import base.*;

class SnapEdSegment extends EdSegment {

  /**
   * Set point, with optional snapping to grid
   * @param ptIndex index of point
   * @param point new location of point
   * @param useGrid if true, snaps to grid (if one is active)
   * @param action if not null, action that caused this edit
   */
  public void setPoint(int ptIndex, FPoint2 point, boolean useGrid,
      TBAction action) {

    Grid g = Main.grid();
    iPts[ptIndex] = g.toGrid(point);
    super.setPoint(ptIndex, g.toView(iPts[ptIndex]), false, action);
  }

  //  /**
  //   * Get location of a particular point
  //   * @param ptIndex  index of point
  //   * @return location, or null if that point doesn't exist
  //   */
  //  public FPoint2 getPoint(int ptIndex) {
  //    FPoint2 ret = null;
  //    if (ptIndex < 2 && iPts[ptIndex] != null) {
  //    Grid g = Main.grid();
  //    IPoint2 ip = iPts[ptIndex];
  //    ret = g.toView(ip);
  //    }
  ////    
  ////    FPoint2 ret = null;
  ////    if (ptIndex < pts.size())
  ////      ret = pts.getFPoint2(ptIndex);
  //    return ret;
  //  }

  private IPoint2[] iPts = new IPoint2[2];

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (int i = 0; i < 2; i++) {
      FPoint2 ep = getPoint(i);
      if (ep != null)
        IPoint2.write(sb, Main.grid().toGrid(ep, null));
      else
        sb.append(" ? ");
    }
    sb.append(')');
    return sb.toString();
  }

  /**
   * Construct list of segments from array of EdSegment objects
   */
  public static Segment[] getSegments(BlackBox blackBox, Iterator it,
      boolean includeVert) {
    DArray s = new DArray();
    int id = 1;
    while (it.hasNext()) {
      SnapEdSegment obj = (SnapEdSegment) it.next();
      //      if (!obj.complete())
      //        continue;
      //      if (obj instanceof SnapEdSegment) 
      {
        Segment seg = new Segment(obj);
        if (!includeVert && blackBox.isVertical(seg))
          continue;

        seg.setId(id++);
        s.add(seg);
      }
    }
    return (Segment[]) s.toArray(Segment.class);
  }

  //  public int doneInsert(int ptIndex) {
  //    int nextPtInd = ptIndex + 1;
  //    if (complete())
  //      nextPtInd = -1;
  //    else {
  //      setPoint(nextPtInd, getPoint(0));
  //    }
  //    return nextPtInd;
  //  }

  private SnapEdSegment() {
  }

  public SnapEdSegment(Segment s) {
    this(s.pt(0), s.pt(1));
  }

  public SnapEdSegment(IPoint2 p0, IPoint2 p1) {
    Grid g = Main.grid();

    setPoint(0, g.toView(p0));
    setPoint(1, g.toView(p1));
  }

  //  public boolean complete() {
  //    boolean compl = nPoints() == 2;
  //    if (db)
  //      Streams.out.println("complete=" + compl);
  //    return compl;
  //  }

  //  public void setPoint(int fieldNumber, FPoint2 pt) {
  //    boolean db = false;
  //
  //    FPoint2 spt = pt;
  //    if (true)
  //      Main.grid().snap(pt);
  //    if (db)
  //      Streams.out.println("setPoint #" + fieldNumber + " pt=" + pt
  //          + " snapped to: " + spt);
  //
  //    super.setPoint(fieldNumber, spt);
  //  }

  //  public double distFrom(FPoint2 pt) {
  //    return MyMath.ptDistanceToSegment(pt, getPoint(0), getPoint(1), null);
  //  }

  public EdObjectFactory getFactory() {
    return FACTORY;
  }

  //  public void plot(Color c, int stroke, int markType) {
  //    if (complete()) {
  //      super.plot(c, stroke, markType);
  //      //      
  //      //      V.pushColor(Color.BLUE);
  //      //      V.drawLine(getPoint(0), getPoint(1));
  //      if (Main.highDetail()) {
  //        V.pushColor(Color.BLUE);
  //        SnapUtils.render(getPoint(0));
  //        SnapUtils.render(getPoint(1));
  //        V.popColor();
  //      }
  //      //      V.popColor();
  //    }
  //  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {

    public EdObject construct() {
      return new SnapEdSegment();
    }

    public String getTag() {
      return "seg";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdSegment, parse, next=" + s.peek().debug());

      s.read(IEditorScript.T_PAROP);
      SnapEdSegment seg = new SnapEdSegment();
      seg.setFlags(flags);
      for (int i = 0; i < 2; i++) {
        IPoint2 pt = s.extractIPoint2();
        Grid g = Main.grid();
        if (db)
          Streams.out.println("EdSeg, reading " + pt + " for grid " + g);
        seg.setPoint(i, g.toView(pt, null));
      }
      s.read(IEditorScript.T_PARCL);
      return seg;
    }

    public void write(StringBuilder sb, EdObject obj) {
      Grid g = Main.grid();
      SnapEdSegment seg = (SnapEdSegment) obj;
      sb.append('(');
      for (int i = 0; i < 2; i++) {
        FPoint2 pt = seg.getPoint(i);
        sb.append(g.toGrid(pt));
      }
      sb.append(')');
      
      
//    System.out.println(" writing sb: "+sb);
            
    }

    public String getMenuLabel() {
      return "Add segment";
    }
    public String getKeyEquivalent() {
      return "s";
    }

  };

  /**
   * Reconvert grid-space coordinates to view space, which may have changed
   * @param grid new grid
   */
  public void useGrid(Grid grid) {
    for (int ep = 0; ep < nPoints(); ep++) {
      IPoint2 gp = iPts[ep];
      FPoint2 pt2 = grid.toView(gp);
      pt2 = grid.snap(pt2);
      setPoint(ep, pt2);
    }
  }

}
