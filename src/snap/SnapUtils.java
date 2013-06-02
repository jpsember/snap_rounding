package snap;

import java.awt.*;
import java.util.*;
import testbed.*;
import base.*;

public class SnapUtils {

  public static void render(int x, int y) {
    FPoint2 w0 = Main.grid().toView(x, y, null);
    render(w0);
  }

  public static void render(IPoint2 gridPt) {
    render(gridPt.x, gridPt.y);
  }

  public static void render(FPoint2 viewPt) {
//    vp vp = TestBed.view();
    V.fillCircle(viewPt, V.getScale() * .4);
  }

  private static class TraceHotPixel implements  Renderable {
    public TraceHotPixel(IPoint2 pt) {
      this.pt = pt;
    }
    private IPoint2 pt;

    public void render(Color c, int stroke, int markType) {
      if (c == null) c = Color.RED;
      V.pushColor(c);
      Main.grid().highlightCell(pt);
      V.popColor();
    }
  }

  public static String plot(IPoint2 pt) {
    return T.show(new TraceHotPixel(pt));
  }

  /**
   * Plot a snapped arrangement
   * @param g : Grid
   * @param segs : array of segments, each containing zero or more snap points
   * @param segColor : if not null, segments are plotted with this color
   * @param hotPixelColor : if not null, hot pixels are plotted with this color
   */
  public static void plotSnappedSegments(Grid g, Segment[] segs,
      Color segColor, Color hotPixelColor, Color listColor) {
//    vp vp = TestBed.view();
    IPoint2[] hp = extractSnapPoints(segs, null);
    if (segColor != null) {
      V.pushColor(segColor);
      if (Main.highDetail()) {
        for (int i = 0; i < hp.length; i++) {
          FPoint2 worldPt2 = g.toView(hp[i]);
          SnapUtils.render(worldPt2);
        }
      }

      for (int i = 0; i < segs.length; i++) {
        Segment s = segs[i];
        FPoint2 prev = null;
        for (int j = 0; j < s.nSnapPoints(); j++) {
          FPoint2 next = g.toView(s.getSnapPoint(j));
          if (prev != null)
            V.drawLine(prev, next);
          prev = next;
        }
      }
      V.popColor();
    }

    if (hotPixelColor != null) {
      V.pushColor(hotPixelColor);
      for (int i = 0; i < hp.length; i++) {
        g.highlightCell(hp[i]);
      }
      V.popColor();
    }

    if (listColor != null) {
      StringBuilder sb = new StringBuilder();
      V.pushScale(.7);
      V.pushColor(listColor);
      for (int i = 0; i < segs.length; i++) {
        Segment s = segs[i];
        sb.append(s.id() + ":");
        for (int j = 0; j < s.nSnapPoints(); j++) {
          IPoint2 pt = s.getSnapPoint(j);
          sb.append("  ");
          sb.append(Tools.f(pt.x, 2) + "," + Tools.f(pt.y, 2));
        }
        sb.append('\n');
      }
      V.draw(sb.toString(), 20, 90, Globals.TX_CLAMP | Globals.TX_BGND | 30);
      V.popColor();
      V.popScale();
    }

  }

