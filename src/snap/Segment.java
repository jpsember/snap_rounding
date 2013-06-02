package snap;

import java.awt.*;
import base.*;
import snaptree.*;
import testbed.*;

public class Segment implements Globals, Item, Renderable {  
  public String encode() {
    throw new UnsupportedOperationException();
  }

  private static boolean VERIFYRANGE;

  public Handle getHandle() {
    if (handle == null) {
      handle = new Handle(this);
    }
    return handle;
  }

  public void setHandle(Handle h) {
    this.handle = h;
  }

  public String str() {
    StringBuffer sb = new StringBuffer();
    if (id != 0) {
      sb.append(Tools.f(id, 3));
      sb.append(": ");
    }
    Point pt = pt(0);
    sb.append('(');
    sb.append(pt.x);
    sb.append(',');
    sb.append(pt.y);
    sb.append(')');
    sb.append("-");
    pt = pt(1);
    sb.append('(');
    sb.append(pt.x);
    sb.append(',');
    sb.append(pt.y);
    sb.append(')');

    return sb.toString();
  }

  public Segment neighborAbove() {
    return neighbor(true);
  }

  public Segment neighborBelow() {
    return neighbor(false);
  }

  /**
   * Find neighbor to a particular side
   * 
   * @param side :
   *          0 for left, 1 for right
   * @return Segment, null if none
   */
  public Segment neighbor(boolean toRight) {
    Segment nbr = null;
    if (segTree != null && segPageId != 0)
      nbr = segTree.neighbor(this, segPageId, toRight);
    return nbr;
  }

  public void setPage(int id) {
    this.segPageId = id;
  }

  /**
   * Exchange the positions of two segments within the tree. Uses their handles
   * to modify the tree in O(1) time.
   * 
   * @param a
   *          Segment
   * @param b
   *          Segment
   */
  public static void exchangeTreePositions(Segment a, Segment b) {

    final boolean db = false;

    if (db && T.update()) {
      T.msg("exchTreePos\n" + deb(a) + "\n" + deb(b));
    }
    Handle ha = a.getHandle(), hb = b.getHandle();
    a.setHandle(hb);
    b.setHandle(ha);

    ha.setItem(b);
    hb.setItem(a);

    int pa = a.segPageId, pb = b.segPageId;
    a.segPageId = pb;
    b.segPageId = pa;

    if (db && T.update()) {
      T.msg(" after exchTreePos\n" + deb(a) + "\n" + deb(b));
    }
  }

  private static String deb(Segment a) {
    StringBuffer sb = new StringBuffer();
    sb.append(a.name());
    sb.append("->" + ((Segment) a.getHandle().getItem()).name());
    if (a.segPageId != 0) {
      sb.append(" page=" + a.segPageId);
    }

    return sb.toString();
  }

  /**
   * @param x0
   * @param y0
   * @param x1
   * @param y1
   */
  private void set(Scalar x0, Scalar y0, Scalar x1, Scalar y1) {
    // swap if necessary, so angle is always -PI/2 < a <= PI/2
    boolean swap = x0.intValue() > x1.intValue();
    if (x0.intValue() == x1.intValue()) {
      swap = y0.intValue() > y1.intValue();
    }

    if (!swap) {
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
    } else {
      this.x0 = x1;
      this.y0 = y1;
      this.x1 = x0;
      this.y1 = y0;
    }
    //    epts = new IPoint2[2];
    //    epts[0] = new IPoint2(x0(), y0());
    //    epts[1] = new IPoint2(x1(), y1());
  }

  public Segment(SnapEdSegment s) {
    Grid grid = Main.grid();
    IPoint2 p0 = grid.toGrid(s.getPoint(0), null);
    IPoint2 p1 = grid.toGrid(s.getPoint(1), null);
    //    Streams.out.println("constructed seg pts "+p0+", "+p1+" from EdSeg\n"+s.getPoint(0)+","+s.getPoint(1));
    set(new Int(p0.x), new Int(p0.y), new Int(p1.x), new Int(p1.y));
  }

  /**
   * Copy constructor
   * @param orig
   */
  public Segment(Segment orig) {
    set(orig.x0, orig.y0, orig.x1, orig.y1);
    setId(orig.id);
  }

  public Segment(int x0, int y0, int x1, int y1) {
    set(new Int(x0), new Int(y0), new Int(x1), new Int(y1));
  }

  public Segment(IPoint2 pt0, IPoint2 pt1) {
    this(pt0.x, pt0.y, pt1.x, pt1.y);
  }

  //  private Segment() {
  //  }

//  private static final String tag = Segment.class.getSimpleName();

