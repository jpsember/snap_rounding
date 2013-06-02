package snap;

import java.util.*;
import base.*;
import testbed.*;
import snaptree.*;

public class SegTree {

  public int getHeight() {
    return tree.getHeight();
  }

  private SegPage getPage(int id) {
    return (SegPage) tree.read(id);
  }

  /**
   * Find neighbor to a particular side
   * 
   * @param side :
   *          0 for left, 1 for right
   * @return Segment, null if none
   */
  public Segment neighbor(Segment seg, int segPageId, boolean toRight) {
    int pos = -1;
    SegPage pg = getPage(segPageId);

    if (toRight) {
      pos = 1 + pg.positionOf(seg);
      if (pos >= pg.nKeys()) {
        pos = 0;
        pg = pg.sibPage(true);
      }
    } else {
      pos = pg.positionOf(seg) - 1;
      if (pos < 0) {
        pg = pg.sibPage(false);
        if (pg != null) {
          pos = pg.nKeys() - 1;
        }
      }
    }

    Segment out = null;
    if (pg != null) {
      out = pg.seg(pos);
    }
    return out;
  }

  private static class OurBTree extends BTree {
    public OurBTree(Comparator comparator) {
      super(comparator);
    }

    /**
     * Construct a BPage (i.e. the BPage factory)
     * @param id : id to assign to page
     * @param isLeaf : true if it's to be a leaf page (vs. an interior/index page)
     * @return BPage
     */
    protected BPage constructPage(int id, boolean isLeaf) {
      return new SegPage(this, id, isLeaf);
    }
  }

  public SegTree(BlackBox blackBox, SweepStrip sweepLine) {
    this(sweepLine, new SegmentComparator(blackBox, sweepLine));
    //    this.sweepLine = sweepLine;
    //    this.tree = new OurBTree(new SegmentComparator(blackBox, sweepLine));
  }

  public SegTree(SweepStrip sweepLine, Comparator c) {
    this.sweepLine = sweepLine;
    this.tree = new OurBTree(c);
  }

  public void add(Segment s) {
    s.setTree(this);
    tree.add(s);
  }

  public void remove(Segment s) {
    s.setTree(null);
    tree.remove(s);
  }

  /**
   * @return
   */
  public IsectEvent peekNextIsectEvent() {
    SegPage pg = root();
    return pg.getFirstIntersectionEvent();
  }

  private SegPage root() {
    return (SegPage) tree.getRootPage();
  }

  /**
   * Construct a string describing the segments in the leaf nodes.
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("SegTree:");
    Segment s = null;
    SegPage pg = root();
    while (!pg.isLeaf()) {
      pg = pg.childPage(0);
    }
    if (pg.nKeys() != 0) {
      s = pg.seg(0);
    }

    while (true) {
      if (s == null) {
        break;
      }
      Segment s2 = s.neighbor(false);
      if (s2 == null) {
        break;
      }
      s = s2;
    }

    while (true) {
      if (s == null) {
        break;
      }
      sb.append(' ');
      sb.append(s.name());
      //      sb.append(s.pt(0)+" .. "+s.pt(1));
      s = s.neighbor(true);
    }
    return sb.toString();
  }

  /**
   * Determine if an IsectEvent is valid for this sweepline
   * 
   * @param e
   *          IsectEvent
   * @return boolean
   */
  private boolean eventValid(IsectEvent e) {
    //    if (e != null) {
    //      if (T.update())
    //        T.msg("eventValid: " + e + "\n valid=" + e.valid() + "\n sweepLine="
    //            + sweepLine + "\n occursWithin=" + e.occursWithin(sweepLine)
    //             );
    //    }
    //
    return e != null && e.valid() && e.occursWithin(sweepLine);
  }

  /**
   * Get all intersection events from tree that are predicted for the current
   * sweep strip.
   * @param intersectionEvents : queue to populate with events
   * @return HeatedSegmentList of involved segments
   */
  public HeatedSegmentList extractIntersectEvents(DQueue intersectionEvents) {
      
    HeatedSegmentList hl = new HeatedSegmentList(sweepLine.stripX(),
        "intersect");
    extractIntersectEvents(intersectionEvents, root(), hl, 1);
    return hl;
  }

  public void resetStats() {
    stat_eventCount = stat_edgeCount = stat_maxEdgeCount = 0;
    stat_tTotal = 0;
  }
  