  /**
   * Construct a list of hot pixels for a set of segments.
   * 
   * @param s : segments
   * @param blackBox : BlackBox
   * @param hpList : if not null, stores DArrays for each segment, listing
   *  hot pixels each segment is HOT within
   * @return IPoint[]
   */
  public static IPoint2[] calcHotPixels(Segment[] s, BlackBox blackBox,
      DArray hpList) {
    Map m = new HashMap();

    final boolean db = false;

    if (db && T.update())
      T.msg("calcHotPixels for " + s.length + " segments");
    blackBox.setOrientation(0);

    if (hpList != null) {
      hpList.clear();
      for (int i0 = 0; i0 < s.length; i0++)
        hpList.add(new DArray());
    }

    for (int i0 = 0; i0 < s.length; i0++) {

      Segment a = s[i0];

      IPoint2 e0 = a.pt(0), e1 = a.pt(1);

      m.put(e0, Boolean.TRUE);
      m.put(e1, Boolean.TRUE);

      DArray lst = null;
      if (hpList != null) {
        lst = hpList.getDArray(i0);
        lst.add(e0);
        lst.add(e1);
      }

      if (db && T.update())
        T.msg(" adding endpoints " + e0 + ", " + e1);

      for (int i1 = i0 + 1; i1 < s.length; i1++) {
        Segment b = s[i1];
        IPoint2 pt;

        BlackBox bb = blackBox.construct(a, b);
        if (db && T.update())
          T.msg("" + bb);
        if (!bb.abWithinSegments())
          continue;
        if (db && T.update())
          T.msg(" adding intersection pixel " + bb.getIntersectionPixel(false));
        pt = bb.getIntersectionPixel(false);
        m.put(pt, Boolean.TRUE);

        if (hpList != null) {
          hpList.getDArray(i1).add(pt);
          lst.add(pt);
        }
      }
    }

    // sort hot pixels for segments into order, and remove duplicates
    if (hpList != null) {
      for (int i = 0; i < s.length; i++) {
        DArray a = hpList.getDArray(i);
        a.sort(IPoint2.comparator);
        IPoint2 prev = null;
        for (int j = a.size() - 1; j >= 0; j--) {
          IPoint2 th = (IPoint2) a.get(j);
          if (prev != null && prev.equals(th)) {
            a.remove(j);
          }
          prev = th;
        }
      }
    }

    DArray jpts = new DArray();
    jpts.addAll(m.keySet());
    jpts.sort(IPoint2.comparator);
    return (IPoint2[]) jpts.toArray(IPoint2.class);
  }

  private static IPoint2[] extractSnapPoints(SnapArrangement a) {
    int nn = a.nNodes();
    IPoint2[] r = new IPoint2[nn];
    for (int i = 0; i < nn; i++) {
      r[i] = a.hotPixel(i + a.idBase());
    }
    return r;
  }

  private static IPoint2[] extractSnapPoints(Segment[] segs, Map m) {
    DArray h = new DArray();
    if (m == null)
      m = new HashMap();
    for (int i = 0; i < segs.length; i++) {
      Segment s = segs[i];
      for (int j = 0; j < s.nSnapPoints(); j++)
        m.put(s.getSnapPoint(j), new Integer(s.id()));
    }
    h.addAll(m.keySet());
    return (IPoint2[]) h.toArray(IPoint2.class);
  }

  private static class Endpoints {
    private FPoint2 v0, v1;

    public Endpoints(Grid grid, IPoint2 p0, IPoint2 p1) {
      this.v0 = grid.toView(p0);
      this.v1 = grid.toView(p1);
    }
  }

  /**
   * Plot list of problems associated with snap list.
   * 
   * @param grid 
   * @param pts DArray containing <FPoint2 String> pairs
   */
  public static void plotSnapErrors(Grid grid, FPoint2[] pts) {
//    vp V = TestBed.view();
    for (int i = 0; i < pts.length; i++) {
      FPoint2 probPt = pts[i];
      FPoint2 pt = probPt;
      V.drawCircle(pt,V.getScale() * 1.1);
    }
  }

