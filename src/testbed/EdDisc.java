package testbed;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import base.*;

public class EdDisc extends EdPoint {
  //  private static final boolean db = false;

  /**
   * Constructor
   * @param center
   * @param radius
   */
  public EdDisc(FPoint2 center, double radius) {
    setCenter(center);
    setRadius(radius);
  }

  public EdDisc(double[] script, int offset) {
    this(script[offset + 0], script[offset + 1], script[offset + 2]);
  }

  public EdDisc(double originX, double originY, double radius) {
    this(new FPoint2(originX, originY), radius);
  }

  public EdDisc(EdDisc edDisc) {
    this(edDisc.getOrigin(), edDisc.getRadius());
    setLabel(edDisc.getLabel());
  }

  public FRect getBounds() {
    FPoint2 origin = this.getOrigin();
    double radius = this.getRadius();
    return new FRect(origin.x - radius, origin.y - radius, radius * 2,
        radius * 2);
  }
  private EdDisc() {
  }
  public static String toString(Collection c) {
    StringBuilder sb = new StringBuilder();
    for (Iterator it = c.iterator(); it.hasNext();) {
      Object obj = it.next();
      if (obj instanceof EdDisc) {
        Tools.addSp(sb);
        sb.append(((EdDisc) obj).getLabel());
      }
    }
    return sb.toString();
  }

  /**
   * Render object within editor.
   * Override this to change highlighting behaviour for points.
   */
  public void render() {

    render(isActive() ? Color.BLUE : Color.gray, -1, -1);
    if (isSelected()) {
      for (int i = 0; i < nPoints(); i++) {
        if (pointMode() && i == 1)
          break;
        if (i == 0 && complete() && !pointMode()) {
          V.pushColor(Color.gray);
          V.mark(getOrigin(), MARK_X);
          V.popColor();
          continue;
        }
        hlSmall(i);
      }
    }

  }

  public static EdDisc isCompletedDisc(EdObject obj) {
    EdDisc ret = null;
    if (obj != null && obj instanceof EdDisc && obj.complete())
      ret = (EdDisc) obj;
    return ret;
  }

  public FPoint2 polarPoint(double ang) {
    return MyMath.ptOnCircle(getOrigin(), ang, getRadius());
  }

  public static int getFirstActive(ObjArray a) {
    int ret = -1;
    for (int i = 0; i < a.size(); i++) {
      EdDisc d = (EdDisc) a.obj(i);
      if (d.isActive()) {
        ret = i;
        break;
      }
    }
    return ret;
  }

  private static final double RAD_OFFSET = 1.5;
  /**
   * Get radius of circle
   * @return radius
   */
  public double getRadius() {
    if (hasFlags(FLG_POINTMODE))
      return 0;
    return this.radius;
  }

  public boolean complete() {
    return nPoints() == 2;
  }

  /**
   * Set transformed location of point.  Default method calls
   * setPoint().  For discs, radius point should be calculated from others.
   * @param ptIndex
   * @param point
   */
  public void setTransformedPoint(int ptIndex, FPoint2 point) {
    if (ptIndex != 1) {
      super.setTransformedPoint(ptIndex, point);
    } else {
      setRadius(radius);
    }
  }

  public void setPoint(int fieldNumber, FPoint2 point, boolean useGrid,
      TBAction action) {

    final boolean db = false;

    if (db)
      Streams.out.println("EdCircle, setPoint " + fieldNumber + " " + point);

    switch (fieldNumber) {
    case 0:
      setCenter(V.snapToGrid(point));
      break;
    case 1:
      {
        double dist = 0;

        if (!pointMode()) {
          double adj = point.y - getOrigin().y - RAD_OFFSET;

          FPoint2 orig = this.getOrigin();

          dist = V.snapToGrid(new FPoint2(orig.x, orig.y + adj)).y - orig.y;
          if (db)
            Streams.out.println("adjust radius, adj=" + adj + " snapped="
                + dist);
          dist = MyMath.clamp(dist, 0, 10000);
        }

        if (db)
          Streams.out.println("set radius to " + dist);

        setRadius(dist);
      }
      break;
    }
    if (db && fieldNumber == 1) {
      Streams.out.println("reading point back=" + this.getPoint(fieldNumber));
    }

  }
  public boolean pointMode() {
    return hasFlags(FLG_POINTMODE);
  }

