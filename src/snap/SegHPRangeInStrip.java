package snap;

import base.*;

/**
 * Maintains range of hot pixels for each pixel column intersecting a strip.
 */
public class SegHPRangeInStrip {

  /**
   * Constructor
   * 
   * @param stripX : sweep line position
   */
  public SegHPRangeInStrip(BlackBox blackBox, int stripX) {
    this.stripX = stripX;

    pciFirst = blackBox.firstPixelColumnIntersectingStrip(stripX);
    int pciLast = blackBox.lastPixelColumnIntersectingStrip(stripX);
    heatRange = new ScalarRange[pciLast + 1 - pciFirst];
  }

  
  public int getPixelY(int pc, boolean upper) {
    ScalarRange r = heatRange[findPC(pc)];
    return upper ? r.y1() : r.y0();
  }


  private int findPC(int pc) {
    return pc - pciFirst;
  }

  public void include(IPoint2 pt) {
    int j = findPC(pt.x);
    if (heatRange[j] == null)
      heatRange[j] = new ScalarRange(pt.y);
    else
      heatRange[j].include(pt.y);
  }

  public boolean rangeDefined(int pc) {
    return heatRange[findPC(pc)] != null;
  }

  public  Range getRange(int pc) {
    return heatRange[findPC(pc)];
  }

  /**
   * Get strip number for this object
   * @return stripX
   */
  public int stripX() {
    return stripX;
  }

  private int pciFirst;
  private int stripX;
  private ScalarRange[] heatRange;
}
