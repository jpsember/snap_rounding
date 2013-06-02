package snap;

import java.util.*;
import testbed.*;
import base.*;

/**
 * Ordered, linked list of hot segments or neighbors of hot segments.
 * 
 * Each segment has a reference to an entry for ONE of these HeatedSegmentLists.
 * See SnapOper.doSweepProcess().
 */
public class HeatedSegmentList {

  private static final boolean db = false;

  // test data integrity
  private static final boolean VERIFY = false;

  /**
   * Constructor
   * @param stripX : position of sweep strip
   * @param debugName : for debug purposes only
   */
  public HeatedSegmentList(int stripX, String debugName) {
    this(stripX, debugName, OLDMETHOD);
  }

  /**
   * Constructor
   * @param stripX : position of sweep strip
   * @param debugName : for debug purposes only
   * @param includeNeighbors : true if non-heated neighbors should be included
   *  in this list; ignored if NEW_NEIGHBOR_METHOD is not true
   */
  public HeatedSegmentList(int stripX, String debugName,
      boolean includeNeighbors) {
    this.stripX = stripX;
    this.debugName = debugName + "(stripX=" + stripX + ")";
    this.includeNeighbors = includeNeighbors;
    sentinel = new HSEntry(null);
    sentinel.prev = sentinel.next = sentinel;
  }

  public String debugName() {
    return debugName;
  }

  /**
   * Construct a copy of a HeatList
   * @param src : original HeatList
   * @param debugName : new list's (debug) name
   */
  public HeatedSegmentList(HeatedSegmentList src, String debugName) {
    this.debugName = debugName;
    this.stripX = src.stripX;
    this.includeNeighbors = src.includeNeighbors;
    sentinel = new HSEntry(null);
    sentinel.prev = sentinel.next = sentinel;

    HSEntry destEntry = sentinel;
    HSEntry srcEntry = src.sentinel.next;
    while (srcEntry != src.sentinel) {
      destEntry = destEntry.insertAbove(srcEntry.seg);
      srcEntry = srcEntry.next;
    }
  }

  public String toString() {
    return toString(false);
  }

  private String toString(boolean big) {
    StringBuilder sb = new StringBuilder();
    sb.append("HeatedSegmentList ");
    sb.append(debugName);
    sb.append("\n [");

    HSEntry ent = sentinel;
    if (!big)
      ent = ent.next;

    while (true) {
      if (!big && ent == sentinel)
        break;
      if (big) {
        sb.append(' ');
        sb.append(ent.prev);
        sb.append(' ');
      }
      sb.append(ent);
      sb.append(' ');
      if (big) {
        sb.append(ent.next);
        sb.append('\n');
      }
      ent = ent.next;
      if (ent == sentinel)
        break;
    }

    sb.append("]");
    if (big)
      sb.append('\n');
    return sb.toString();
  }

  /**
   * Add a segment to the list
   * @param s
   */
  public void add(Segment s) {
    add(s, OLDMETHOD || includeNeighbors);
  }

  /**
   * Add a segment to this list.  
   * 
   * Insert non-hot neighbors, if desired, of this segment.
   * 
   * @param s : Segment to add; should not occur elsewhere in list, except
   *  possibly as the last element (or the second to last, in case its non-hot
   *  neighbor was added)
   * @param insertNonHotNeighbors : if false, doesn't include non-hot neighbors.
   *   Currently used only for processing vertical segments
   */
  public void add(Segment s, boolean insertNonHotNeighbors) {

    if (db && T.update())
      T.msg("add " + s + " to " + this);
    do {

      // if we already added this seg, do nothing
      // check last two entries, in case it was capped
      if (sentinel.prev.seg == s || sentinel.prev.prev.seg == s) {
        if (db && T.update())
          T.msg("last entry is already this seg");
        // cap if necessary
        if (insertNonHotNeighbors)
          capFor(s);
        break;
      }
      if (db && T.update())
        T.msg("prev.seg " + sentinel.prev.seg + " != this one, adding");

      if (insertNonHotNeighbors) {
        Segment s2 = s.neighborBelow();
        if (s2 != null && s2 != sentinel.prev.seg) {
          sentinel.prev.insertAbove(s2);
        }
      }
      sentinel.prev.insertAbove(s);

      if (insertNonHotNeighbors)
        capFor(s);

    } while (false);

    if (db && T.update())
      T.msg(" after adding: " + this);
    if (VERIFY)
      verify();

  }

  private void capFor(Segment s) {
    Segment s2 = s.neighborAbove();
    if (s2 != null && sentinel.prev.seg != s2) {
      sentinel.prev.insertAbove(s2);
    }
  }

  private void verify() {
    if (VERIFY) {
      DArray a = new DArray();
      Map map = new HashMap();
      HSEntry ent = sentinel.next;
      while (ent != sentinel) {
        a.add(ent.seg);
        if (map.containsKey(ent.seg))
          T.msg("Segment " + ent.seg + " appears twice:\n" + this);
        map.put(ent.seg, Boolean.TRUE);
        ent = ent.next;
      }
    }
  }

