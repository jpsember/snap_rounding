package snap;

import java.util.*;
import base.*;

public class HotPixelColumn {

  /**
   * Constructor
   * @param src : DArray containing IPoint2s, sorted lexicographically into columns
   * @param offset : index of first pixel in column
   * @param length : number of pixels in column
   */
  public HotPixelColumn(DArray src, int offset, int length ) {
    this.hp = src;
    this.offset = offset;
    this.length = length;
    if (length <= 0)
      throw new IllegalArgumentException();
  }

  /**
   * Get number of pixels in column
   * @return
   */
  public int size() {
    return length;
  }

  /**
   * Get pixel from column
   * @param i
   * @return
   */
  public HotPixel get(int i) {
    if (!exists(i))
      throw new NoSuchElementException();
    return (HotPixel) hp.get(i + offset);
  }

  /**
   * Determine if pixel exists with offset
   * @param i : offset to test
   * @return
   */
  private boolean exists(int i) {
    return (i >= 0 && i < length);
  }

  public int x() {
    return get(0).x;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HotPixelColumn ");
//    IPointUtils.encode(sb, get(0));
    sb.append("\n");
    for (int i = 0; i < size(); i++)
      sb.append(Tools.f(get(i).y, 3));
    return sb.toString();
  }
  private DArray hp;
  private int offset, length;
}