  public int statEventCount() {return stat_eventCount;}
  public int statEdgeCount() {return stat_edgeCount;}
  public int statMaxEdgeCount() {return stat_maxEdgeCount;}
  public double statCalcN() {
    return stat_tTotal / (double)stat_eventCount;
  }
  public int statHotPixels() {return stat_hotPixels;}

  private int stat_eventCount;
  private int stat_edgeCount;
  private int stat_maxEdgeCount;
  private int stat_tTotal;
  private int stat_hotPixels;
  
  /**
   * Invalidate all nodes in the tree that lie on the path from a segment to the
   * root.
   * 
   * Stops if it reaches a node that has already been invalidated in this
   * sweep strip; thus the remaining path to the root has already been invalidated.
   * 
   * @param s
   *          Segment
   */
  public void invalidateSegmentPath(Segment s) {
    SegPage pg = getPage(s.getPageId());
    while (pg != null) {
      Stats.event(Stats.TREE, "invalidatePath");

      if (pg.pathInvalidatedAt != sweepLine.stripX()) {
        pg.pathInvalidatedAt = sweepLine.stripX();
        pg.pathInvalidated = false;
      }
      if (pg.pathInvalidated)
        break;

      pg.pathInvalidated = true;
      pg.touch();
      pg = pg.parentPage();
    }
  }

  /**
   * Get all intersection events from subtree that are predicted for the current
   * sweep position.
   * 
   * Descends recursively from root node to children to gather these events.
   * 
   * Recursion stops when child page heap event does not exist, or occurs to the
   * right of the current sweep line.
   * 
   * @param intersectionEvents : queue to populate with events
   * @param root : SegPage for root of subtree
   * @param hl : HeatedSegmentList we're constructing
   * @param depth : for statistics gathering only, the depth into the tree
   *   (root = 1)
   */
  private void extractIntersectEvents(DQueue intersectionEvents, SegPage root,
      HeatedSegmentList hl, int depth) {
    boolean db = false;

    Stats.event(Stats.TREE, "extractSeed");

    do {
      // Determine if this subtree has any intersection events.
      // If not, stop recursion without examining child nodes.
      if (!eventValid(root.getFirstIntersectionEvent()))
        break;

      stat_edgeCount++;
      
      if (!root.isLeaf()) {

        // intersection events must be reported in the tree order, for
        // HeatList ordering to occur correctly

        // gather events in subtrees
        for (int i = 0; i < root.nKeys(); i++) {

          if (i > 0) {
            if (db && T.update())
              T.msg("examining trans-subtree events, i=" + i);
            SegPage childPage = root.childPage(i);
            Segment rightSegment = childPage.firstSegment();
            IsectEvent event = new IsectEvent(rightSegment.neighbor(false));
            if (eventValid(event)) {
              if (db && T.update())
                T.msg("pushing trans-subtree event " + event);
              intersectionEvents.push(event);
              hl.add(event.a());
              hl.add(event.b());
              
              stat_eventCount++;
              stat_maxEdgeCount += depth;
              stat_tTotal += tree.getHeight();
            }
          }

          if (db && T.update())
            T.msg("extracting events from subtree " + root.childPage(i));
          extractIntersectEvents(intersectionEvents, root.childPage(i), hl, depth+1);
        }
        break;
      }

      // gather events occurring between two leaf nodes
      for (int i = 0; i < root.nKeys() - 1; i++) {
        Segment sa = root.seg(i);
        IsectEvent event = new IsectEvent(sa);
        if (eventValid(event)) {
          if (db && T.update())
            T.msg("pushing event " + event);
          intersectionEvents.push(event);
          hl.add(event.a());
          hl.add(event.b());
          
          stat_eventCount++;
          stat_maxEdgeCount += depth;
          stat_tTotal += tree.getHeight();
        }
      }
    } while (false);
  }

  public Iterator iterator() {
    return iterator(null);
  }

  public Iterator iterator(Object seekItem) {
    if (seekItem == null)
      return tree.iterator();
    return tree.iterator(seekItem);
  }

  private BTree tree;

  private SweepStrip sweepLine;

  private static class SegPage extends BPage {

    private SegPage parentPage() {
      return (SegPage) getParentPage();
    }

    private static final boolean db = false;

    private SegPage sibPage(boolean toRight) {
      return (SegPage) getSiblingPage(toRight);
    }

    private SegPage childPage(int pos) {
      return (SegPage) getKey(pos).ptr();
    }

