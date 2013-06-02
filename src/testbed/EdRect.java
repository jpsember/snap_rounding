package testbed;

import base.*;
import java.awt.*;
import java.awt.geom.*;
import testbed.*;

public class EdRect extends EdObject implements Globals {
  public static final int FLG_SQUARE = (1 << (USER_FLAG_BITS - 1));

  static final boolean db = false;

  @Override
  public FRect getBounds() {
    return new FRect(getPoint(0), getPoint(4));
  }

  public boolean isSquare() {
    return hasFlags(FLG_SQUARE);
  }

  private static FPoint2 closestPointTo(FPoint2 p0, double dx, double dy,
      FPoint2 s) {
    FPoint2 clPt = new FPoint2();
    MyMath.ptDistanceToLine(s, p0, new FPoint2(p0.x + dx, p0.y + dy), clPt);
    return clPt;
  }

  /**
   * Set point, with optional snapping to grid
   * @param p index of point
   * @param point new location of point
   * @param useGrid if true, snaps to grid (if one is active)
   * @param action if not null, action that caused this edit
   */
  public void setPoint(int p, FPoint2 point, boolean useGrid, TBAction action) {

    final boolean db = false;

    if (db)
      Streams.out.println("setPoint #" + p + " to " + point);

    if (!useGrid)
      point = new FPoint2(point);
    else
      point = V.snapToGrid(point);

    if (!complete())
      init(point, point);

    double px = point.x;
    double py = point.y;

    if (isSquare()) {
      FRect bounds = getBounds();
      if (p == 3 || p == 7)
        py = bounds.midPoint().y;
      if (p == 1 || p == 7)
        px = bounds.midPoint().x;

      if (p == 0 || p == 4) {
        point = closestPointTo(bounds.bottomLeft(), 1, 1, point);
        px = point.x;
        py = point.y;
        if (db)
          Streams.out.println(" closest point is " + point);
      }
      if (p == 2 || p == 6) {
        point = closestPointTo(bounds.bottomRight(), 1, -1, point);
        px = point.x;
        py = point.y;
        if (db)
          Streams.out.println(" closest point is " + point);
      }
      if (isSide(p)) {
        Tools.unimp("editing sides of square");
        return;
      }
    }

    if (db)
      Streams.out.println("set #" + p + " to " + px + "," + py);

    if (p == 0 || p == 1 || p == 2)
      setY0(py);
    if (p == 4 || p == 5 || p == 6)
      setY1(py);
    if (p == 0 || p == 6 || p == 7)
      setX0(px);
    if (p == 2 || p == 3 || p == 4)
      setX1(px);
    setMidPoints();
  }

  //  public double width() {
  //    return getPoint(4).x - getPoint(0).x;
  //  }
  //  public double height() {
  //    return getPoint(4).y - getPoint(0).y;
  //  }
  //  public FPoint2 size() {
  //    FPoint2 p4 = getPoint(4);
  //    FPoint2 p0 = getPoint(0);
  //    return new FPoint2(p4.x - p0.x, p4.y - p0.y);
  //  }

  @Override
  public void moveBy(EdObject orig, FPoint2 delta) {
    EdRect ro = (EdRect) orig;
    FRect r = ro.getBounds();

    FPoint2 n0 = FPoint2.add(orig.getPoint(0), delta, null);
    n0 = V.snapToGrid(n0);

    //    FPoint2 size = ro.size();
    FPoint2 tr = FPoint2.add(n0, r.size(), null);
    tr = V.snapToGrid(tr);
    init(n0.x, n0.y, tr.x, tr.y);
  }

  private void setY0(double n) {
    DArray p = getPts();
    p.getFPoint2(0).y = n;
    p.getFPoint2(2).y = n;
  }

  private void setMidPoints() {
    DArray p = getPts();
    FPoint2 p0 = p.getFPoint2(0);
    FPoint2 p4 = p.getFPoint2(4);

    double xm = (p0.x + p4.x) * .5;
    double ym = (p0.y + p4.y) * .5;

    p.getFPoint2(1).setLocation(xm, p0.y);
    p.getFPoint2(3).setLocation(p4.x, ym);
    p.getFPoint2(5).setLocation(xm, p4.y);
    p.getFPoint2(7).setLocation(p0.x, ym);
  }

  private void setY1(double n) {
    DArray p = getPts();
    p.getFPoint2(4).y = n;
    p.getFPoint2(6).y = n;

  }
  private void setX0(double n) {
    DArray p = getPts();
    p.getFPoint2(0).x = n;
    p.getFPoint2(6).x = n;
  }
  private void setX1(double n) {
    DArray p = getPts();
    p.getFPoint2(2).x = n;
    p.getFPoint2(4).x = n;
  }
  private void init(FPoint2 p0, FPoint2 p1) {
    init(p0.x, p0.y, p1.x, p1.y);
  }