  /**
   * Move entire object by a displacement
   * Default implementation just adjusts each point.
   * @param orig : a copy of the original object
   * @param delta : amount to move by
   */
  public void moveBy(EdObject orig, FPoint2 delta) {
    setPoint(0, FPoint2.add(orig.getPoint(0), delta, null));
  }

  public double distFrom(FPoint2 pt) {
    double d = FPoint2.distance(pt, getPoint(0));
    d = Math.max(0, d - getRadius());

    if (isSelected())
      d = Math.min(d, FPoint2.distance(pt, getPoint(1)));

    return d;
  }

  public double distFrom(int ptIndex, FPoint2 pt) {

    final boolean db = false;

    if (db)
      Streams.out.println("EdDisc, distFrom ptIndex=" + ptIndex + " pt=" + pt);

    double d = -1;
    switch (ptIndex) {
    case 0:
      d = 10000;
      break;
    case 1:
      {
        FPoint2 rpt = getPoint(ptIndex);
        if (rpt != null)
          d = FPoint2.distance(pt, rpt);
      }
      break;
    }
    if (db)
      Streams.out.println(" d=" + d);
    return d;
  }

  public EdObjectFactory getFactory() {
    return EdDisc.FACTORY;
  }

  private void setCenter(FPoint2 pt) {
    double rad = getRadius();
    super.setPoint(0, new FPoint2(pt), false, null);
    setRadius(rad);
  }