  //  /**
  //   * @param tk
  //   * @param readTag
  //   * @return
  //   */
  //  public static Segment extract(Tokenizer tk) {
  //    T.openTag(tk, tag);
  //    Segment s = extract0(tk);
  //    T.closeTag(tk);
  //
  //    return s;
  //  }

  //  private static Segment extract0(Tokenizer tk) {
  //    IPoint2 p0 = IPointUtils.extract(tk), p1 = IPointUtils.extract(tk);
  //    Segment s = new Segment(p0.x, p0.y, p1.x, p1.y);
  //    return s;
  //  }
  //
  //  public Traceable parse(Tokenizer tk) {
  //    IPoint2 p0 = IPointUtils.extract(tk, false), p1 = IPointUtils.extract(tk,
  //        false);
  //
  //    Segment s = new Segment(p0.x, p0.y, p1.x, p1.y);
  //    return s;
  //  }
  //
//  public void plotTrace() {
//    ViewPanel V = TestBed.view();
//    V.pushColor(Color.red);
//    render(V);
//    V.popColor();
//  }
  public void render(Color c, int stroke, int markType) {
    if (c == null)
      c = Color.RED;
    V.pushColor(c);
//    render( );
    
    boolean withLabels =  
    Main.labelSegments();
//    Tools.warn("withLabels always false.");
    
    boolean db = false;
    if (db)
      Streams.out.println("Segment.render " + pt(0) + "..." + pt(1));

    IPoint2 pt0 = pt(0);
    IPoint2 pt1 = pt(1);

    {
      Grid g = Main.grid();

      FPoint2 w0 = g.toView(pt0, null), w1 = g.toView(pt1, null);

      V.drawLine(w0, w1);
      if (Main.highDetail()) { //g.cellSize() > 3) {
        SnapUtils.render(w0);
        if (x1.intValue() != x0.intValue() || y1.intValue() != y0.intValue())
          SnapUtils.render(w1);
      }

      if (withLabels) {
        if (id() != 0) {
          V.pushColor(Color.blue);
          V.pushScale(.7);
          double offset = V.getScale() * 1.6;
          String name = name();
          if (db)
            Streams.out.println("  drawing " + name + " at " + w0);

         V.draw(name, w0.x - offset, w0.y, TX_BGND | TX_FRAME);
          if (!pt0.equals(pt1))
            V.draw(name, w1.x + offset, w1.y, TX_BGND | TX_FRAME);
          V.popScale();
          V.popColor();
        }
      }
    }

    
    V.popColor();
  }

  //  static {
  //    T.registerTag(tag, new Segment());
  //  }
  //
//  /**
//   * @deprecated : use plot()
//   */
//  private void render( ) {
//    render(  false);
//  }

  /**
   * @deprecated : use plot()
   */
  public void render(  boolean withLabels) {

    boolean db = false;
    if (db)
      Streams.out.println("Segment.render " + pt(0) + "..." + pt(1));

    IPoint2 pt0 = pt(0);
    IPoint2 pt1 = pt(1);

    {
      Grid g = Main.grid();

      FPoint2 w0 = g.toView(pt0, null), w1 = g.toView(pt1, null);

      V.drawLine(w0, w1);
      if (Main.highDetail()) { //g.cellSize() > 3) {
        SnapUtils.render(w0);
        if (x1.intValue() != x0.intValue() || y1.intValue() != y0.intValue())
          SnapUtils.render(w1);
      }

      if (withLabels) {
        if (id() != 0) {
          V.pushColor(Color.blue);
          V.pushScale(.7);
          double offset = V.getScale() * 1.6;
          String name = name();
          if (db)
            Streams.out.println("  drawing " + name + " at " + w0);

         V.draw(name, w0.x - offset, w0.y, TX_BGND | TX_FRAME);
          if (!pt0.equals(pt1))
            V.draw(name, w1.x + offset, w1.y, TX_BGND | TX_FRAME);
          V.popScale();
          V.popColor();
        }
      }
    }
  }

  public String name() {
    StringBuffer sb = new StringBuffer();

    int side = id % 100;

    if (id < 100 || side >= 2) {
      sb.append(id);
    } else {
      sb.append(id / 100);
      sb.append(side == 0 ? 'L' : 'H');
    }
    return sb.toString();
  }

  //  public void encode(StringBuilder sb) {
  //    T.openTag(sb, tag);
  //    IPointUtils.encode(sb, pt(0));
  //    IPointUtils.encode(sb, pt(1));
  //    T.closeTag(sb);
  //  }

