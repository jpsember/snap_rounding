package snap;

import java.util.*;
import base.*;
import testbed.*;

/**
 * Priority queue for starting/stopping segments
 */
class SegPriorityQueue implements Comparator {

  /**
   * Constructor
   * @param ptIndex : 0 if queue stores startpoint, 1 for endpoint
   */
  public SegPriorityQueue(BlackBox blackBox, int ptIndex) {
    this.ptIndex = ptIndex;
    this.blackBox = blackBox;
    set = new TreeSet(this);
  }

  public void add(Segment s) {
    final boolean db = false;

    set.add(s);
    if (db && T.update()) {
      T.msg("add to priority queue: " + s.str() + " x1=" + s.x1());
    }
  }

  public int compare(Object object, Object object1) {
    Segment a = (Segment) object, b = (Segment) object1;

    IPoint2 pa, pb;
    pa = blackBox.toStripSpace(a.pt(ptIndex));
    pb = blackBox.toStripSpace(b.pt(ptIndex));
    int out = 0;
    do {
      out = IPoint2.compare(pa, pb);
      if (out != 0)
        break;
      out = a.id() - b.id();
    } while (false);
    return out;
  }

  public Segment peek() {
    if (peek == null) {
      if (!set.isEmpty()) {
        peek = (Segment) set.first();
        set.remove(peek);
      }
    }
    return peek;
  }

  public Segment pop() {
    if (peek == null) {
      peek();
    }
    if (peek == null) {
      throw new NoSuchElementException();
    }
    Segment s = peek;
    peek = null;
    return s;
  }

  public boolean isEmpty() {
    return peek() == null;
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SegPriorityQueue ptIndex=" + ptIndex);
    sb.append(" [ ");
    if (peek != null) {
      sb.append(peek);
      sb.append(' ');
    }
    Iterator it = set.iterator();
    while (it.hasNext()) {
      Segment s = (Segment) it.next();
      sb.append(s);
      sb.append(' ');
    }
    sb.append("]");

    return sb.toString();
  }

  private BlackBox blackBox;
  
  private Segment peek;

  private int ptIndex;

  private TreeSet set;
}