  /**
   * Test that snapped segments don't contain intersections not occurring
   * at hot pixels.
   * 
   * @return FPoint2[] array of problem intersections
   */
  public static FPoint2[] findSnapErrors(Grid grid, SnapArrangement ar) {

    DArray a = new DArray();
    do {
      extractSnapPoints(ar);

      // construct a list of segment sections between snap points
      DArray fragList = new DArray();
      FRect fragBounds = null;

      Graph g = ar.getGraph();
      for (int jj = 0; jj < ar.nNodes(); jj++) {
        int id1 = jj + ar.idBase();
        for (int kk = 0; kk < g.nCount(id1); kk++) {
          int id2 = g.neighbor(id1, kk);
          if (id2 < id1)
            continue;

          //        Segment seg = segs[i];
          //        for (int j = 0; j < seg.nSnapPoints() - 1; j++) {
          Endpoints ep = new Endpoints(grid, ar.hotPixel(id1), ar.hotPixel(id2));
          if (fragBounds == null)
            fragBounds = new FRect(ep.v0, ep.v1);
          fragBounds.add(ep.v0);
          fragBounds.add(ep.v1);
          fragList.add(ep);
        }
      }

      if (fragList.isEmpty())
        break;

      // to speed up this process, divide bounds into a grid of bucket/pixels.
      // Each  bucket contains the fragments whose minimum bounding rectangles
      // intersect the bucket's pixel.  
      // Thus we need only compare fragments against others from the same bucket.

      int nBuckets = Math.max(1, ar.nNodes() / 60);
      DArray[] buckets = new DArray[nBuckets * nBuckets];
      {
        double bWidth = fragBounds.width / nBuckets, bHeight = fragBounds.height
            / nBuckets;
        for (int i = 0; i < buckets.length; i++)
          buckets[i] = new DArray();
        for (int i = 0; i < fragList.size(); i++) {
          Endpoints e = (Endpoints) fragList.get(i);
          // add endpoint to every bucket it may intersect
          int y0 = (int) ((Math.min(e.v0.y, e.v1.y) - fragBounds.y) / bHeight);
          int y1 = MyMath.clamp(
              (int) ((Math.max(e.v0.y, e.v1.y) - fragBounds.y) / bHeight), 0,
              nBuckets - 1);
          int x0 = (int) ((Math.min(e.v0.x, e.v1.x) - fragBounds.x) / bWidth);
          int x1 = MyMath.clamp(
              (int) ((Math.max(e.v0.x, e.v1.x) - fragBounds.x) / bWidth), 0,
              nBuckets - 1);

          for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
              buckets[y * nBuckets + x].add(e);
            }
          }
        }
      }

      final double NEARZERO = .00001;

