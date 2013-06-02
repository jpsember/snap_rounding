package testbed;

import base.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class EdPolygon extends EdObject implements IEditorScript, Globals {

  //  private static final boolean NEWEDIT = true;

  public static final int FLG_OPEN = (1 << (USER_FLAG_BITS - 1));
  //  public static final int FLG_LBLVERT = (1 << (USER_FLAG_BITS - 2));

  //  public static void setLabelVerts(boolean f) {
  //    labelVerts = f;
  //  }
  //  private static boolean labelVerts;

  /**
   * @deprecated use getConvexHull
   */
  public EdPolygon calcHull() {
    DArray p = new DArray();
    for (int i = 0; i < nPoints(); i++)
      p.add(this.getPoint(i));
    EdPolygon ret = calcHull(p);
    ret.setLabel(this.getLabel());
    return ret;
  }

  public static String show(Collection points, Color color, int stroke,
      int markType) {
    if (color == null)
      color = MyColor.cRED;
    if (markType < 0)
      markType = MARK_DISC;

    EdPolygon p = new EdPolygon(points);
    return T.show(p, color, stroke, markType);
  }
  public static String show(Collection points, Color color, int stroke) {
    return show(points, color, stroke, -1);
  }
  public static String show(Collection points, Color color) {
    return show(points, color, -1, -1);
  }
  public static String show(Collection points) {
    return show(points, null, -1, -1);
  }

  /**
   * Construct an open polygon representing a path of points
   * @param verts list of points
   * @return Renderable, or null if no points
   */
  public static Renderable constructPath(DArray verts) {
    Renderable r = null;
    switch (verts.size()) {
    case 0:
      break;
    case 1:
      r = new EdPoint(verts.getFPoint2(0));
      break;
    case 2:
      r = new EdSegment(verts.getFPoint2(0), verts.getFPoint2(1));
      break;
    default:
      {
        EdPolygon p = new EdPolygon(verts);
        p.addFlags(EdPolygon.FLG_OPEN);
        r = p;
      }
      break;
    }
    return r;
  }

  /**
   * Calculate convex hull.  Assumes polygon is simple.
   * @param c
   * @return
   */
  public static EdPolygon calcHull(DArray c) {
    DArray h = MyMath.convexHullOfPoly(c);
    EdPolygon ret = new EdPolygon();
    for (int i = 0; i < h.size(); i++)
      ret.addPoint(c.getFPoint2(h.getInt(i)));
    return ret;
  }

  public void setPointMod(int i, FPoint2 q1) {
    setPoint(MyMath.mod(i, nPoints()), q1);
  }

  /**
   * Clip polygon to left side of line
   * @param s0 : start point
   * @param s1 : end point
   * @return clipped polygon, or null if it has been clipped away
   */
  public EdPolygon clipTo(FPoint2 s0, FPoint2 s1) {

    final boolean db = false;

    if (db && T.update())
      T.msg("EdPolygon clipTo," + T.show(this) + " s0=" + s0 + " s1=" + s1
          + EdSegment.show(s0, s1));

    EdPolygon c = new EdPolygon();

    FPoint2 prevPt = null;
    boolean prevInside = false;
    for (int i = 0; i <= nPoints(); i++) {
      FPoint2 pt = getPointMod(i);
      boolean inside = MyMath.sideOfLine(s0, s1, pt) >= 0;

      if (db && T.update())
        T.msg(EdSegment.show(s0, s1) + " pt #" + i + T.show(pt) + " is inside="
            + inside + " prevPt=" + prevPt + T.show(prevPt, MyColor.cDARKGREEN)
            + " prevInside=" + prevInside);

      if (i > 0) {
        if (!prevInside) {
          if (inside) {
            FPoint2 ept = MyMath.linesIntersection(s0, s1, prevPt, pt, null);

            if (ept != null) {
              // add point where it enters poly
              if (db && T.update())
                T.msg(EdSegment.show(s0, s1) + "adding enter point"
                    + T.show(ept));
              c.addPoint(ept, false);
            }
            // add the current point, which is inside
            if (db && T.update())
              T.msg(EdSegment.show(s0, s1) + "adding current point, inside;"
                  + T.show(pt));
            c.addPoint(pt, false);
          }
        } else {
          if (!inside) {
            // add point where it exits poly
            FPoint2 ept = MyMath.linesIntersection(s0, s1, prevPt, pt, null);
            if (ept != null) {
              if (db && T.update())
                T.msg(EdSegment.show(s0, s1) + "adding exit point"
                    + T.show(ept));
              c.addPoint(ept, false);
            }
          } else {
            if (db && T.update())
              T.msg(EdSegment.show(s0, s1) + "adding current point, inside;"
                  + T.show(pt));
            c.addPoint(pt, false);
          }
        }
      }
      prevInside = inside;
      prevPt = pt;
    }
    if (db && T.update())
      T.msg(EdSegment.show(s0, s1) + "clipped" + T.show(c));

    if (false) {
      c.filterCollinear(1e-2);
      if (db && T.update())
        T.msg(EdSegment.show(s0, s1) + "after filtering" + T.show(c));
    }
    EdPolygon p = null;

    if (c.nPoints() >= 3) {
      p = new EdPolygon();
      p.copyPointsFrom(c);
    }
    return p;
  }

  /**
   * Test if polygon is convex
   * @return index of first non-convex vertex, or -1 if convex
   */
  public int testConvexity() {
    final boolean db = false;

    int ret = -1;
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 p0 = getPointMod(i - 1);
      FPoint2 p1 = getPointMod(i);
      FPoint2 p2 = getPointMod(i + 1);
      double dist = MyMath.sideOfLine(p0, p2, p1);
      if (db)
        Streams.out.println("i=" + i + " p0=" + p0 + " p1=" + p1 + " p2=" + p2
            + " dist=" + dist);

      if (dist >= 0) {
        ret = i;
        break;
      }
    }
    return ret;
  }

  static final boolean db = false;

  public EdPolygon() {

  }

  private EdPolygon convexHull;

  /**
   * Clear any current convex hull for this polygon
   * (i.e., in case polygon has been edited in some way).
   */
  public void resetHull() {
    convexHull = null;
  }

  /**
   * Get convex hull of polygon.  It is lazy-initialized, so it is constructed
   * if necessary and retained for subsequent calls.  
   * @return array of FPoint2s
   */
  public EdPolygon getConvexHull() {
    if (convexHull == null) {
      convexHull = calcHull(this.getPts());
      convexHull.convexHull = convexHull;
    }
    return convexHull;
  }

  public EdPolygon(double[] p1) {
    for (int i = 0; i < p1.length; i += 2)
      setPoint(i / 2, new FPoint2(p1[i + 0], p1[i + 1]));
  }

  public EdPolygon(Collection ptArray) {
    int i = 0;
    for (Iterator it = ptArray.iterator(); it.hasNext(); i++)
      setPoint(i, (FPoint2) it.next());
  }
  public EdPolygon(FPoint2[] ptArray) {
    for (int i = 0; i < ptArray.length; i++)
      setPoint(i, ptArray[i]);
  }

  public EdPolygon(EdPolygon p) {
    this(p.getPts());
    setLabel(p.getLabel());
  }

  public boolean complete() {
    return nPoints() >= 3;
  }

  @Override
  public int getNextPointToInsert(TBAction a, int ptIndex, FPoint2 drift) {

    FPoint2 prevPt = getPoint(ptIndex);

    int newPtIndex = -2;
    int offset = 1;

    if (!a.ctrlPressed()) {
      drift = null;
    }

    if (drift == null || drift.length() > .5) {

      if (drift != null) {
        if (nPoints() >= 2) {
          FPoint2 prevPt2 = getPointMod(ptIndex - 1);
          double a0 = MyMath.polarAngle(prevPt2, prevPt) - Math.PI / 2;
          double a1 = MyMath.polarAngle(drift);
          double diff = MyMath.normalizeAngle(a1 - a0);
          if (diff < 0)
            offset = 0;
        }
      }
      newPtIndex = ptIndex + offset;
      FPoint2 nextPt = prevPt;
      addPoint(newPtIndex, nextPt);
    }
    return newPtIndex;
  }
  public int nSides() {
    int i = nPoints();
    return (i <= 1 ? 0 : i);
  }

  public FPoint2 sideStart(int si) {
    return getPoint(MyMath.mod(si, nPoints()));
  }

  public FPoint2 sideEnd(int si) {
    return getPoint(MyMath.mod(si + 1, nPoints()));
  }

  //  /**
  //   * Determine if point is inside polygon
  //   * @param pt point to test
  //   * @return true if point is on or inside polygon
  //   * @deprecated
  //   */
  //  public boolean pointInside(FPoint2 pt) {
  //
  //    final boolean db = true;
  //
  //    if (db && T.update())
  //      T.msg("isPointInside?" + T.show(this, MyColor.cRED) + T.show(pt));
  //    boolean ret = false;
  //
  //    // if the sum of the angles swept by seg from pt to each vertex is
  //    // +/- 360 degrees, we're inside.
  //
  //    double sum = 0;
  //    double prevTheta = 0;
  //    boolean prevThetaDefined = false;
  //    for (int i = 0; i <= nSides(); i++) {
  //      FPoint2 v = getPointMod(i);
  //      //      if (true)
  //      //        v = getPointMod(-i);
  //      if (v.equals(pt)) {
  //        if (db && T.update())
  //          T.msg("vertex equals query point" + EdSegment.showDirected(pt, v));
  //        ret = true;
  //        break;
  //      }
  //      double theta = MyMath.polarAngle(pt, v);
  //      if (prevThetaDefined) {
  //        double add = MyMath.normalizeAngle(theta - prevTheta);
  //        sum += add;
  //      }
  //
  //      if (db && T.update())
  //        T.msg("vertex" + EdSegment.showDirected(pt, v) + " theta="
  //            + MyMath.degrees(theta) + " sum=" + +MyMath.degrees(sum));
  //
  //      prevThetaDefined = true;
  //      prevTheta = theta;
  //    }
  //    if (Math.abs(sum) > 1e-2)
  //      ret = true;
  //    if (db && T.update())
  //      T.msg("returning " + ret);
  //    return ret;
  //  }
  //
  //  //  private static final double EPS = 1e-3;
  //  private static boolean left(FPoint2 p0, FPoint2 p1, FPoint2 p2) {
  //    return MyMath.sideOfLine(p0, p1, p2) > 0;
  //  }

  //  /**
  //   * Find a convex vertex
  //   * @return index of convex vertex, or -1 if none found
  //   */
  //  public int findConvexVertex() {
  //    return findVertex(1);
  //  }
  //  /**
  //   * Find a reflex vertex
  //   * @return index of reflex vertex, or -1 if none found
  //   */
  //  public int findReflexVertex() {
  //    return findVertex(-1);
  //  }
  //  private int findVertex(int sign) {
  //    int cVert = -1;
  //    for (int i = 0; i < nPoints(); i++) {
  //      FPoint2 p0 = getPointMod(i);
  //      FPoint2 p1 = getPointMod(i + 1);
  //      FPoint2 p2 = getPointMod(i + 2);
  //      double sl = MyMath.sideOfLine(p0, p1, p2);
  //
  //      if (MyMath.sign(sl) == sign
  //          && MyMath.ptDistanceToLine(p1, p0, p2, null) > 1e-1) {
  //        cVert = (i + 1) % nPoints();
  //        break;
  //      }
  //    }
  //    return cVert;
  //  }

  /**
   * Determine if point is inside polygon
   * @param pt point to test
   * @return -1 if inside and polygon has cw winding,
   *  1 if inside and polygon has ccw winding, 0 if outside
   */
  public int isPointInside(FPoint2 pt) {

    final boolean db = false;

    if (db && T.update())
      T.msg("isPointInside?" + T.show(this, MyColor.cRED) + T.show(pt));
    int ret = 0;

    // if the sum of the angles swept by seg from pt to each vertex is
    // +/- 360 degrees, we're inside.

    double sum = 0;
    double prevTheta = 0;
    boolean prevThetaDefined = false;
    for (int i = 0; i <= nSides(); i++) {
      FPoint2 v = getPointMod(i);
      if (v.equals(pt)) {
        if (db && T.update())
          T.msg("vertex equals query point" + EdSegment.showDirected(pt, v));

        continue;
      }
      double theta = MyMath.polarAngle(pt, v);
      if (prevThetaDefined) {
        double add = MyMath.normalizeAngle(theta - prevTheta);
        sum += add;
      }

      if (db && T.update())
        T.msg("vertex" + EdSegment.showDirected(pt, v) + " theta="
            + MyMath.degrees(theta) + " sum=" + +MyMath.degrees(sum));

      prevThetaDefined = true;
      prevTheta = theta;
    }
    if (Math.abs(sum) > 1e-2) {
      ret = (sum > 0) ? 1 : -1;
    }
    if (db && T.update())
      T.msg("returning " + ret);
    return ret;
  }

  public double signedDist(FPoint2 pt) {
    double minDist = 0;
    for (int i = 0; i < nSides(); i++) {
      FPoint2 p0 = this.getPointMod(i), p1 = getPointMod(i + 1);
      double side = MyMath.sideOfLine(p0, p1, pt);
      if (side == 0)
        side = 1;

      double sd = MyMath.ptDistanceToSegment(pt, p0, p1, null);

      double dist = side * sd;
      if (i == 0)
        minDist = dist;
      else {
        if (dist < 0) {
          if (minDist >= 0 || dist > minDist)
            minDist = dist;
        } else {
          minDist = Math.min(minDist, dist);
        }
      }
    }
    return minDist;
  }

  public double distFrom(FPoint2 pt) {

    double dist = Double.MAX_VALUE;
    do {
      //      if (inactive() && iState != FINITE)
      //        break;

      if (getBounds().contains(pt)) {
        if (isPointInside(pt) != 0) {
          dist = 0;
          break;
        }
      }
      for (int i = 0; i < nPoints(); i++) {
        FPoint2 p1 = getPoint(i);
        FPoint2 p2 = getPoint((i + 1) % nPoints());
        double d2 = MyMath.ptDistanceToSegment(pt, p1, p2, null);
        if (d2 < dist)
          dist = d2;
      }
    } while (false);
    return dist;
  }

  public EdObjectFactory getFactory() {
    return FACTORY;
  }

  /**
   * Get bounding rectangle of object
   * @return FRect
   */
  public FRect getBounds() {
    FRect r = null;
    do {
      for (int i = 0; i < nPoints(); i++)
        r = FRect.add(r, getPoint(i));
    } while (false);
    return r;
  }

  /** 
   * Get # vertices in polygon
   * @return # vertices in polygon
   * @deprecated use nPoints instead
   */
  public int nVert() {
    return nPoints();
  }

  /**
   * Move entire object by a displacement
   * Default implementation just adjusts each point.
   * @param orig : a copy of the original object
   * @param delta : amount to move by
   */
  public void moveBy(EdObject orig, FPoint2 delta) {
    for (int i = 0; i < nPoints(); i++)
      setPoint(i,
      //          Main.snap(
          FPoint2.add(orig.getPoint(i), delta, null)
      //)
      );
  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {

    public EdObject construct() {
      return new EdPolygon();
    }

    public String getTag() {
      return "polygon";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdPolygon, parse, next=" + s.peek().debug());

      EdPolygon poly = new EdPolygon();
      poly.setFlags(flags);
      s.read(T_PAROP);
      int nPts = s.extractInt();
      for (int i = 0; i < nPts; i++)
        poly.addPoint(s.extractFPoint2());

      s.read(T_PARCL);
      return poly;
    }

    public void write(StringBuilder sb, EdObject obj) {
      EdPolygon d = (EdPolygon) obj;
      sb.append('(');
      toString(sb, d.nPoints());
      for (int i = 0; i < d.nPoints(); i++) {
        sb.append("\n  ");
        toString(sb, d.getPoint(i));
      }
      sb.append(')');
    }

    public String getMenuLabel() {
      return "Add polygon";
    }
    public String getKeyEquivalent() {
      return "y";
    }
  };

  /**
   * Find a point that is strictly interior to a polygon.
   * Runs in O(n^2) time.
   * @param p
   * @return interior point, or null if failed to find one
   */
  public FPoint2 findInteriorPoint() {
    final boolean db = false;
    if (db && T.update())
      T.msg("findInteriorPoint" + T.show(this, null, STRK_THICK, -1));
    FPoint2 ret = null;
    outer: do {

      // starting with a point near an arbitrary vertex, perform a binary search
      // moving point closer to vertex, testing if it's interior
      FPoint2 cv, cw;
      {
        int cVert = 0;
        FPoint2 p0 = getPointMod(cVert - 1);
        cv = getPointMod(cVert);
        FPoint2 p2 = getPointMod(cVert + 1);
        double th0 = MyMath.polarAngle(cv, p0);
        double th1 = MyMath.polarAngle(cv, p2);
        double thMid = MyMath.normalizeAnglePositive(th0 - th1) / 2 + th1;
        cw = MyMath.ptOnCircle(cv, thMid, 10.0);
      }
      if (db && T.update())
        T.msg("vertex and midpoint" + T.show(cv) + T.show(cw));
      for (int attempt = 0; attempt < this.nPoints(); attempt++) {
        if (isPointInside(cw) == 1) {
          ret = cw;
          break outer;
        }
        cw = FPoint2.midPoint(cw, cv);
      }

      // binary search failed; examine centroid of every possible ear
      for (int i = 0; i < nPoints(); i++) {
        // find interior point by examining centroids of every possible ear
        FPoint2 p0 = getPoint(i);
        FPoint2 p1 = getPointMod(i + 1);
        FPoint2 p2 = getPointMod(i + 2);
        FPoint2 c = new FPoint2((p0.x + p1.x + p2.x) / 3,
            (p0.y + p1.y + p2.y) / 3);
        if (isPointInside(c) != 0) {
          ret = c;
          break;
        }
      }
    } while (false);
    return ret;
  }

  /**
   * Determine winding of polygon
   * @return 1 if ccw, -1 if cw, 0 if incomplete
   */
  public int winding() {
    int ret = 0;

    if (complete()) {
      FPoint2 c = findInteriorPoint();
      /*
      if (c == null)
        throw new IllegalStateException("failed to find interior point");
        */
      if (c != null)
        ret = isPointInside(c);
      //      for (int i = 0; i < nPoints(); i++) {
      //        // find interior point by examining centroids of every possible ear
      //        FPoint2 p0 = getPoint(i);
      //        FPoint2 p1 = getPointMod(i + 1);
      //        FPoint2 p2 = getPointMod(i + 2);
      //        FPoint2 c = new FPoint2((p0.x + p1.x + p2.x) / 3,
      //            (p0.y + p1.y + p2.y) / 3);
      //        ret = isPointInside(c);
      //        if (ret != 0)
      //          break;
      //      }
    }
    return ret;
  }

  /**
   * Construct normalized polygon, one that has a ccw winding
   * @param src : polygon to normalize
   * @return EdPolygon
   */
  public static EdPolygon normalize(EdPolygon src) {

    EdPolygon dest = null;
    int winding = src.winding();
    if (winding < 0) {
      dest = new EdPolygon();
      dest.setLabel(src.getLabel());
      for (int j = src.nPoints() - 1; j >= 0; j--)
        dest.addPoint(src.getPoint(j));
    } else {
      dest = (EdPolygon) src.clone();
    }
    return dest;
  }

  /**
   * Determine if polygon lies within a circle
   * @param center center of circle
   * @param rad radius of circle
   * @param extremalPoint if not null, and polygon is within circle, location of point that 
   *   is farthest from the center is returned here
   */
  public boolean withinCircle(FPoint2 center, double rad, FPoint2 extremalPoint) {
    double rs = rad * rad;
    double extDist = -1;

    boolean within = true;
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 p = getPoint(i);
      double dx = p.x - center.x;
      double dy = p.y - center.y;

      double distSq = dx * dx + dy * dy;
      if (distSq > rs) {
        within = false;
        break;
      }
      if (extremalPoint != null && distSq > extDist) {
        extDist = distSq;
        extremalPoint.setLocation(p);
      }
    }
    return within;
  }

  private static final double NEARENDPTDIST = 1e-2;

  /**
   * Find closest point on polygon boundary to a point
   * @param pt  point
   * @return closest point on boundary to pt
   */
  public FPoint2 closestBoundaryPointTo(FPoint2 pt) {
    FPoint2 w = new FPoint2();
    closestBoundaryPointTo(pt, w);
    return w;
    //    
    //    FPoint2 ret = null;
    //    double minDist = 0;
    //    FPoint2 workPt = new FPoint2();
    //    for (int i = 0; i < nPoints(); i++) {
    //
    //      FPoint2 e0 = sideStart(i), e1 = sideEnd(i);
    //
    //      double dist = MyMath.ptDistanceToSegment(pt, e0, e1, workPt);
    //
    //      if (ret == null || dist < minDist) {
    //        minDist = dist;
    //        ret = new FPoint2(workPt);
    //      }
    //    }
    //    return ret;
  }

  /**
   * Find closest point on polygon boundary to a point
   * @param pt   point
   * @param destPt closest point on boundary returned here
   * @return closest component (1+v for vertex, -(1+e) for edge
   * @return closest point on boundary to pt
   */
  public int closestBoundaryPointTo(FPoint2 pt, FPoint2 destPt) {
    int comp = 0;

    FPoint2 ret = null;
    double minDist = 0;
    FPoint2 workPt = new FPoint2();
    for (int i = 0; i < nPoints(); i++) {

      FPoint2 e0 = sideStart(i), e1 = sideEnd(i);

      double dist = MyMath.ptDistanceToSegment(pt, e0, e1, workPt);

      if (ret == null || dist < minDist) {
        minDist = dist;
        ret = new FPoint2(workPt);
        if (workPt.equals(e0))
          comp = 1 + i;
        else if (workPt.equals(e1))
          comp = 1 + ((i + 1) % nPoints());
        else
          comp = -1 - i;
      }
    }
    if (destPt != null)
      destPt.setLocation(ret);
    return comp;
  }

  /**
   * Determine which part of the polygon, if any, intersects a circle (disc)
   * @param cpt : center of circle
   * @param r : radius
   * @return 0 if no intersection, else 1 + vertex number, or -1 - edge number
   */
  public int intersectsCircle(FPoint2 cpt, double r) {

    int part = 0;

    boolean db = false;
    if (db)
      Streams.out.println("intersectsCircle, " + cpt + " rad=" + r);

    for (int i = 0; i < nPoints(); i++) {

      FPoint2 e0 = sideStart(i), e1 = sideEnd(i);

      double dist = MyMath.ptDistanceToSegment(cpt, e0, e1, null);
      if (db)
        Streams.out.println(" side " + sideStart(i) + ".." + sideEnd(i)
            + " dist=" + dist);

      if (dist <= r) {
        part = -1 - i;
        // did it intersect at an endpoint?
        // calculate parameter for position on segment
        double t = MyMath.positionOnSegment(cpt, e0, e1);
        if (t <= NEARENDPTDIST)
          part = 1 + i;
        else if (t >= 1 - NEARENDPTDIST)
          part = 1 + ((i + 1) % nPoints());

        break;
      }
    }

    return part;
  }

  public void cleanUp() {
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 pi = getPoint(i);
      for (int j = nPoints() - 1; j > i; j--) {
        FPoint2 pj = getPoint(j);
        if (pi.equals(pj))
          deletePoint(j);
      }
    }
    EdPolygon n = normalize(this);
    copyPointsFrom(n);
  }

  public void filterCollinear(double perpDistance) {
    if (nPoints() < 3)
      return;
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 p0 = getPointMod(i - 1);
      FPoint2 p1 = getPointMod(i);
      FPoint2 p2 = getPointMod(i + 1);
      double dist = MyMath.ptDistanceToSegment(p1, p0, p2, null);
      if (dist < perpDistance) {
        deletePoint(i);
        i--;
      }
    }
  }

  public void fill(Graphics2D g) {

    if (nPoints() >= 3) {
      GeneralPath path = new GeneralPath();
      //    Polygon p = new Polygon();
      for (int i = 0; i < nPoints(); i++) {

        FPoint2 pt = getPoint(i);
        float fx = (float) pt.x;
        float fy = (float) pt.y;
        if (i == 0)
          path.moveTo(fx, fy);
        else
          path.lineTo(fx, fy);
      }
      path.closePath();
      g.fill(path);
    }
    //    g.fillPolygon(p);
  }

  public void fill(Color color) {
    V.pushColor(color, Color.BLUE);
    Graphics2D g = V.get2DGraphics();
    fill(g);
    V.pop();
  }
  private static Color COLOR = new Color(0x00, 0xa3, 0xa3); // new Color(0xc2,0x47,0x85);

  public void render(Color color, int stroke, int markType) {

    if (false) { //Tools.dbWarn(true)) {
      color = MyColor.cBLUE;
    }

    V.pushColor(color, isActive() ? COLOR : Color.GRAY);
    V.pushStroke(stroke);

    FPoint2 start = null;
    FPoint2 last = null;
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 pt = getPoint(i);
      if (start == null)
        start = pt;
      if (last != null) {
        V.drawLine(last, pt);
      }
      last = pt;
    }
    if (start != null && last != null && !hasFlags(FLG_OPEN))
      V.drawLine(last, start);

    for (int i = 0; i < nPoints(); i++) {
      FPoint2 pt = getPoint(i);
      if (markType >= 0)
        V.mark(pt, markType);
    }
    V.popStroke();

    if (complete()) {
      if (Editor.withLabels(true)) {
        FRect r = this.getBounds();
        FPoint2 cp = r.midPoint();
        V.pushScale(.6);
        for (int i = 0; i < nPoints(); i++) {
          FPoint2 pt = getPoint(i);
          pt = MyMath.ptOnCircle(pt, MyMath.polarAngle(cp, pt),
              V.getScale() * 1.5);
          V.draw("" + i, pt, TX_FRAME | TX_BGND);
        }
        V.pop();
      }
      if (Editor.withLabels(false)) {
        FPoint2 p0 = getPoint(0);
        FPoint2 p1 = getPoint(1);
        plotLabel(MyMath.ptOnCircle(FPoint2.midPoint(p0, p1), MyMath
            .polarAngle(p0, p1)
            - Math.PI / 2, V.getScale()));
      }
    }
    V.popColor();
  }

  public static EdPolygon regularNGon(FPoint2 center, double radius, int n) {
    EdPolygon ret = new EdPolygon();
    for (int i = 0; i < n; i++)
      ret.addPoint(MyMath.ptOnCircle(center, (i * Math.PI * 2) / n, radius));
    return ret;
  }

  private static FPoint2 startPt = new FPoint2(-1.2387e4, -1.3238e4);

  public boolean boundaryContains(FPoint2 pt) {
    final double EPS = 1e-6;

    boolean ret = false;

    FPoint2 p1 = null;
    for (int i = 0; i < nPoints(); i++) {
      if (i == 0)
        p1 = sideStart(i);
      if (pt.distanceSq(sideStart(i)) < EPS * EPS) {
        ret = true;
        break;
      }
      FPoint2 p2 = sideEnd(i);
      if (MyMath.ptDistanceToSegment(pt, p1, p2, null) < EPS) {
        ret = true;
        break;
      }
      p1 = p2;
    }
    return ret;
  }

  public boolean contains(FPoint2 pt) {
    if (pt == null)
      throw new IllegalArgumentException();
    int crossCount = 0;
    for (int i = 0; i < nSides(); i++) {

      if (MyMath.lineSegmentIntersection(startPt, pt, sideStart(i), sideEnd(i),
          null) != null)
        crossCount++;
    }
    return (crossCount & 1) != 0;
  }

  public static EdPolygon randomPoly(Random r, int numVert, FRect bounds) {
    final boolean db = false;

    if (db)
      Streams.out.println("randomPoly nv=" + numVert + " bnds=" + bounds);

    if (r == null)
      r = new Random();

    EdPolygon ret = null;

    int nAttempts = numVert * numVert;
    while (nAttempts-- > 0) {

      FPoint2[] p = new FPoint2[numVert];

      FPoint2 origin = new FPoint2(.5, .5);

      for (int i = 0; i < numVert; i++) {
        double ang = r.nextDouble() * Math.PI * 2;
        double rr = Math.sqrt(r.nextDouble()) * .5;
        FPoint2 pt = MyMath.ptOnCircle(origin, ang, rr, null);
        pt.x = pt.x * bounds.width + bounds.x;
        pt.y = pt.y * bounds.height + bounds.y;
        p[i] = pt;
      }

      // eliminate crossings, if possible
      int att2 = 10;
      boolean problem = true;
      inner: while (att2-- != 0) {
        for (int i = 0; i < numVert - 1; i++) {
          FPoint2 p1 = p[i];
          FPoint2 p2 = p[i + 1];
          for (int j = i + 2; j < numVert; j++) {
            if (j + 1 == numVert && i == 0)
              continue;

            FPoint2 p3 = p[j];
            FPoint2 p4 = p[(j + 1) % numVert];

            FPoint2 isect = MyMath
                .lineSegmentIntersection(p1, p2, p3, p4, null);
            if (isect == null)
              continue;
            if (db)
              Streams.out.println(" found crossing=" + isect + " for i=" + i
                  + " j=" + j);

            p[(i + 1) % numVert] = p3;
            p[j] = p2;
            continue inner;
          }
        }
        problem = false;
      }

      if (problem)
        continue;

      for (int i = 0; i < numVert; i++) {

        FPoint2 p0 = p[i];
        FPoint2 p1 = p[(i + 1) % numVert];
        FPoint2 p2 = p[(i + 2) % numVert];

        FPoint2 s1 = FPoint2.difference(p0, p1, null);

        FPoint2 s2 = FPoint2.difference(p2, p1, null);

        double a1 = MyMath.polarAngle(s1);
        double a2 = MyMath.polarAngle(s2);

        double ang = MyMath.normalizeAnglePositive(a1 - a2);
        final double MINANG = Math.PI / 12;

        //  System.out.println(p0+" ... "+p1+" ... "+p2+" angle=" + Tools.fa(ang));

        if (ang < MINANG || ang > Math.PI * 2 - MINANG
            || Math.abs(ang - Math.PI) < MINANG

        ) {
          problem = true;
          //  System.out.println();
          break;
        }
      }
      if (problem)
        continue;
      ret = new EdPolygon(p);
      break;
    }
    return ret;
  }
  public void reverseWinding() {
    DArray pts = this.getPts();
    for (int i = 0; i < pts.size() / 2; i++) {
      pts.swap(i, pts.size() - 1 - i);
    }
  }

  public EdPolygon offsetVertices(double amt) {
    EdPolygon p = new EdPolygon();
    for (int i = 0; i < nPoints(); i++) {
      FPoint2 v = MyMath.offsetVertex(getPointMod(i - 1), getPointMod(i),
          getPointMod(i + 1), amt);
      p.addPoint(v);
    }
    return p;
  }

  /**
   * Rotate vertex positions within the points array
   * @param i amount to add to current position
   */
  public void rotatePoints(int i) {
    DArray newPts = new DArray();
    for (int j = 0; j < nPoints(); j++) {
      newPts.add(getPointMod(j + i));
    }
    DArray oldPoints = getPts();
    oldPoints.clear();
    oldPoints.addAll(newPts);
  }
}
