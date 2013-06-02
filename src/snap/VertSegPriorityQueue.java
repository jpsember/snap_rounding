package snap;

import java.util.*;
import testbed.*;
import base.*;

/**
* Priority queue for starting/stopping segments
*/
public class VertSegPriorityQueue implements Comparator {
  final boolean db = false;

  /**
   * Constructor
   * @param ptIndex : 0 if queue stores startpoint, 1 for endpoint
   */
  public VertSegPriorityQueue() {
    set = new TreeSet(this);
  }

  public void add(Segment sOrig) {
    VertSegEntry a = new VertSegEntry(sOrig, 0);
    VertSegEntry b = new VertSegEntry(sOrig, 1);
    if (db && T.update()) {
      T.msg("VertSegPriorityQueue.add: " + sOrig.str() + "\n  a=" + a
          + "\n  b=" + b);
    }
    set.add(a);
    set.add(b);
  }

  public int compare(Object object, Object object1) {
    boolean db = false;
    VertSegEntry a = (VertSegEntry) object, b = (VertSegEntry) object1;

    IPoint2 pa, pb;
    pa = a.pt();
    pb = b.pt();

    int out = IPoint2.compare(pa, pb);
    if (out == 0)
      out = a.ptIndex() - b.ptIndex();
    if (out == 0)
      out = a.seg().id() - b.seg().id();

    if (db && T.update())
      T.msg("VertSegPriorityQueue.compare\n a=" + a + "\n b=" + b
          + "\n returning " + out);
    return out;
  }

  public VertSegEntry peek() {
    if (peek == null) {
      if (!set.isEmpty()) {
        peek = (VertSegEntry) set.first();
        set.remove(peek);
      }
    }
    return peek;
  }

  public VertSegEntry pop() {
    if (peek == null) {
      peek();
    }
    if (peek == null) {
      throw new NoSuchElementException();
    }
    VertSegEntry s = peek;
    peek = null;
    if (db && T.update())
      T.msg("VertSegPriorityQueue.pop:\n " + s);
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
    sb.append("VertSegPriorityQueue [");
    Iterator it = set.iterator();
    while (it.hasNext()) {
      VertSegEntry s = (VertSegEntry) it.next();
      sb.append(s.seg());
      sb.append(":");
      sb.append(s.ptIndex());
      sb.append(' ');
    }
    sb.append("]");
    return sb.toString();
  }

  private VertSegEntry peek;

  private TreeSet set;
}

class VertSegEntry {

  public IPoint2 pt() {
    return seg.pt(endPtIndex);
  }

  public Segment seg() {
    return seg;
  }

  public VertSegEntry(Segment sOrig, int epIndex) {
    this.seg = sOrig;
    this.endPtIndex = epIndex;
  }

  public Segment constructBracket() {
    Segment bracket = seg.constructBracket(endPtIndex != 0);
    return bracket;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("VertSegEntry");
    sb.append(" seg:" + seg);
    sb.append(" endPt:" + endPtIndex);
    sb.append(" " + seg.pt(endPtIndex));
    return sb.toString();
  }

  public int ptIndex() {
    return endPtIndex;
  }

  private Segment seg;

  private int endPtIndex;
}
