package snap;

import java.util.*;
import testbed.*;
import base.*;

/**
 * Set of heat lists for contiguous sweep strips
 */
public class HeatListSet {

  /**
   * Add heat lists for left and right sides of current sweep strip to the set.
   * @param blackBox
   * @param stripX : position of current sweep strip
   * @param hl, hr : HeatedSegmentLists for left and right side of sweep strip
   */
  public void add(BlackBox blackBox, int stripX, HeatedSegmentList hl,
      HeatedSegmentList hr) {

    if (!entries.isEmpty()) {
      HeatListEntry e = (HeatListEntry) entries.last();
      if (e.stripX() >= stripX)
        throw new IllegalStateException(
            "HeatListSet not strictly increasing:\n" + stripX + "\n" + entries);
    }

    entries.add(new HeatListEntry(stripX, hl, hr));
  }

  /**
   * Get all heat lists for a particular pixel column.  Remove those that
   * are before this column.
   * 
   * @param pc : pixel column position
   * @return DArray containing HeatListEntries
   */
  public Iterator getSetsFor(BlackBox blackBox, int pc) {
    final boolean db = false;

    int first = blackBox.firstStripInPixel(pc);
    int last = blackBox.lastStripInPixel(pc);

    DArray a = new DArray();

    int delCount = 0;
    for (int i = 0; i < entries.size(); i++) {
      HeatListEntry e = (HeatListEntry) entries.get(i);

      if (e.stripX() < first) {
        delCount++;
        continue;
      }
      if (e.stripX() > last)
        break;
      a.add(e);
    }

    // delete old entries from start of list
    if (delCount > 0)
      entries.removeRange(0, delCount);

    if (db && T.update())
      T.msg("getSetsFor pc=" + pc + " returning:\n" + a.toString(true));
    return a.iterator();
  }

  private DArray entries = new DArray();

}

class HeatListEntry {
  public int stripX() {
    return stripX;
  }

  public HeatedSegmentList left() {
    return left;
  }

  public HeatedSegmentList right() {
    return right;
  }

  private int stripX;

  public HeatedSegmentList getList(boolean right) {
    return right ? this.right : this.left;
  }

  private HeatedSegmentList left;

  private HeatedSegmentList right;

  public HeatListEntry(int stripX, HeatedSegmentList hl, HeatedSegmentList hr) {
    this.stripX = stripX;
    this.left = hl;
    this.right = hr;
  }
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HeatListEntry");
    sb.append(" strip="+stripX);
    sb.append("\nleft="+left);
    sb.append("\nright="+right);
    return sb.toString();
  }
}