  /**
   * Exchange positions of two segments within list
   * This should be done AFTER the segments have been switched within the tree.
   * We add new neighboring segments to the list, if necessary
   * 
   * @param a : new lower segment
   * @param b : new upper segment
   */
  public void exchangeSegPositions(Segment a, Segment b) {

    if (db && T.update())
      T.msg(this + "\n exchangeSegPositions, a=" + a + ", b=" + b);

    HSEntry ea = (HSEntry) a.getHeatEntry(), eb = (HSEntry) b.getHeatEntry();

    a.setHeatEntry(eb);
    b.setHeatEntry(ea);
    ea.seg = b;
    eb.seg = a;

//    Tools.warn("verify that not inserting neighbors is ok for exchanging seg pos");
//    
//    if (OLDMETHOD)
    {
    {
      Segment n = a.neighborAbove();
      if (n != null && eb.next.seg != n) {
        eb.insertAbove(n);
      }
    }
    {
      Segment n = b.neighborBelow();
      if (n != null && ea.prev.seg != n) {
        ea.prev.insertAbove(n);
      }
    }
    }
    if (db && T.update())
      T.msg(this + "\n after exchangeSegPositions");

    if (VERIFY)
      verify();
  }

  /**
   * Get Iterator
   * @return
   */
  public HeatedSegmentListIterator iterator() {
    return iterator(false);
  }

  /**
   * Get Iterator
   * @param reverseDir : if true, iterates backward through list
   * @return
   */
  public HeatedSegmentListIterator iterator(boolean reverseDir) {
    return new HeatedSegmentListIterator(sentinel, reverseDir);
  }

  /**
   * Merge two HeatedSegmentLists together into a new  HeatList.
   * The new HeatList will not be an 'includeNeighbor' list.
   * @param blackBox
   * @param a, b : HeatedSegmentLists to merge
   * @param debugName : debug name of new list
   * @return merged HeatedSegmentList
   */
  public static HeatedSegmentList merge(BlackBox blackBox, HeatedSegmentList a,
      HeatedSegmentList b, String debugName) {

    HeatedSegmentList h = new HeatedSegmentList(a.stripX, debugName);

    HSEntry ea = a.sentinel.next;
    HSEntry eb = b.sentinel.next;

    SegmentComparator segComparator = new SegmentComparator(blackBox, h
        .stripX());

    while (true) {
      if (ea == a.sentinel) {
        if (eb == b.sentinel)
          break;
        h.add(eb.seg, false);
        eb = eb.next;
        continue;
      }
      if (eb == b.sentinel) {
        if (ea == a.sentinel)
          break;
        h.add(ea.seg, false);
        ea = ea.next;
        continue;
      }

      if (segComparator.compare(ea.seg, eb.seg) <= 0) {
        h.add(ea.seg, false);
        ea = ea.next;
      } else {
        h.add(eb.seg, false);
        eb = eb.next;
      }
    }

    if (db && T.update())
      T.msg("Merged:\n" + a + "\n" + b + "\n to:\n" + h);
    return h;
  }

  /**
   * Determine if this list is empty
   * @return
   */
  public boolean isEmpty() {
    return sentinel.next == sentinel;
  }

  /**
   * Get sweep strip position of this list
   * @return sweep strip position
   */
  public int stripX() {
    return this.stripX;
  }

  private static boolean OLDMETHOD;

  // first/last entry in list
  private HSEntry sentinel;

  private String debugName;

  // strip position of list
  private int stripX;

  private boolean includeNeighbors;
}

/**
 * Iterator for HeatList
 */
class HeatedSegmentListIterator {
  private HSEntry current;

  private boolean forwardDir;

  public HeatedSegmentListIterator(HSEntry sentinel, boolean reverseDir) {
    this.forwardDir = !reverseDir;
    if (forwardDir)
      current = sentinel.next;
    else
      current = sentinel.prev;
  }

  public boolean hasNext() {
    return current.seg != null;
  }

  public Segment peek() {
    return current.seg;
  }

  public Segment next() {
    Segment s = current.seg;
    if (forwardDir)
      current = current.next;
    else
      current = current.prev;
    return s;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}

/**
 * Class for entries within linked list
 */
class HSEntry {
  /**
   * Insert a new entry for a segment above this one
   * @param s : segment
   * @return the new entry
   */
  public HSEntry insertAbove(Segment s) {
    HSEntry newEntry = new HSEntry(s);

    HSEntry next = this.next;
    this.next = newEntry;
    newEntry.prev = this;
    newEntry.next = next;
    next.prev = newEntry;

    return newEntry;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (seg == null)
      sb.append("<s>");
    else
      sb.append(Tools.f(seg.toString(), 3));
    return sb.toString();
  }

  public HSEntry(Segment s) {
    this.seg = s;
    if (s != null) {
      // make the segment point to this entry
      s.setHeatEntry(this);
    }
  }

  // previous entry in list
  HSEntry prev;

  // next entry in list
  HSEntry next;

  // segment associated with this entry
  Segment seg;
}
