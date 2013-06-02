package snap;

import java.util.*;
import base.*;

public class HotPixel extends IPoint2 {
  
  
  public HotPixel(IPoint2 pt) {
    super(pt);
  }

  public HotPixel prev() {
    return link0;
  }
  public HotPixel next() {
    return link1;
  }

  public boolean createLink() {
    if (link1 == null)
      throw new NoSuchElementException();
    boolean ret = vertEdgeFlag;
    vertEdgeFlag = true;
    return ret;
  }

  public String toString() {
    if (true)
      return super.toString();
    else {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      if (link0 != null)
        sb.append('<');
//      if (T.active()) {
        SnapUtils.plot(this);
//      }
      IPoint2.toString(sb, this);
      if (vertEdgeFlag)
        sb.append('=');
      if (link1 != null)
        sb.append('>');
      sb.append("]");
      return sb.toString();
    }
  }

  public int getNode() {
    return graphNode;
  }
  public void setNode(int n) {
    this.graphNode = n;
  }

  /**
   * Determine if vertical edge exists between this hot pixel and its upper neighbor
   * @return true if so
   */
  public boolean vertEdge() {
    return vertEdgeFlag;
  }

  /**
   * Create links between two hot pixels within a column, and
   * clear the vertical edge flag
   * @param p1
   * @param p2
   */
  public static void join(HotPixel p1, HotPixel p2) {
    p1.link1 = p2;
    p2.link0 = p1;
    p1.vertEdgeFlag = false;
  }

  private boolean vertEdgeFlag;
  private HotPixel link0, link1;
  private int graphNode;
}
