package snap;

import base.*;
import java.util.Comparator;
import testbed.*;

public class SegmentComparator implements Comparator {

  public SegmentComparator(BlackBox blackBox, SweepStrip sweepLine) {
    this.sweepLine = sweepLine;
    this.blackBox = blackBox;
  }

  public void setTrace(boolean f) {
    trace = f;

  }
  /**
   * @param blackBox
   * @param stripX
   */
  public SegmentComparator(BlackBox blackBox, int stripX) {
    this(blackBox, new SweepStrip(stripX));
  }

  public int compare(Object obj0, Object obj1) {
    final boolean db = trace;
    int out = 0;
    boolean negFlag = false;

    do {

      if (obj0 == obj1)
        break;

      Segment a = (Segment) obj0;
      Segment b = (Segment) obj1;
      if (db && T.update())
        T.msg("SegmentComparator\n a=" + obj0 + "\n b=" + obj1);

      if (db && T.update())
        T.msg("SegmentComparator comparing " + a + " with " + b
            + "\n" + a.str() + "\n" + b.str());
     

      BlackBox bb = blackBox.construct(a, b);
      if (db && T.update())
        T.msg(" intersection = "
            + 
            SnapUtils.plot( bb.getIntersectionPixel(false)));

      switch (bb.state()) {

      case BlackBox.COLLINEAR:
        out = a.id() - b.id();
        if (db && T.update())
          T.msg("collinear, returning " + out);
        break;

      default:
        {
          boolean p1 = bb.lower() == b;
          boolean p2 = false;

          if (bb.state() == BlackBox.NOT_PARALLEL) {
            if (db && T.update())
              T.msg("swpPos=" + sweepLine.stripX()
                  + " intersect.x=" + bb.getIntersectionPixel(true).x() + "\n"
                  + bb + " p1=" + p1);
            p2 = sweepLine.stripX() > bb.getIntersectionPixel(true).x;
          }
          out = (p1 ^ p2) ? 1 : -1;

          if (db && T.update())
            T.msg("a^b= " + bb.getIntersectionPixel(true)
                + " p1=" + p1 + " p2=" + p2 + " returning: " + out);
          
        }
        break;
      }

      if (out == 0 && a.id() != b.id()) {
        throw new FPError("SegmentComparator failed:\n a=" + a + "\n b=" + b);
      }
    } while (false);

    if (negFlag)
      out = -out;

    if (db && T.update())
      T.msg(" negFlag=" + negFlag + " returning " + out);
    return out;
  }

  protected SweepStrip sweepLine;

  protected BlackBox blackBox;

  protected boolean trace;
}