  public void setRadius(double rad) {
    if (pointMode())
      rad = 0;
    FPoint2 center = getOrigin();
    FPoint2 rpt = null;
    rpt = new FPoint2(center.x, center.y + rad + RAD_OFFSET);

    super.setPoint(1, rpt, false, null);

    if (!pointMode())
      this.radius = rad;
  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {

    public EdObject construct() {
      return new EdDisc();
    }

    public String getTag() {
      return "circ";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdDisc, parse, next=" + s.peek().debug());

      s.read(IEditorScript.T_PAROP);
      EdDisc seg = new EdDisc();
      if (db)
        Streams.out.println("constructed disc");

      seg.setFlags(flags);
      if (db)
        Streams.out.println(" flags=" + flags);
      seg.addPoint(s.extractFPoint2());
      if (db)
        Streams.out.println(" added point");

      seg.radius = s.readDouble();
      seg.setRadius(seg.radius);

      if (db)
        Streams.out.println(" set radius");
      if (db)
        Streams.out.println(" now: " + this);

      //      seg.setInactive(s.readIf(false));
      //      if (!s.peek(IEditorScript.T_PARCL)) {
      //        seg.saveRadius = s.readDouble();
      //      }

      s.read(IEditorScript.T_PARCL);
      return seg;
    }

    public void write(StringBuilder sb, EdObject obj) {
      EdDisc d = (EdDisc) obj;

      sb.append('(');

      FPoint2 center = d.getOrigin();
      if (center != null)
        toString(sb, center);
      else
        sb.append(" ? ");

      toString(sb, d.radius);
      sb.append(')');
    }
    //
    //
    //    public void write(StringBuilder sb, EdObject obj) {
    //      EdDisc seg = (EdDisc) obj;
    //      sb.append(seg.toString());
    //    }

    public String getMenuLabel() {
      return "Add disc";
    }
    public String getKeyEquivalent() {
      return "d";
    }

  };

  public static boolean overlap(EdDisc c1, EdDisc c2) {
    return distanceBetweenOrigins(c1, c2) < c1.getRadius() + c2.getRadius();
  }

  public static boolean partiallyDisjoint(EdDisc a, EdDisc b) {
    double sep = FPoint2.distance(a.getOrigin(), b.getOrigin());
    return (sep > Math.abs(a.getRadius() - b.getRadius()));
  }

  public static boolean contains(EdDisc c1, EdDisc c2) {
    return contains(c1.getOrigin(), c1.radius, c2.getOrigin(), c2.radius);
    //    return distanceBetweenOrigins(c1, c2) <= c1.getRadius() - c2.getRadius();
  }

  public static boolean contains(FPoint2 o1, double r1, FPoint2 o2, double r2) {
    return FPoint2.distance(o1, o2) <= r1 - r2;
  }
  public static boolean contains(FPoint2 origin, double radius2, FPoint2 im) {
    return FPoint2.distance(origin, im) <= radius2;
  }

  public static boolean encloses(EdDisc c1, EdDisc c2) {
    return encloses(c1.getOrigin(), c1.radius, c2.getOrigin(), c2.radius);
  }

  public static boolean encloses(EdDisc c1, FPoint2 q) {
    return c1.getOrigin().distance(q) < c1.getRadius();
  }

  public static double distanceBetweenOrigins(EdDisc c1, EdDisc c2) {
    return FPoint2.distance(c1.getOrigin(), c2.getOrigin());
  }
  private static final int FLG_POINTMODE = 1 << (USER_FLAG_BITS - 1);
  public static final int USER_FLAG_BITS_DISC = (USER_FLAG_BITS - 1);

  //  public static boolean plotThin;

  public void togglePointMode() {
    this.toggleFlags(FLG_POINTMODE);
    setRadius(pointMode() ? 0 : radius);
  }

  public static FPoint2[] lineBetween(EdDisc a, EdDisc b, boolean clipToExterior) {
    FPoint2[] ret = new FPoint2[2];

    FPoint2 c0 = a.getOrigin();
    FPoint2 c1 = b.getOrigin();
    if (clipToExterior) {
      if (overlap(a, b))
        return null;
      // move endpoints to circle boundaries
      c0 = MyMath.ptOnCircle(c0, MyMath.polarAngle(b.getOrigin(), c0),
      //          FPoint2.difference(b.getOrigin(), c0, null),
          a.getRadius());
      c1 = MyMath.ptOnCircle(c1, MyMath.polarAngle(a.getOrigin(), c1),
      //FPoint2.difference(a.getOrigin(), c1, null), 
          b.getRadius());
    }
    ret[0] = c0;
    ret[1] = c1;
    return ret;
  }

  public static FPoint2 nearestPointToOrigin(EdDisc ds, EdDisc dk) {
    double diff = dk.getOrigin().distance(ds.getOrigin()) - dk.getRadius();
    if (diff < 0)
      diff = 0;

    return MyMath.ptOnCircle(ds.getOrigin(), MyMath.polarAngle(ds.getOrigin(),
        dk.getOrigin()), diff);
  }

  public void render(Color c, int stroke, int markType) {

    final boolean special = true; //Tools.dbWarn(true);
    if (c == null)
      c = MyColor.cRED;
    FPoint2 co = getOrigin();
    double rad = 0;

    if (special && !isActive()) {
      markType = MARK_DISC;

      c = MyColor.cRED;

      V.pushColor(c);
      V.pushStroke(stroke, STRK_RUBBERBAND); //  : STRK_NORMAL);
      V.mark(co, markType);

      if (co != null) {
        rad = getRadius();
        if (rad < V.getScale() * .4) {
        } else
          V.drawCircle(co, rad);
      }
    } else {

      if (markType < 0 && !isActive())
        markType = MARK_X;

      V.pushColor(c);
      V.pushStroke(stroke, hasFlags(FLAG_PLOTDASHED) ? STRK_RUBBERBAND
          : STRK_NORMAL);
      if (markType >= 0)
        V.mark(getOrigin(), markType);

      if (co != null) {
        rad = getRadius();
        if (rad < V.getScale() * .4)
          V.fillCircle(co, V.getScale() * .4);
        else
          V.drawCircle(co, rad);
      }
    }

    if (isSelected()) {
      V.pushStroke(STRK_THIN);
      for (int i = 0; i < nPoints(); i++) {

        if (pointMode()) {
          if (i == 1)
            break;
        } else {
          if (i == 0) {
            V.pushColor(MyColor.get(MyColor.RED));
            V.drawCircle(getPoint(i), getRadius() + .2 * V.getScale());
            V.popColor();
            continue;
          }
        }
        hlSmall(i);
      }
      V.popStroke();
    }

    V.popStroke();

    if (complete()) {
      if (Editor.withLabels(false))
        plotLabel(MyMath.ptOnCircle(co, Math.PI * .2, rad + 1.2));
    }

    V.popColor();

  }
  /**
   * Calculate the circumcenter of three points.
   * @param a first point
   * @param b second point
   * @param c third point
   */
  public static EdDisc calcCircumCenter(FPoint2 a, FPoint2 b, FPoint2 c) {

    double rad = 0;
    FPoint2 dest = new FPoint2();

    if (Math.abs(b.x - a.x) > Math.abs(b.y - a.y)) {
      dest.y = (((c.x * c.x - a.x * a.x) + (c.y * c.y - a.y * a.y))
          * (b.x - a.x) + (a.x - c.x)
          * ((b.y * b.y - a.y * a.y) + (b.x * b.x - a.x * a.x)))
          / (2 * (c.y - a.y) * (b.x - a.x) + 2 * (c.x - a.x) * (a.y - b.y));
      dest.x = ((b.y * b.y - a.y * a.y) + (b.x * b.x - a.x * a.x) + 2
          * (a.y - b.y) * dest.y)
          / (2 * (b.x - a.x));
    } else {
      dest.x = (((c.y * c.y - a.y * a.y) + (c.x * c.x - a.x * a.x))
          * (b.y - a.y) + (a.y - c.y)
          * ((b.x * b.x - a.x * a.x) + (b.y * b.y - a.y * a.y)))
          / (2 * (c.x - a.x) * (b.y - a.y) + 2 * (c.y - a.y) * (a.x - b.x));
      dest.y = ((b.x * b.x - a.x * a.x) + (b.y * b.y - a.y * a.y) + 2
          * (a.x - b.x) * dest.x)
          / (2 * (b.y - a.y));
    }
    rad = FPoint2.distance(a, dest);
    return new EdDisc(dest, rad);
  }

  /**
   * Calculate the circumcenter of three points.
   * @param ax ay bx by cx cy : locations of three points
   * @param ret : array of at least three doubles, to hold the
   *   output: x,y,radius
   */
  public static void calcCircumCenter(double ax, double ay, double bx,
      double by, double cx, double cy, double[] ret) {

    double dx, dy;
    if (Math.abs(bx - ax) > Math.abs(by - ay)) {
      dy = (((cx * cx - ax * ax) + (cy * cy - ay * ay)) * (bx - ax) + (ax - cx)
          * ((by * by - ay * ay) + (bx * bx - ax * ax)))
          / (2 * (cy - ay) * (bx - ax) + 2 * (cx - ax) * (ay - by));
      dx = ((by * by - ay * ay) + (bx * bx - ax * ax) + 2 * (ay - by) * dy)
          / (2 * (bx - ax));
    } else {
      dx = (((cy * cy - ay * ay) + (cx * cx - ax * ax)) * (by - ay) + (ay - cy)
          * ((bx * bx - ax * ax) + (by * by - ay * ay)))
          / (2 * (cx - ax) * (by - ay) + 2 * (cy - ay) * (ax - bx));
      dy = ((bx * bx - ax * ax) + (by * by - ay * ay) + 2 * (ax - bx) * dx)
          / (2 * (by - ay));
    }
    //    if (dest != null)
    //      dest.setLocation(dx, dy);

    double rad = FPoint2.distance(ax, ay, dx, dy);
    ret[0] = dx;
    ret[1] = dy;
    ret[2] = rad;
    //    return new EdDisc(dx, dy, rad);
  }

  /**
   * Calculate the circumcenter of three points.
   * @param ax 
   * @param ay
   * @param bx
   * @param by
   * @param cx 
   * @param cy the three points
   * @return smallest EdDisc containing the three points
   */
  public static EdDisc calcCircumCenter(double ax, double ay, double bx,
      double by, double cx, double cy) {

    double[] ret = new double[3];
    calcCircumCenter(ax, ay, bx, by, cx, cy, ret);
    return new EdDisc(ret[0], ret[1], ret[2]);
  }

  public boolean contains(FPoint2 pt) {
    return contains(pt.x, pt.y);
  }

  public boolean contains(double x, double y) {
    FPoint2 pt = getOrigin();
    return contains(pt.x, pt.y, getRadius(), x, y);
  }

  public boolean contains(EdDisc pt) {
    double ds = getOrigin().distance(pt.getOrigin()) + pt.getRadius();

    double r = getRadius();
    return (ds * ds < r * r + 1e-8);
  }

  public Shape arc(double a0, double extent, double rAdj) {
    FPoint2 origin = getOrigin();
    double adjustedRad = this.radius + rAdj;

    double d0 = MyMath.degrees(a0);
    double d1 = MyMath.degrees(extent);

    // hmm...
    d0 = 360 - (d0 + d1);

    return new Arc2D.Double(origin.x - adjustedRad, //
        origin.y - adjustedRad, //
        2 * adjustedRad, 2 * adjustedRad, d0, d1, Arc2D.OPEN);
  }

  public static void main(String[] args) {
    double[] c = { 2, 3, 4, ///
        1, 3, 4, 1, 3, 4.1, 1, 3, 2, 1, 3, 1.9, };

    for (int i = 0; i < c.length;) {
      double r1 = c[i++];
      double r2 = c[i++];
      double x2 = c[i++];
      FPoint2 o1 = new FPoint2(0, 0);
      FPoint2 o2 = new FPoint2(x2, 0);
      DArray a = circleIntersections(o1, r1, o2, r2);
      Streams.out.println("circle " + o1 + " r=" + r1 + "  " + o2 + " r2=" + r2
          + "\n  " + a.toString(true));
    }

  }

  public static DArray circleIntersections(EdDisc a, EdDisc b) {
    return circleIntersections(a.getOrigin(), a.radius, b.getOrigin(), b.radius);
  }

  /**
   * Calculate all points of intersection between distinct circle boundaries
   * @param o1 
   * @param r1 origin and radius of first circle
   * @param o2
   * @param r2 origin and radius of second circle
   * @return array of angles of rotation around origin of o1
   */
  public static DArray circleIntersections(FPoint2 o1, double r1, FPoint2 o2,
      double r2) {
    
    final double EPS = 1e-8;
    
    DArray r = new DArray();
    do {
      double xtheta = MyMath.polarAngle(o1, o2);
      double d = FPoint2.distance(o1, o2);
      if (d == 0)
        break;
      double x = (d * d - r2 * r2 + r1 * r1) / (2 * d);

      if (Math.abs(x - r1) < EPS)
        x = r1;
      if (Math.abs(x - -r1) < EPS)
        x = -r1;

      if (x > r1 || x < -r1)
        break;
      double y = Math.sqrt(r1 * r1 - x * x);
      double th2 = Math.atan2(y, x);
      r.addDouble(xtheta - th2);
      if (x != r1)
        r.addDouble(xtheta + th2);
    } while (false);
    return r;
  }

  public static boolean contains(double ox, double oy, double radius, double x,
      double y) {
    double ds = FPoint2.distanceSq(ox, oy, x, y);
    return (ds < radius * radius + 1e-8);
  }

  /**
   * Scale object.
   * Default implementation just scales all the object's points.
   * @param factor  scaling factor
   */
  public void scale(double factor) {
    FPoint2 center = this.getOrigin();
    double rad = this.getRadius();
    scalePoint(center, factor);
    //    center.scale(factor);
    setPoint(0, center);
    setRadius(rad * factor);
  }

  private double radius;
  public static boolean encloses(FPoint2 origin1, double radius1,
      FPoint2 origin2, double radius2) {
    double distOr = FPoint2.distance(origin1, origin2);
    return distOr < radius1 - radius2;
  }

}