      for (int k = 0; k < buckets.length; k++) {
        DArray b = buckets[k];
        for (int i = 0; i < b.size(); i++) {
          Endpoints ei = (Endpoints) b.get(i);

          for (int j = i + 1; j < b.size(); j++) {
            Endpoints ej = (Endpoints) b.get(j);

            FPoint2 pt = MyMath.lineSegmentIntersection(ei.v0, ei.v1, ej.v0,
                ej.v1, null);
            if (pt == null)
              continue;
            String err = null;

            // make sure the intersection occurs at an endpoint of each
            double di = Math.min(pt.distance(ei.v0), pt.distance(ei.v1));
            double dj = Math.min(pt.distance(ej.v0), pt.distance(ej.v1));

            // in triangle grid, fragment endpoint can lie on other fragment.
            // To eliminate this possibility, intersection point must be
            // distinct from all four endpoints.

            if (Math.min(di, dj) > NEARZERO) {
              err = "";
            }
            if (err != null) {
              a.add(pt);
              //              a.add(err);
            }
          }
        }
      }
    } while (false);
    return (FPoint2[]) a.toArray(FPoint2.class);
  }

  //  /**
  //   * Test that snapped segments don't contain intersections not occurring
  //   * at hot pixels.
  //   * 
  //   * @return FPoint2[] array of problem intersections
  //   * @deprecated
  //   */
  //  public static FPoint2[] findSnapErrors(Grid grid, Segment[] segs) {
  //
  //    DArray a = new DArray();
  //    do {
  //      extractSnapPoints(segs, null);
  //
  //      // construct a list of segment sections between snap points
  //      DArray fragList = new DArray();
  //
  //      FRect fragBounds = null;
  //      for (int i = 0; i < segs.length; i++) {
  //        Segment seg = segs[i];
  //        for (int j = 0; j < seg.nSnapPoints() - 1; j++) {
  //          Endpoints ep = new Endpoints(grid, seg.getSnapPoint(j), seg
  //              .getSnapPoint(j + 1));
  //          if (fragBounds == null)
  //            fragBounds = new FRect(ep.v0, ep.v1);
  //          fragBounds.add(ep.v0);
  //          fragBounds.add(ep.v1);
  //          fragList.add(ep);
  //        }
  //      }
  //
  //      if (fragList.isEmpty())
  //        break;
  //
  //      // to speed up this process, divide bounds into a grid of bucket/pixels.
  //      // Each  bucket contains the fragments whose minimum bounding rectangles
  //      // intersect the bucket's pixel.  
  //      // Thus we need only compare fragments against others from the same bucket.
  //
  //      int nBuckets = Math.max(1, segs.length / 50);
  //      DArray[] buckets = new DArray[nBuckets * nBuckets];
  //      {
  //        double bWidth = fragBounds.width / nBuckets, bHeight = fragBounds.height
  //            / nBuckets;
  //        for (int i = 0; i < buckets.length; i++)
  //          buckets[i] = new DArray();
  //        for (int i = 0; i < fragList.size(); i++) {
  //          Endpoints e = (Endpoints) fragList.get(i);
  //          // add endpoint to every bucket it may intersect
  //          int y0 = (int) ((Math.min(e.v0.y, e.v1.y) - fragBounds.y) / bHeight);
  //          int y1 = MyMath.clamp(
  //              (int) ((Math.max(e.v0.y, e.v1.y) - fragBounds.y) / bHeight), 0,
  //              nBuckets - 1);
  //          int x0 = (int) ((Math.min(e.v0.x, e.v1.x) - fragBounds.x) / bWidth);
  //          int x1 = MyMath.clamp(
  //              (int) ((Math.max(e.v0.x, e.v1.x) - fragBounds.x) / bWidth), 0,
  //              nBuckets - 1);
  //
  //          for (int y = y0; y <= y1; y++) {
  //            for (int x = x0; x <= x1; x++) {
  //              buckets[y * nBuckets + x].add(e);
  //            }
  //          }
  //        }
  //      }
  //
  //      final double NEARZERO = .00001;
  //
  //      for (int k = 0; k < buckets.length; k++) {
  //        DArray b = buckets[k];
  //        for (int i = 0; i < b.size(); i++) {
  //          Endpoints ei = (Endpoints) b.get(i);
  //
  //          for (int j = i + 1; j < b.size(); j++) {
  //            Endpoints ej = (Endpoints) b.get(j);
  //
  //            FPoint2 pt = MyMath.lineSegmentIntersection(ei.v0, ei.v1, ej.v0,
  //                ej.v1, null);
  //            if (pt == null)
  //              continue;
  //            String err = null;
  //
  //            // make sure the intersection occurs at an endpoint of each
  //            double di = Math.min(pt.distance(ei.v0), pt.distance(ei.v1));
  //            double dj = Math.min(pt.distance(ej.v0), pt.distance(ej.v1));
  //
  //            // in triangle grid, fragment endpoint can lie on other fragment.
  //            // To eliminate this possibility, intersection point must be
  //            // distinct from all four endpoints.
  //
  //            if (Math.min(di, dj) > NEARZERO) {
  //              err = "";
  //            }
  //            if (err != null) {
  //              a.add(pt);
  //              //              a.add(err);
  //            }
  //          }
  //        }
  //      }
  //    } while (false);
  //    return (FPoint2[]) a.toArray(FPoint2.class);
  //  }

  /**
   * Build a snap list from a set of segments, using simple, slow algorithm
   * @param segs
   * @return
   */
  public static void performSimpleSnap(Segment[] segs, BlackBox blackBox,
      boolean introduceErrs, boolean skipWarm) {
    final boolean db = false;

    if (db && T.update()) {
      T.msg("SimpleSnapOper, processing " + segs.length + " segments, bb="
          + blackBox + "\n skipWarm=" + skipWarm);
    }

    blackBox.setOrientation(0);

    Random r = null;
    if (introduceErrs)
      r = new Random(1965);

    DArray hpList = null;
    if (skipWarm)
      hpList = new DArray();

    // get sorted list of hot pixels
    IPoint2[] hotPixels = SnapUtils.calcHotPixels(segs, blackBox, hpList);

    // process each hot pixel

    if (db && T.update())
      T.msg("processing hot pixels (#= " + hotPixels.length + ")");

    // Test every segment against this hot pixel.
    // Do this in two passes, first for nonnegative slopes.
    for (int slopesPass = 0; slopesPass < 2; slopesPass++) {

      for (int j = 0; j < hotPixels.length;) {

        // determine # hot pixels in this column
        int nPixInColumn = 0;
        for (; nPixInColumn + j < hotPixels.length; nPixInColumn++)
          if (hotPixels[nPixInColumn + j].x != hotPixels[j].x)
            break;
        for (int k = 0; k < nPixInColumn; k++) {
          // in pass 2, process hot pixels in reverse order within column,
          // for negative-sloped segments
          IPoint2 hotPixel = hotPixels[(slopesPass == 0) ? (j + k) : (j
              + nPixInColumn - 1 - k)];
          if (introduceErrs && r.nextInt(5) == 0) {
            if (db && T.update())
              T.msg("skipping hot pixel " + hotPixel);
            continue;
          }

          if (db && T.update())
            T.msg("processing hot pixel " + SnapUtils.plot(hotPixel)
                + ", slopes=" + (slopesPass == 0 ? "positive" : "negative"));

          for (int i = 0; i < segs.length; i++) {
            Segment a = segs[i];
            if (a.hasNegativeSlope() ^ (slopesPass == 1))
              continue;

            if (db && T.update())
              T.msg("testing segment " + a.toString(true) + " for hot pixel "
                  + SnapUtils.plot(hotPixel) );

            boolean isects = false;

            if (skipWarm) {
              // only snap if this hot pixel appears in the segment's snap list
              DArray snapList = hpList.getDArray(i);
              isects = snapList.contains(hotPixel);
            } else
              isects = blackBox.segmentIntersectsPixel(a.x0(), a.y0(), a.x1(),
                  a.y1(), hotPixel.x, hotPixel.y);

            if (isects) {
              if (db && T.update())
                T.msg("intersects=true");
              if (blackBox == BlackBoxHexStrip.S) {
                // find insertion point for this hot pixel
                DArray stack = new DArray();
                while (a.nSnapPoints() > 0) {
                  IPoint2 prev = a.peekSnapPoint();
                  if (db && T.update())
                    T.msg("testing prev=" + prev + " with new=" + hotPixel);

                  // slope > 3?
                  if (!(Math.abs(a.y1() - a.y0()) > 3 * (a.x1() - a.x0())))
                    break;

                  if (prev.x != hotPixel.x - 1)
                    break;
                  if ((prev.y <= hotPixel.y) ^ a.hasNegativeSlope())
                    break;
                  stack.push(prev);
                  a.popSnapPoint();
                }
                if (db && T.update())
                  T.msg("inserting hot pixel " + SnapUtils.plot(hotPixel)
                      + " to " + a + " snap points");
                a.addSnapPoint(hotPixel);
                while (!stack.isEmpty()) {
                  if (db && T.update())
                    T.msg("appending old "
                        + SnapUtils.plot( (IPoint2) stack.last()) + " to " + a
                        + " snap points");
                  a.addSnapPoint((IPoint2) stack.pop());
                }
              } else {
                if (db && T.update())
                  T.msg("adding hot pixel " + SnapUtils.plot(hotPixel) + " to "
                      + a + " snap points");
                a.addSnapPoint(hotPixel);
              }
            }
          }
        }
        j += nPixInColumn;
      }
    }

    // now sort snap points for segments according to black box
    for (int i = 0; i < segs.length; i++) {
      Segment s = segs[i];

      IPoint2[] p = (IPoint2[]) (s.popSnapPoints(s.nSnapPoints())
          .toArray(IPoint2.class));
      for (int m = 0; m < p.length; m++) {
        for (int n = m + 1; n < p.length; n++) {
          int ret = blackBox.compareSnapPoints(s, p[m], p[n]);
          if (ret > 0) {
            IPoint2 tmp = p[m];
            p[m] = p[n];
            p[n] = tmp;
          }
        }
      }
      for (int j = 0; j < p.length; j++)
        s.addSnapPoint(p[j]);
      if (db && T.update()) {
        StringBuilder sb = new StringBuilder();
        sb.append("snap points for " + s.str() + " are:\n");
        for (int k = 0; k < p.length; k++)
          sb.append(" " + p[k] + "\n");

        T.msg(sb.toString());
      }
    }

  }
}
