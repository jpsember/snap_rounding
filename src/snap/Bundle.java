package snap;

import java.awt.*;
import testbed.*;
import base.*;

public class Bundle  implements Renderable {

  /**
   * Constructor
   * @param originPixel
   * @param low
   * @param high
   */
  public Bundle(HotPixel originPixel, Segment low, Segment high) {
    this.origin = originPixel;
    this.low = low;
    this.high = high;
  }

  public Segment getHigh() {
    return high;
  }

  public HotPixel getOrigin() {
    return origin;
  }

  public Segment getLow() {
    return low;
  }

  //  public void encode(StringBuilder sb) {
  //    T.openTag(sb, tag);
  //    IPointUtils.encode(sb, origin);
  //    low.encode(sb);
  //    high.encode(sb);
  //    T.closeTag(sb);
  //  }
  //
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Bundle");
    sb.append(" origin=" + BlackBox.fromSweep(origin));
    sb.append(" low=" + low.id());
    sb.append(" high=" + high.id());
    //    encode(sb);
    return sb.toString();
  }

  //  private static final String tag = Bundle.class.getSimpleName();

  //  static {
  //    T.registerTag(tag, new Bundle());
  //  }
  //
  //  /**
  //   * Do-nothing constructor for registering tags
  //   */
  //  private Bundle() {
  //  }
  //
  //  public Traceable parse(Tokenizer tk) {
  //    IPoint2 originPixel = IPointUtils.extract(tk  );
  //    Segment sL = Segment.extract(tk );
  //    Segment sH = Segment.extract(tk );
  //
  //    return  new Bundle(originPixel, sL, sH);
  //  }
  //  
  //  public  void plotTrace() {
  //    render(true, true);
  //  }

  private static FPoint2 widen(FPoint2 origin, FPoint2 pt, boolean upper) {
    FPoint2 ret = pt;

    double rad = FPoint2.distance(origin, pt);
    if (rad > 0) {

      ret = MyMath.ptOnCircle( //
          origin,//
          MyMath.polarAngle(origin, pt)
              + (upper ? MyMath.radians(5) : MyMath.radians(-5)), //
          rad);
    }

    return ret;
  }
  /**
   * @param highlight
   */
  public void render(Color color, int stroke, int markType) {
    final boolean db = false;

    if (color == null) color = MyColor.get(MyColor.MAGENTA, .6 );
    
    //    vp V = TestBed.view();

    V.pushColor(color);

    FPoint2 lowerPt = calcViewPt(false, true);
    FPoint2 upperPt = calcViewPt(true, true);

    FPoint2 pixel = Main.grid().toView(BlackBox.fromSweep(origin));

    if (db)
      Streams.out.println("render Bundle, drawing line from " + pixel + " to "
          + lowerPt + " and to " + upperPt);

    V.drawLine(pixel, widen(pixel, lowerPt, false));
    V.drawLine(pixel, widen(pixel, upperPt, true));

    //    if (highlight)
    //      Main.grid().highlightCell(orf);

    V.popColor();
  }

  private FPoint2 calcViewPt(boolean upper, boolean transform) {

    boolean db = false && upper;

    Segment s = upper ? high : low;

    if (db)
      Streams.out.println("calcViewPt,\n seg=" + s.toString(true)
          + "\n s.pt(0)=" + s.pt(0));

    IPoint2 s0 = s.pt(0), s1 = s.pt(1);

    if (transform) {
      s0 = BlackBox.fromSweep(s0);
      s1 = BlackBox.fromSweep(s1);
    }
    FPoint2 w0, w1;

    w0 = Main.grid().toView(s0);
    w1 = Main.grid().toView(s1);

    SweepStrip str = BundleSet.getTraceSweepStrip();
    BlackBox bb = BlackBox.getTransform();
    int pc = bb.lastPixelColumnIntersectingStrip(str.stripX());

    double t = (pc - s.pt(0).x) / (double) (s.pt(1).x - s.pt(0).x);
    FPoint2 ret = FPoint2.interpolate(w0, w1, t);

    return ret;
  }

  // segment at lower boundary of bundle
  private Segment low;
  // segment at upper boundary of bundle
  private Segment high;
  // hot pixel location for start of bundle
  private HotPixel origin;
}