  /**
   * @param verbose
   * @return
   */
  public String toString(boolean verbose) {
    StringBuilder sb = new StringBuilder();
    sb.append(name());
    if (verbose) {
      if (T.active()) {
        T.show(this);
        //        encode(sb);
      } else {
        sb.append(pt(0));
        sb.append('-');
        sb.append(pt(1));
      }
    }
    return sb.toString();
  }

  /**
   * Get string describing object
   * 
   * @return String
   */
  public String toString() {
    return toString(true);
  }

  /**
   * Determine if the bounding box for this segment contains a point.
   * 
   * @param pt
   *          Point
   * @return boolean
   */
  public boolean boundsContains(Point pt) {
    return boundsContains(pt.x, pt.y);
  }

  /**
   * Determine if the bounding box for this segment contains a point.
   */
  public boolean boundsContains(int x, int y) {
    boolean c = false;
    do {
      int ex1 = x1.intValue();

      if (x < x0.intValue() || x > ex1) {
        break;
      }
      if (y < Math.min(y0.intValue(), y1.intValue())) {
        break;
      }
      if (y > Math.max(y0.intValue(), y1.intValue())) {
        break;
      }
      c = true;
    } while (false);
    return c;
  }

  public IPoint2 pt(int ind) {
    //    if (false) {
    //      return epts[ind];
    //    } else
    {
      return (ind == 0) ? new IPoint2(x0.intValue(), y0.intValue())
          : new IPoint2(x1.intValue(), y1.intValue());
    }
  }

  public void setId(int id) {
    this.id = id;
  }

  public int id() {
    return id;
  }

  /**
   * Perform lazy evaluation on segment's HPRange for a particular strip
   * 
   * @param sweepLine 
   * @param construct : if true, and no HPRange exists for this column,
   *  constructs one
   * @return SnapInfo that was found or constructed, or null
   */
  private SegHPRangeInStrip lazyEvalForStrip(BlackBox blackBox, int stripX,
      boolean construct) {

    int currX = stripX;

    // if no queue exists yet, create it
    if (lzSRQueue == null)
      lzSRQueue = new DArray();

    // pop elements that are off the left edge of the pixel column
    while (!lzSRQueue.isEmpty()) {
      SegHPRangeInStrip si = (SegHPRangeInStrip) lzSRQueue.get(0);
      if (si.stripX() > currX - blackBox.maxStripsPerPixel())
        break;
      lzSRQueue.remove(0);
    }

    // find current element, or insertion point
    SegHPRangeInStrip si = null;
    int insertPos = lzSRQueue.size();
    while (insertPos > 0) {
      SegHPRangeInStrip s2 = (SegHPRangeInStrip) lzSRQueue.get(insertPos - 1);
      if (s2.stripX() == currX) {
        si = s2;
        break;
      }
      if (s2.stripX() < currX)
        break;
      insertPos--;
    }

    if (construct && si == null) {
      si = new SegHPRangeInStrip(blackBox, stripX);
      lzSRQueue.add(insertPos, si);
    }
    return si;

  }

  /**
   * Add a pixel to this segment's heated range.
   * 
   * It is possible, with hex grids, that a pixel that is outside of a segment's
   * clipping range is added to the heated range.
   * 
   * If hex grid, it is possible that seg A and B both have positive slopes,
   * with A preceding B, yet B is hot in pixel H that is below A's range.
   * This only happens if A intersects B in the concave bit between H and
   * H+1, in the pixel column to the left of H.  (Symmetric result holds
   * for neg slopes / to right of H.)  See notes June 25 (1).
   * 
   * See orderHotPixels().  The hot pixel range will only be at most one
   * pixel different than its clip range.
   *
   * Now modified to avoid this problem?
   * 
   * @param blackBox
   * @param stripX0
   * @param pt
   */
  public void addToHeatRange(BlackBox blackBox, int stripX0, IPoint2 pt) {
    final boolean db = false;
    if (db && T.update())
      T.msg("addToSnapRange " + this + //
          " stripX=" + stripX0 + " pt=" + pt);

    if (db && T.update())
      T.msg("segment " + this + " addToSnapRange pt=" + pt);
    SegHPRangeInStrip si = lazyEvalForStrip(blackBox, stripX0, true);
    si.include(pt);

    if (VERIFYRANGE) {
      Tools.warn("testing range");
      Range r = blackBox.getClipRangeWithinPixelColumn(this, pt.x);
      if (!r.contains(pt.y)) {
        T.msg("addToHeatRange, hot pixel " + pt + "\n not near range " + r
            + " for seg " + this + "\n" + Tools.stackTrace(1, 5));
      }
    }

    if (db && T.update())
      T.msg(" snap range now " + si.getRange(pt.x));

    // If this is a bracket, add to original seg too
    if (bracketFor != null)
      bracketFor.addToHeatRange(blackBox, stripX0, pt);

  }