    /**
     * Get string describing object
     * 
     * @return String
     */
    public String toString(boolean full) {
      if (!full)
        return super.toString(full);

      StringBuffer sb = new StringBuffer();
      sb.append("SegPage " + getId());
      sb.append(" p=" + getParentPage());
      sb.append(" L=" + Tools.f(isLeaf()));
      sb.append(" #k=" + Tools.f(nKeys(), 2));
      sb.append(' ');
      sb.append(" ");
      sb.append(treeString());
      sb.append("\n");
      sb.append(" keys: ");
      for (int i = 0; i < nKeys(); i++) {
        KeyEntry e = getKey(i);
        if (isLeaf()) {
          sb.append(" " + e.item());
        } else {
          sb.append(" " + e.toString(false));
        }
      }
      return sb.toString();
    }

    private SegPage(BTree tree, int id, boolean isLeaf) {
      super(tree, id, isLeaf);
    }

    private Segment seg(int pos) {
      KeyEntry k = getKey(pos);
      return (Segment) k.item();
    }

    /**
     * Invalidate this page's data
     */
    protected void touch() {
      valid = false;
    }

    /**
     * Determine the first segment in the subtree rooted at this node
     * @return
     */
    private Segment firstSegment() {
      validate();
      return firstSegInSubTree;
    }

    /**
     * Get first intersection event occuring in subtree
     * @return IsectEvent, or null
     */
    private IsectEvent getFirstIntersectionEvent() {
      validate();
      return firstIsectEventInSubTree;
    }

    /**
     * Make sure this page is valid.  If not, perform calculations.
     */
    private void validate() {
      if (!valid) {
        Stats.event(Stats.TREE, "validate");
        if (db && T.update())
          T.msg("SegPage validate " + this);

        firstSegInSubTree = calcFirstSegInSubTree();
        firstIsectEventInSubTree = calcFirstIsectEvtInSubTree();
        valid = true;

        if (db && T.update())
          T.msg(" validated " + this + " max=" + segString(firstSegInSubTree)
              + " heapData=" + firstIsectEventInSubTree);
      }
    }

    private static String segString(Object obj) {
      if (obj == null) {
        return "<null>";
      }
      Segment s = (Segment) obj;
      return Tools.f(s.toString(), 3);
    }

    /**
     * Determine the first segment within a subtree
     * @return Segment, or null
     */
    private Segment calcFirstSegInSubTree() {
      Segment out = null;
      if (isLeaf()) {
        if (nKeys() > 0)
          out = (Segment) getKey(0).item();
      } else
        out = child(0).firstSegment();
      return out;
    }

    private IsectEvent calcFirstIsectEvtInSubTree() {

      // heap data is the smallest of the children's heap data +
      // heap data associated with min+max of neighboring children

      IsectEvent out = null;

      if (isLeaf()) {
        for (int i = 1; i < nKeys(); i++) {
          out = IsectEvent.smallerOf(out, new IsectEvent(seg(i - 1)));
        }
      } else {
        for (int i = 0; i < nKeys(); i++) {
          SegPage child = child(i);
          IsectEvent hdata = child.getFirstIntersectionEvent();
          out = IsectEvent.smallerOf(out, hdata);

          if (i < nKeys() - 1) {
            SegPage child2 = child(i + 1);
            out = IsectEvent.smallerOf(out, new IsectEvent(child2
                .firstSegment().neighbor(false)));
          }
        }
      }
      return out;
    }

    private SegPage child(int index) {
      Tools.ASSERT(!isLeaf());
      return (SegPage) getKey(index).ptr();
    }

    private int positionOf(Segment seg) {
      int out = -1;
      for (int i = 0; i < nKeys; i++) {
        if (seg(i) == seg) {
          out = i;
          break;
        }
      }
      if (out < 0)
        T.msg("can't find segment in page:\nsegment=" + seg + "\npage=\n"
            + this);
      return out;
    }

    private Segment firstSegInSubTree;
    private IsectEvent firstIsectEventInSubTree;
    // true if node has been validated
    private boolean valid;
    // true if this node has already been invalidated within this sweep strip
    private boolean pathInvalidated;
    // sweep strip location where node was previously invalidated, or null
    private int pathInvalidatedAt = Integer.MAX_VALUE - 1;
  }

  public void addHotPixels(int size) {
   stat_hotPixels += size;
  }

}
