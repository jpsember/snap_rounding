package snap;

/**
 * ADT for a vertical range of pixels
 */
public class ScalarRange extends Range {

  public ScalarRange() {this(0);}
  
  /**
   * Constructor
   * @param y : single pixel within range
   */
  public ScalarRange(int y) {
    init(y, y);
  }

  /**
   * Constructor
   * @param y0, y1 : extreme pixels of range, in any order
   */
  public ScalarRange(int y0, int y1) {
    init(y0, y1);
  }

  /**
   * Replace range with new values
   * @param y0, y1 : extreme pixels of range, in any order
   */
  private void init(int y0, int y1) {
    if (y0 > y1) {
      this.y0 = y1;
      this.y1 = y0;
    } else {
      this.y0 = y0;
      this.y1 = y1;
    }
  }

  /**
   * Expand range if necessary to include a new pixel
   * @param y : pixel to include
   */
  public boolean include(int y) {
    boolean mods = true;

    if (y < y0) {
      y0 = y;
    } else if (y > y1) {
      y1 = y;
    } else {
      mods = false;
    }
    return mods;
  }

  /**
   * Get minimum pixel of range
   * @return
   */
  public int y0() {
    return y0;
  }

  /**
   * Get maximum pixel of range
   * @return
   */
  public int y1() {
    return y1;
  }

  private int y0;
  private int y1;
}
