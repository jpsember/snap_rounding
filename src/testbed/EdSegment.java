package testbed;

import base.*;
import java.awt.*;
import java.awt.geom.*;

public class EdSegment extends EdObject implements Globals {

  static final boolean db = false;

  public EdSegment() {
  }

  public EdSegment(double[] p1) {
    for (int i = 0; i < p1.length; i += 2)
      setPoint(i / 2, new FPoint2(p1[i + 0], p1[i + 1]), false, null);
  }

  public EdSegment(FPoint2 p1, FPoint2 p2) {
    setPoint(0, p1, false, null);
    setPoint(1, p2, false, null);
  }
  public EdSegment(double x0, double y0, double x1, double y1) {
    this(new FPoint2(x0, y0), new FPoint2(x1, y1));
  }

  public boolean complete() {
    return nPoints() >= 2;
  }
  public double distFrom(FPoint2 pt) {
    FPoint2 p1 = getPoint(0);
    FPoint2 p2 = getPoint(1);
    return MyMath.ptDistanceToSegment(pt, p1, p2, null);
  }

  public EdObjectFactory getFactory() {
    return FACTORY;
  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {
    public EdObject construct() {
      return new EdSegment();
    }

    public String getTag() {
      return "seg";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdSegment, parse, next=" + s.peek().debug());

      EdSegment seg = new EdSegment();
      seg.setFlags(flags);
      for (int i = 0; i < 2; i++)
        seg.addPoint(s.extractFPoint2());

      return seg;
    }

    public void write(StringBuilder sb, EdObject obj) {
      EdSegment d = (EdSegment) obj;
      for (int i = 0; i < 2; i++)
        toString(sb, d.getPoint(i));
    }

    public String getMenuLabel() {
      return "Add segment";
    }
    public String getKeyEquivalent() {
      return "s";
    }

  };

  //  private static final Color SEGMENT_COLOR = MyColor.cBLUE; //new Color(0xff, 0x00, 0xcc);
  private static FPoint2 zero = new FPoint2();
  public void render(Color color, int stroke, int markType) {
    V.pushColor(color, isActive() ? Color.BLUE : Color.GRAY);
    //    V.pushColor(color, SEGMENT_COLOR);
    V.pushStroke(stroke);

    FPoint2 prev = null;
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 pt = getPoint(i);
      if (prev != null)
        V.drawLine(prev, pt);
      if (markType >= 0)
        V.mark(pt, markType);
      prev = pt;
    }
    V.pop();
    if (complete()) {
      FPoint2 p0 = getPoint(0);
      FPoint2 p1 = getPoint(1);
      double theta = MyMath.polarAngle(p0, p1);
      if (Editor.withLabels(true)) {
        V.pushScale(.6);
        FPoint2 offset = MyMath.ptOnCircle(zero, theta, V.getScale() * 1.5);
        V.draw("0", FPoint2.add(p0, offset, null), TX_FRAME | TX_BGND);
        V.draw("1", FPoint2.add(p1, offset, null), TX_FRAME | TX_BGND);
        V.pop();
      }
      if (Editor.withLabels(false)) {
        FPoint2 cp = FPoint2.midPoint(p0, p1);
        plotLabel(MyMath.ptOnCircle(cp, theta - Math.PI / 2, V.getScale()));
      }
    }
    V.pop();
  }

  /**
   * Construct and show a segment from a pair of endpoints
   * @param x0
   * @param y0
   * @param x1
   * @param y1
   * @param c  color to display with, or null for default
   * @param stroke  if >= 0, stroke to use; else, default
   * @return empty string
   */
  public static String show(double x0, double y0, double x1, double y1,
      Color c, int stroke) {
    return show(x0, y0, x1, y1, c, stroke, 0);
  }

  private static String show(double x0, double y0, double x1, double y1,
      Color c, int stroke, int arrowFlags) {
    return T.show(new MiscLine(x0, y0, x1, y1, c, stroke, arrowFlags));
  }

  public static String showDirected(FPoint2 p0, FPoint2 p1, Color c, int stroke) {
    if (p0 == null || p1 == null)
      return "";
    return show(p0.x, p0.y, p1.x, p1.y, c, stroke, (1 << 0));
  }
  public static String showDirected(FPoint2 p0, FPoint2 p1) {
    return showDirected(p0, p1, null, -1);
  }

  public static String show(FPoint2 p0, FPoint2 p1, Color c, int stroke) {
    return show(p0.x, p0.y, p1.x, p1.y, c, stroke);
  }
  public static String show(FPoint2 p0, FPoint2 p1, Color c) {
    return show(p0.x, p0.y, p1.x, p1.y, c, -1);
  }
  public static String show(FPoint2 p0, FPoint2 p1) {
    return show(p0, p1, Color.red, -1);
  }

  private static class MiscLine implements Renderable {
    public MiscLine(double x0, double y0, double x1, double y1, Color c,
        int stroke, int arrowFlags) {
      this.pt0 = new FPoint2(x0, y0);
      this.pt1 = new FPoint2(x1, y1);
      if (c == null)
        c = Color.red;
      if (stroke < 0)
        stroke = V.STRK_NORMAL;

      this.c = c;
      this.stroke = stroke;
      this.arrowFlags = arrowFlags;
    }
    private int arrowFlags;
    private FPoint2 pt0, pt1;
    private Color c;
    private int stroke;
    public void render(Color c, int stroke, int markType) {
      if (stroke < 0)
        stroke = this.stroke;
      V.pushStroke(stroke);
      if (c == null)
        c = this.c;
      V.pushColor(c);
      V.drawLine(pt0, pt1);
      for (int i = 0; i < 2; i++) {
        FPoint2 pt = i == 1 ? pt0 : pt1;
        FPoint2 pt2 = (i == 1) ? pt1 : pt0;

        if ((arrowFlags & (1 << i)) != 0) {
          plotArrowHead(pt, MyMath.polarAngle(pt2, pt));
        } else {
          if (markType >= 0) {
            V.mark(pt, markType);
          }
        }
        //        
        //        
        //      if (markType >= 0) {
        //        V.mark(pt0, markType);
        //        V.mark(pt1, markType);
      }
      V.popColor();
      V.popStroke();
    }
  };
  public static void plotDirectedLine(FPoint2 p0, FPoint2 p1) {
    plotDirectedLine(p0, p1, false, true);
  }
  public static void plotDirectedLine(FPoint2 p0, FPoint2 p1, boolean p0Head,
      boolean p1Head) {
    V.drawLine(p0, p1);
    double len = p0.distance(p1);
    // draw arrowheads
    if (len > 0) {
      double theta = MyMath.polarAngle(p0, p1);

      for (int h = 0; h < 2; h++) {
        FPoint2 ep = h == 0 ? p0 : p1;
        if ((h == 0 ? p0Head : p1Head)) {
          plotArrowHead(ep, theta);
        }
      }
    }
  }

  public static void plotArrowHead(FPoint2 pt, double theta) {
    final double AH_LEN = 1.2;
    final double AH_ANG = Math.PI * .85;
    double th = theta;

    FPoint2 a0 = MyMath.ptOnCircle(pt, th + AH_ANG, AH_LEN);
    V.drawLine(pt, a0);
    FPoint2 a1 = MyMath.ptOnCircle(pt, th - AH_ANG, AH_LEN);
    V.drawLine(pt, a1);
  }

  public void renderTo(Graphics2D g) {
    Line2D.Double wl = new Line2D.Double();
    wl.setLine(getPoint(0), getPoint(1));
    g.draw(wl);
  }

}
