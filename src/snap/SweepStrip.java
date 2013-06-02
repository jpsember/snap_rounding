package snap;

import testbed.*;
import java.awt.*;

/**
 * Sweep strip class.
 * Maintains position of sweep strip during plane sweep.
 */
public class SweepStrip implements Renderable {
  //  public String encode() {
  //    throw new UnsupportedOperationException();
  //      }
  //
  //  public Traceable parse(Tokenizer tk) {
  //    SweepStrip s = new SweepStrip(tk.extractInt());
  //    return s;
  //  }

  //  public void plotTrace() {
  //    if (blackBox != null && stripX() > Integer.MIN_VALUE
  //        && stripX() < Integer.MAX_VALUE) {
  //      blackBox.renderSweepLine(stripX());
  //    }
  //  }
  //  
  public void render(Color c, int stroke, int markType) {
    if (blackBox != null && stripX() > Integer.MIN_VALUE
        && stripX() < Integer.MAX_VALUE) {
      blackBox.renderSweepLine(stripX());
    }
  }

  //  private static SweepStrip factory = new SweepStrip();

  //  private static final String tag = SweepStrip.class.getSimpleName();

  //  /**
  //   * @param tk
  //   * @param readTag
  //   * @return
  //   */
  //  public static SweepStrip extract(Tokenizer tk) {
  //    T.openTag(tk, tag);
  //    SweepStrip s = (SweepStrip) factory.parse(tk);
  //    T.closeTag(tk);
  //    return s;
  //  }

  private static BlackBox blackBox;

  /**
   * Set BlackBox to use for rendering (algorithm tracing only)
   * @param b
   */
  public static void setBlackBox(BlackBox b) {
    blackBox = b;
  }

  //  static {
  //    T.registerTag(tag, new SweepStrip());
  //  }
  //
  /**
   * Constructor.
   * 
   * Sets sweep line offscreen to left.
   */
  public SweepStrip() {
    this(Integer.MIN_VALUE);
  }

  /**
   * Constructor
   * @param stripX : initial position of sweep strip
   */
  public SweepStrip(int stripX) {
    this.x = stripX;
  }

  /**
   * Advance sweep strip 
   * @param stripX : new position; must not be to left of current position
   */
  public void moveTo(int stripX) {
    {
      if (stripX < this.x) {
        T.err("sweep line is moving to left:\n" + this + " to position="
            + stripX);
      }
      this.x = stripX;
    }
  }

  /**
   * Get position of sweep strip. 
   * 
   * @return position of sweep line, in strip space
   */
  public int stripX() {
    return x;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("SweepStrip(");
    sb.append(x);
    sb.append(')');
    //    encode(sb);
    return sb.toString();
  }

  //  public void encode(StringBuilder sb) {
  //    T.openTag(sb, tag);
  //    sb.append(x);
  //    T.closeTag(sb);
  //    //
  //  }
  private int x;

}