  /**
    * Determine if segment is heated within a pixel column
    * @param pixelColumnPos
    * @return true if heating occurs
    */
  public boolean isHeatedInPixelColumn(BlackBox blackBox, int pixelColumnPos) {
    final boolean db = false;

    boolean ret = false;
    if (db && T.update())
      T.msg("isHeatedInPixelColumn, pos=" + pixelColumnPos);

    for (int sx = blackBox.firstStripInPixel(pixelColumnPos); sx <= blackBox
        .lastStripInPixel(pixelColumnPos); sx++) {
      if (db && T.update())
        T.msg(" sx=" + sx);
      SegHPRangeInStrip r = lazyEvalForStrip(blackBox, sx, false);
      if (r != null && r.rangeDefined(pixelColumnPos)) {
        ret = true;
        break;
      }
    }
    if (db && T.update())
      T.msg("isHeated, seg " + this + ", pixelColumn=" + pixelColumnPos
          + ", returning " + ret);
    return ret;
  }

  public boolean isHeatedInPixelColWithinStrip(BlackBox blackBox, int stripX,
      int pc) {
    SegHPRangeInStrip si = lazyEvalForStrip(blackBox, stripX, false);
    return si != null && si.rangeDefined(pc);
  }

  public Range getHeatPixelRange(int pc, BlackBox blackBox, int stripX) {
    SegHPRangeInStrip si = lazyEvalForStrip(blackBox, stripX, false);
    return si == null ? null : si.getRange(pc);
  }

  public boolean hasNegativeSlope() {
    return y1.intValue() < y0.intValue();
  }

  /**
   * Get the HeatedSegmentList entry object assigned to this segment
   * @return Object, or null if none assigned
   */
  public Object getHeatEntry() {
    return heatEntry;
  }

  /**
   * Assign HeatedSegmentList entry object to this segment
   * @param e : Object to assign
   */
  public void setHeatEntry(Object e) {
    this.heatEntry = e;
  }

  public int getPageId() {
    return segPageId;
  }

  public void setTree(SegTree segTree2) {
    this.segTree = segTree2;
  }

  public Segment constructBracket(boolean upper) {
    int y = upper ? y1.intValue() : y0.intValue();
    Segment s = new Segment(x0.intValue(), y, x0.intValue() + 1, y);
    s.id = this.id;
    s.bracketFor = this;
    return s;
  }

  /**
   * Get the vertical segment that this segment is a bracket for
   * @return vertical segment, or this if we're not a bracket for a vertical segment
   */
  public Segment bracketToVertical() {
    return bracketFor != null ? bracketFor : this;
  }

  public int x0() {
    return x0.intValue();
  }
  public int x1() {
    return x1.intValue();
  }
  public int y0() {
    return y0.intValue();
  }
  public int y1() {
    return y1.intValue();
  }
  public int nSnapPoints() {
    return snapList.size();
  }

  public IPoint2 getSnapPoint(int index) {
    return (IPoint2) snapList.get(index);
  }

  public IPoint2 popSnapPoint() {
    return (IPoint2) snapList.pop();
  }

  public DArray popSnapPoints(int count) {
    DArray a = new DArray(count);
    for (int i = 0; i < count; i++)
      a.add(snapList.get(snapList.size() - count + i));
    snapList.removeRange(snapList.size() - count, snapList.size());
    return a;
  }

  public IPoint2 peekSnapPoint() {
    return (IPoint2) snapList.last();
  }

  public void addSnapPoint(IPoint2 pt) {
    snapList.add(pt);
  }

  public void startNewPts() {
    prevSnapList = snapList.size();
  }

  public int startOfNewPts() {
    return prevSnapList;
  }

  public void resetSnappedFlag() {
    this.lastSnappedTo = Integer.MIN_VALUE;
  }

  public boolean snapped(int pc) {
    return lastSnappedTo == pc;
  }

  public void setSnappedFlag(int pc) {
    lastSnappedTo = pc;
  }

  private int lastSnappedTo;

  private int prevSnapList;

  // Id of segment (>= 0)
  private int id;

  // handle associated with segment, for modifying tree in O(1) time
  private Handle handle;

  // used by HeatList: pointer to Entry
  private Object heatEntry;

  private DArray lzSRQueue;

  private SegTree segTree;

  private int segPageId;

  private Segment bracketFor;

  private DArray snapList = new DArray();

  private Scalar x0, y0, x1, y1;

  public void setMark(HotPixel m) {
    this.mark = m;
  }
  public HotPixel getMark() {
    return mark;
  }

  private HotPixel mark;

}
