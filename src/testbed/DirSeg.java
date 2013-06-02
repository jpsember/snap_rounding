package testbed;

import java.awt.*;
import base.*;

public class DirSeg implements Globals, Renderable {

  /**
   * Constructor
   * @param p0 start endpoint
   * @param p1 finish endpoint
   */
  public DirSeg(FPoint2 p0, FPoint2 p1) {
    this(p0.x, p0.y, p1.x, p1.y);
  }
  /**
   * Constructor
   * @param x0
   * @param y0 start endpoint
   * @param x1
   * @param y1 finish endpoint
   */
  public DirSeg(double x0, double y0, double x1, double y1) {
    this.lineEqn = new LineEqn(x0, y0, x1, y1);
    this.ta = lineEqn.parameterFor(x0, y0);
    this.tb = lineEqn.parameterFor(x1, y1);
  }

  /**
   * Calculate parameterized point on segment
   * @param t : parameter
   * @return FPoint2
   */
  public FPoint2 pt(double t) {
    return lineEqn.pt(t);
  }

  /**
   * Get LineEqn of this segment
   * @return LineEqn
   */
  public LineEqn lineEqn() {
    return lineEqn;
  }

  public void render(Color c, int stroke, int markType) {
    V.pushColor(c, MyColor.cRED);
    V.pushStroke(stroke);
    plot(lineEqn.pt(ta), lineEqn.pt(tb));
    V.pop(2);
  }

  /**
   * Get start or finish endpoints
   * @param index : 0 for start, 1 for finish
   * @return FPoint2
   */
  public FPoint2 endPoint(int index) {
    return lineEqn.pt(index == 0 ? ta : tb);
  }

  /**
   * Construct large segment that contains this one
   * @return DirSeg
   */
  public DirSeg extend() {
    return new DirSeg(lineEqn.pt(-1000), lineEqn.pt(1000));
  }

  private LineEqn lineEqn;

  private double ta, tb;

  // ------------------ Utility functions --------------------

  /**
   * Plot a directed line segment, with an arrow at the finish endpoint
   * @param p0 start endpoint
   * @param p1 finish endpoints
   */
  public static void plotDirectedLine(FPoint2 p0, FPoint2 p1) {
    plotDirectedLine(p0, p1, false, true);
  }
  /**
   * Plot a directed line segment, with optional arrowheads
   * @param p0 start endpoint
   * @param p1 finish endpoint
   * @param p0Head if true, draws arrowhead at start
   * @param p1Head if true, draws arrowhead at finish
   */
  public static void plotDirectedLine(FPoint2 p0, FPoint2 p1, boolean p0Head,
      boolean p1Head) {
    V.drawLine(p0, p1);
    double len = p0.distance(p1);
    // draw arrowheads
    if (len > 0) {
      final double AH_LEN = 1.2;
      final double AH_ANG = Math.PI * .85;
      double theta = MyMath.polarAngle(p0, p1);

      for (int h = 0; h < 2; h++) {
        FPoint2 ep = h == 0 ? p0 : p1;
        if ((h == 0 ? p0Head : p1Head)) {
          double th = theta;

          FPoint2 a0 = MyMath.ptOnCircle(ep, th + AH_ANG, AH_LEN);
          V.drawLine(ep, a0);
          FPoint2 a1 = MyMath.ptOnCircle(ep, th - AH_ANG, AH_LEN);
          V.drawLine(ep, a1);
        }
      }
    }
  }

  /**
   * Plot a directed line segment, with dashed line to left side
   * @param p0 start endpoint
   * @param p1 finish endpoint
   */
  public static void plot(FPoint2 p0, FPoint2 p1) {

    double SEP = .2 * V.getScale();
    double ang = MyMath.polarAngle(p0, p1);
    FPoint2 d0 = MyMath.ptOnCircle(p0, ang + Math.PI / 2, SEP);
    FPoint2 d1 = MyMath.ptOnCircle(p1, ang + Math.PI / 2, SEP);

    plotDirectedLine(p0, p1);
    if (false)
      Tools.warn("not drawing rubber band part");
    else {
      V.pushStroke(STRK_RUBBERBAND);
      V.drawLine(d0, d1);
      V.popStroke();
    }
  }

}