  private void init(double x0, double y0, double x1, double y1) {

    if (x0 > x1) {
      double tmp = x0;
      x0 = x1;
      x1 = tmp;
    }
    if (y0 > y1) {
      double tmp = y0;
      y0 = y1;
      y1 = tmp;
    }
    DArray pts = this.getPts();
    pts.clear();

    //    double x0 = p0.x, y0 = p0.y, x1 = p1.x, y1 = p1.y;
    double xm = (x0 + x1) * .5;
    double ym = (y0 + y1) * .5;
    addPt(x0, y0);
    addPt(xm, y0);
    addPt(x1, y0);
    addPt(x1, ym);
    addPt(x1, y1);
    addPt(xm, y1);
    addPt(x0, y1);
    addPt(x0, ym);
  }
  private void addPt(double x, double y) {
    DArray pts = this.getPts();
    pts.add(new FPoint2(x, y));
  }
  private EdRect(double x0, double y0, double x1, double y1) {
    init(x0, y0, x1, y1);
  }
  private EdRect(FPoint2 bottomLeft, FPoint2 topRight) {
    init(bottomLeft.x, bottomLeft.y, topRight.x, topRight.y);
  }
  private EdRect() {
  }
  public boolean complete() {
    return nPoints() == 8;
  }
  public EdRect(FRect r) {
    init(r.topLeft(), r.bottomRight());
  }

  private static double ptDistanceToRect(FPoint2 pt, FRect r) {
    double cx = r.x + r.width / 2;
    double cy = r.y + r.height / 2;
    double dx = Math.max(Math.abs(cx - pt.x) - r.width / 2, 0);
    double dy = Math.max(Math.abs(cy - pt.y) - r.height / 2, 0);
    return Math.sqrt(dx * dx + dy * dy);
  }

  public double distFrom(FPoint2 pt) {
    FRect r = getBounds();
    return ptDistanceToRect(pt, r);
  }

  public double distFrom(int ptIndex, FPoint2 pt) {
    double ret;
    if (isSquare() && isSide(ptIndex))
      ret = 100;
    else
      ret = super.distFrom(ptIndex, pt);
    return ret;
  }

  public EdObjectFactory getFactory() {
    return FACTORY;
  }

  @Override
  public void render() {
    render(null, -1, -1);
    if (isSelected()) {
      for (int i = 0; i < nPoints(); i++) {
        if (isSquare() && isSide(i))
          continue;
        hlSmall(i);
      }
    }
  }
  public static boolean isSide(int ptIndex) {
    return (ptIndex < 8 && (ptIndex & 1) == 1);
  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {
    public EdObject construct() {
      return new EdRect();
    }

    public String getTag() {
      return "rect";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdRect, parse, next=" + s.peek().debug());

      FPoint2 p0 = s.extractFPoint2();
      FPoint2 p1 = s.extractFPoint2();

      EdRect seg = new EdRect(p0, p1);
      seg.setFlags(flags);

      return seg;
    }

    public void write(StringBuilder sb, EdObject obj) {
      toString(sb, obj.getPoint(0));
      toString(sb, obj.getPoint(4));
    }

    public String getMenuLabel() {
      return "Add rectangle";
    }
    public String getKeyEquivalent() {
      return "r";
    }

  };

  public void render(Color color, int stroke, int markType) {
    V.pushColor(color, isActive() ? Color.BLUE : Color.GRAY);
    V.pushStroke(stroke, STRK_THIN);
    if (complete()) {
      FRect r = this.getBounds();
      V.drawRect(r);
    }
    V.pop(2);
  }

  public void scale(double factor) {
    FPoint2 p0 = getPoint(0);
    FPoint2 p4 = getPoint(4);
    scalePoint(p0, factor);
    scalePoint(p4, factor);
    init(p0.x, p0.y, p4.x, p4.y);

  }

  public static EdRect square(double x, double y, double size) {
    EdRect r = new EdRect(x, y, x + size, y + size);
    r.addFlags(FLG_SQUARE);
    return r;
  }

  /**
   * Determine if two rectangles overlap
   * @param r0
   * @param r1
   * @return true if they overlap
   */
  public static boolean overlap(EdRect r0, EdRect r1) {
    boolean ret = false;
    do {
      FRect b0 = r0.getBounds();
      FRect b1 = r1.getBounds();
      if (b0.x + b0.width <= b1.x || b0.x >= b1.x + b1.width)
        break;
      if (b0.y + b0.height <= b1.y || b0.y >= b1.y + b1.height)
        break;
      ret = true;
    } while (false);
    return ret;
  }

}
