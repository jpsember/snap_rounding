package snap;

import base.*;
import testbed.*;
import java.awt.*;
import java.util.*;

class BundleOper implements Globals, TestBedOperation {

  /*! .enum  .private 2600
  panel DB_SWEEP _ db_startingsegments db_stoppingsegments
  skipsweepmark db_comparepixseg plot_original db_findsegments plot_snapped show_snaplist
  db_createbundles db_splitbundles auxpasses db_vertedge db_findbundle db_segcompare 
  show_list_problems simple skipallmarks
  */

  private static final int PANEL = 2600;//!
  private static final int DB_SWEEP = 2601;//!
  private static final int DB_STARTINGSEGMENTS = 2603;//!
  private static final int DB_STOPPINGSEGMENTS = 2604;//!
  private static final int SKIPSWEEPMARK = 2605;//!
  private static final int DB_COMPAREPIXSEG = 2606;//!
  private static final int PLOT_ORIGINAL = 2607;//!
  private static final int DB_FINDSEGMENTS = 2608;//!
  private static final int PLOT_SNAPPED = 2609;//!
  private static final int SHOW_SNAPLIST = 2610;//!
  private static final int DB_CREATEBUNDLES = 2611;//!
  private static final int DB_SPLITBUNDLES = 2612;//!
  private static final int AUXPASSES = 2613;//!
  private static final int DB_VERTEDGE = 2614;//!
  private static final int DB_FINDBUNDLE = 2615;//!
  private static final int DB_SEGCOMPARE = 2616;//!
  private static final int SHOW_LIST_PROBLEMS = 2617;//!
  private static final int SIMPLE = 2618;//!
  private static final int SKIPALLMARKS = 2619;//!
  /* ! */

  public void addControls() {
    C.sOpenTab("Snap: Bundles");
    C
        .sStaticText("Snap algorithm using bundles of segments to avoid cubic worst-case behaviour."
            + " Instead of generating anchor points for the segments, generates the resulting arrangement."
            + " Performs an initial sweep to determine the nodes (hot pixels), then horizontal and vertical sweeps"
            + " to determine the arcs.");
    {
      boolean inTabs = true;
      if (!inTabs) {
        C.sOpen();
        C.sOpen("Display / Logic");
      } else {
        C.sOpenTabSet(PANEL);
        C.sOpenTab("Display / Logic");
      }
      {
        C.sOpen();
        C.sCheckBox(PLOT_ORIGINAL, "Plot original", null, true);
        C.sCheckBox(PLOT_SNAPPED, "Plot snapped", null, true);
        C.sCheckBox(SHOW_SNAPLIST, "Plot snap list", null, false);
        C.sCheckBox(SHOW_LIST_PROBLEMS, "Find problems",
            "Detect problems with snapped arrangement", false);
        C
            .sCheckBox(AUXPASSES, "Aux. passes", "Perform additional passes",
                true);
        C.sNewColumn();
        C.sCheckBox(SIMPLE, "Simple",
            "Generate using simple, slow algorithm; based on 'old' definition",
            false);
        C.sCheckBox(SKIPALLMARKS, "skip all marks", "skip all marking", false);
        C.sCheckBox(SKIPSWEEPMARK, "skip sweep marks",
            "skip marking during sweep process\nto see if this is necessary",
            false);
        C.sClose();
      }
      if (!inTabs) {
        C.sClose();
        C.sNewColumn();
        C.sOpen("Tracing");
      } else {
        C.sCloseTab();
        C.sOpenTab("Tracing");
      }
      {
        C.sCheckBox(DB_SPLITBUNDLES, "Step: Split bundles", null, false);
        C
            .sCheckBox(DB_STOPPINGSEGMENTS, "Step: Stopping segments", null,
                false);
        C.sCheckBox(DB_SWEEP, "Step: Sweep", null, false);
        C
            .sCheckBox(DB_STARTINGSEGMENTS, "Step: Starting segments", null,
                false);
        C.sCheckBox(DB_CREATEBUNDLES, "Step: Create bundles", null, false);
        C.sNewColumn();
        C.sCheckBox(DB_SEGCOMPARE, "seg compare", null, false);
        C.sCheckBox(DB_FINDBUNDLE, "find bundles", null, false);
        C.sCheckBox(DB_FINDSEGMENTS, "find segments", null, false);
        C.sCheckBox(DB_COMPAREPIXSEG, "compare pix/seg", null, false);
        C.sCheckBox(DB_VERTEDGE, "vertical arcs", null, false);

      }
      if (!inTabs) {
        C.sClose();
        C.sClose();
      } else {
        C.sCloseTab();
        C.sCloseTabSet();
      }
    }
    C.sCloseTab();
  }

  public void processAction(TBAction a) {
    if (a.code == TBAction.CTRLVALUE) {
      switch (a.ctrlId) {
      }
    }
  }
  //  public void paintView( ) {
  ////    Main.plotGrid();
  //    T.runAlgorithm(this);
  //  }

  /**
   * Plot results after runAlgorithm(), which may have been interrupted
   */
  public void paintView() {
    if (sweepLine != null)
      blackBox.renderSweepLine(sweepLine.stripX());

    //      if (false) Tools.warn("not plot bund"); else
    T.render(bundleSet);
    //    if (bundleSet != null)
    //      bundleSet.render();

    //    vp V = TestBed.view();

    if (segs != null) {
      if (C.vb(PLOT_ORIGINAL)) {

        int oldOr = prepareSegments(0);

        Color norm = C.vb(PLOT_SNAPPED) ? MyColor.get(MyColor.LIGHTGRAY, .4)
            : MyColor.get(MyColor.BLUE, .32);
        V.pushColor(norm);
        for (int i = 0; i < segs.length; i++) {
          Segment s = segs[i];
          s.render(null, -1, -1);
          //          s.render(  Main.labelSegments());
        }
        V.popColor();
        prepareSegments(oldOr);
      }
    }

    SnapArrangement a = snapArrangement;
    if (a != null) {
      if (C.vb(PLOT_SNAPPED))
        a.render();
      if (C.vb(SHOW_SNAPLIST)) {
        V.pushScale(.6);
        V.pushColor(Color.blue);

        V.draw(a.toString(), 95, 95, TX_CLAMP | TX_BGND | 40);
        V.popColor();
        V.popScale();
      }
    }

    if (snapListProblems != null) {
      V.pushColor(Color.red);
      SnapUtils.plotSnapErrors(Main.grid(), snapListProblems);
      V.popColor();
    }
    if (simpleSegs != null) {
      SnapUtils.plotSnappedSegments(Main.grid(), simpleSegs,
          C.vb(PLOT_SNAPPED) ? MyColor.get(MyColor.BLUE, .31) : null, null,
          null);
      //      if (C.vb(TRACEPLOT)) 
      {
        V.pushScale(.7);
        V.draw("(simple algorithm)", 100, 98, TX_BGND | TX_FRAME | TX_CLAMP);
        V.popScale();
      }
    }
  }

  // -----------------------------------------------------------
  // Snap algorithm and related functions, procedures
  // -----------------------------------------------------------

  public void runAlgorithm() {

    blackBox = Main.blackBox();
    if (blackBox instanceof BlackBoxTriStrip)
      T.msg("tri grid not supported");

    hex = blackBox instanceof BlackBoxHexStrip;

    sweepLine = null;
    segComparator = null;
    bundleSet = null;
    snapArrangement = null;
    simpleSegs = null;

    if (C.vb(SIMPLE)) {
      segs = Main.getSegments();
      simpleSegs = segs;
      SnapUtils.performSimpleSnap(simpleSegs, blackBox, false, false);
    } else {

      snapArrangement = new SnapArrangement();

      tr = false;
      prepareSegments(0);
      findHotPixels();
      tr = true;

      int nPasses = hex ? 2 : 2;
      if (!C.vb(AUXPASSES))
        nPasses = 1;

      for (int pass = 0; pass < nPasses; pass++) {
        prepareSegments(pass);
        bundlePass();
      }
      prepareSegments(0);
      if (C.vb(SHOW_LIST_PROBLEMS))
        snapListProblems = SnapUtils.findSnapErrors(Main.grid(),
            snapArrangement);
    }
  }

  /**
   * Find hot pixels.  We could do this by using the non-bundle algorithm,
   * and skip the snap process.
   * We will just use our slow but simple utility method.
   * 
   * Also, generate nodes for these hot pixels within the output arrangement.
   */
  private void findHotPixels() {
    hotPixels = new DArray();

    IPoint2[] pt = SnapUtils.calcHotPixels(segs, blackBox, null);
    for (int i = 0; i < pt.length; i++) {
      HotPixel p = new HotPixel(pt[i]);
      p.setNode(snapArrangement.addNode(p));
      hotPixels.add(p);
    }
  }

  /**
   * Convert segments to sweep space, so we can use the same sweep
   * algorithm for both passes.
   * 
   * @param pass
   * @return
   */
  private int prepareSegments(int pass) {
    int oldOr = 0;
    if (blackBox != null)
      oldOr = blackBox.orientation();

    if (hex)
      blackBox = BlackBoxHexBundle.S;

    blackBox.setOrientation(pass);
    BlackBox.setTransform(blackBox);

    DArray sg = new DArray();
    Segment[] s = Main.getSegments();
    for (int i = 0; i < s.length; i++) {
      Segment se = s[i];
      int id = se.id();
      se = new Segment(blackBox.toSweepSpace(se.pt(0)), blackBox
          .toSweepSpace(se.pt(1)));
      se.setId(id);
      sg.add(se);
    }
    segs = (Segment[]) sg.toArray(Segment.class);
    return oldOr;
  }

  /**
   * Prepare start, stop segment queues for auxilliary passes
   * 
   * @param segs
   */
  private void prepareSegmentQueues() {
    startPts = new SegPriorityQueue(blackBox, 0);
    stopPts = new SegPriorityQueue(blackBox, 1);

    for (int i = 0; i < segs.length; i++) {
      Segment s = segs[i];

      int dx = s.x1() - s.x0();
      int dy = s.y1() - s.y0();
      if (dx == 0)
        continue;

      if (!hex) {
        if (blackBox.orientation() == 0) {
          // Skip segments with slope m < -1 or m >= 1
          if (!(dy >= -dx && dy < dx))
            continue;
        } else {
          // Skip segments with slope m >= -1 and m < 1,
          // or in flipped space,
          //  m <= -1 or m > 1
          if (dy <= -dx || dy > dx)
            continue;
        }
      } else {

        if (blackBox.orientation() == 0) {
          // skip segments with slope m < -30 or m >= 60 
          if (dy < -dx)
            continue;
          if (dy >= 3 * dx)
            continue;
        } else {
          // skip segments with slope m < -60 or m >= 30 
          if (dy < -3 * dx)
            continue;
          if (dy >= dx)
            continue;
        }
        //        // Skip segments with slope |m| > 1
        //        if (dx == 0 || dy > (hex ? 3 : 1) * dx)
        //          continue;
      }
      startPts.add(s);
      stopPts.add(s);
    }
  }
  private void prepareHotPixels() {
    startNextPixCol = 0;

    if (blackBox.orientation() != 0) {
      for (int i = 0; i < hotPixels.size(); i++) {
        HotPixel h = (HotPixel) hotPixels.get(i);
        IPoint2 pt = blackBox.toSweepSpace(h);
        HotPixel h2 = new HotPixel(pt);
        h2.setNode(h.getNode());
        hotPixels.set(i, h2);
      }
      hotPixels.sort(IPoint2.comparator);
    }
  }
  /**
   * Generate arcs in arrangement by performing a horizontal or vertical sweep
   * over the segments.
   */
  private void bundlePass() {
    //    inf = Inf.create();

    prepareSegmentQueues();
    unmarkAllSegments();

    sweepLine = new SweepStrip();
    SweepStrip.setBlackBox(blackBox);
    segComparator = new SegmentComparator(blackBox, sweepLine);
    segComparator.setTrace(C.vb(DB_SEGCOMPARE));

    tree = new SegTree(sweepLine, segComparator);
    bundleSet = new BundleSet(sweepLine, segComparator);

    prepareHotPixels();

    while (true) {
      Inf.update(inf);
      hpCol = readColumnOfHotPixels();
      if (hpCol == null)
        break;

      sweepLine.moveTo(blackBox.firstStripInPixel(hpCol.x()));

      if (T.update())
        T.msg(sweepLine);

      // Process existing bundles that are entering this column of hot pixels
      step_SplitBundles();

      // Remove stopping segments
      step_StoppingSegments();

      for (int i = 0; i < blackBox.maxStripsPerPixel(); i++)
        step_Sweep();

      // Insert starting segments. Note that it inserts segments starting
      // at the old (unadvanced) sweep position.
      step_StartingSegments();

      // Insert new bundles for segments leaving hot pixels
      step_CreateBundles();
    }
    sweepLine = null;
    bundleSet = null;
  }

  /**
   * Read next column of hot pixels
   * @return true if some pixels were read
   */
  private HotPixelColumn readColumnOfHotPixels() {
    HotPixelColumn col = null;
    int j = startNextPixCol;
    HotPixel prev = null;
    while (j < hotPixels.size()) {
      HotPixel pixel = (HotPixel) hotPixels.get(j);
      if (prev != null && pixel.x != prev.x)
        break;
      if (prev != null)
        HotPixel.join(prev, pixel);
      prev = pixel;
      j++;
    }
    if (j > startNextPixCol) {
      col = new HotPixelColumn(hotPixels, startNextPixCol, j - startNextPixCol);
      startNextPixCol = j;
    }
    return col;
  }

  /**
   * Find a bundle intersecting a particular hot pixel.
   * We are looking for a bundle whose lower bounding segment is <= hot pixel,
   * and whose upper bounding segment >= hot pixel.
   * This can be done in O(lg n) time, by quering the bundle tree,
   * since bundles only intersect within hot pixels.
   * 
   * @param hotPixel
   * @return Bundle, or null if none found
   */
  private Bundle findBundleIntersectingPixel(IPoint2 hotPixel, int hotPixelIndex) {
    final boolean db = tr && C.vb(DB_FINDBUNDLE);

    Bundle ret = null;

    // We could construct a special type of wedge here;
    // for now, simulate using iteration.

    if (db && T.update())
      T.msg("findBundlesIntersectingPixel " + SnapUtils.plot(hotPixel));

    int state = 0;
    for (Iterator it = bundleSet.iterator(); it.hasNext();) {
      Bundle b = (Bundle) it.next();
      Segment sLow = b.getLow();
      Segment sHigh = b.getHigh();

      int res0 = compare(hotPixel, hotPixelIndex, sLow, true);
      int res1 = compare(hotPixel, hotPixelIndex, sHigh, true);

      if (db && T.update())
        T.msg("iterated to " + b + "\n res0=" + res0 + " res1=" + res1);
      if (res0 <= 0 && res1 >= 0) {
        if (state == 2)
          T.err("findBundleIntersectingHotPixel, bundles not contiguous");
        state = 1;
        if (ret == null)
          ret = b;
      } else {
        if (state == 1)
          state = 2;
      }
    }
    return ret;
  }

  /**
   * Process bundles that are entering the sweep column.
   * Iterate through every hot pixel in the column, and test
   * for pixel/bundle intersections.
   */
  private void step_SplitBundles() {
    boolean db = tr && C.vb(DB_SPLITBUNDLES);
    if (db && T.update())
      T.msg("step_SplitBundles\n " + tree);

    // can order be arbitrary?

    int[] ord = MyMath.permutation(hpCol.size(), new Random(1965));

    for (int j = 0; j < hpCol.size(); j++) {
      int i = j;
      // try permutation?
      i = ord[j];
      HotPixel hotPixel = hpCol.get(i);
      if (db && T.update())
        T.msg("splitting bundles intersecting " + SnapUtils.plot(hotPixel));

      // Determine lowest and highest segments intersecting hot pixel
      Segment[] segs = findSegmentsIntersectingPixel(hotPixel, i, true);
      while (true) {
        Inf.update(inf);

        if (db && T.update())
          T.msg(" finding a bundle intersecting " + SnapUtils.plot(hotPixel));
        // find bundle intersecting this pixel
        Bundle b = findBundleIntersectingPixel(hotPixel, i);
        if (b == null)
          break;
        if (db && T.update())
          T.msg(" found: " + b);
        bundleSet.remove(b);
        if (db && T.update())
          T.msg(" removed from set, now\n" + bundleSet);

        int cl = compare(hotPixel, i, b.getLow(), true);
        int ch = compare(hotPixel, i, b.getHigh(), true);

        if (cl < 0) {
          if (db && T.update())
            T.msg(" hot pixel was above bundle low seg " + b.getLow()
                + ";\n creating new");
          Bundle c = new Bundle(b.getOrigin(), b.getLow(), segs[0]);
          bundleSet.add(c);

        }
        if (ch > 0) {
          if (db && T.update())
            T.msg(" hot pixel was below bundle high seg " + b.getHigh()
                + ";\n creating new");
          Bundle c = new Bundle(b.getOrigin(), segs[3], b.getHigh());
          bundleSet.add(c);
        }

        if (segs[1] != null) {
          // Add an arc to the arrangment, since this bundle intersects the hot
          // pixel.
          if (db && T.update())
            T.msg("segL=" + segs[1] + "\nsegH=" + segs[2] + "\n"
                + "adding arc between " + b.getOrigin() + " and " + hotPixel);
          snapArrangement.addArc(b.getOrigin().getNode(), hotPixel.getNode());

          // mark bounding segments of terminating bundle with this hot pixel
          markSegment(segs[1], hotPixel);
          markSegment(segs[2], hotPixel);
        }
      }
    }
  }
  private void unmarkAllSegments() {
    for (int i = 0; i < segs.length; i++) {
      Segment s = segs[i];
      s.setMark(null);
    }
  }

  private void markSegment(Segment s, HotPixel p) {
    if (C.vb(SKIPALLMARKS))
      return;

    HotPixel prevMark = s.getMark();
    if (prevMark != p) {

      boolean db = C.vb(DB_VERTEDGE);
      if (db && T.update())
        T.msg("marking " + s + " with " + p + " (was " + prevMark + ")");

      s.setMark(p);

      for (int pass = 0; pass < 2; pass++) {
        HotPixel h0 = (pass == 0) ? p : p.prev();
        HotPixel h1 = (pass == 0) ? p.next() : p;
        if (h0 == null || h1 == null)
          continue;

        // if we've already generated an edge between these pixels, skip
        if (h0.vertEdge())
          continue;

        // does segment intersect both hot pixels?
        Range r = blackBox.getClipRangeWithinPixelColumn(s, p.x);
        if (db && T.update())
          T.msg("seeing if segment " + s + " within pc=" + p.x
              + "\n intersects both " + h0.y + " and " + h1.y);

        if (r.contains(h0.y) && r.contains(h1.y)) {
          if (db && T.update())
            T.msg("creating arc between " + h0 + " and " + h1);
          h0.createLink();
          snapArrangement.addArc(h0.getNode(), h1.getNode());
        }
      }
    }
  }

  /**
   * Pop any segment stopping point events from the endpoint queue that 
   * occur at the current sweep column's position. 
   * 
   * add segment to
   * list of completed segments, 
   * so when the column is complete we can remove
   * them from the tree
   */
  private void step_StoppingSegments() {
    boolean db = tr && C.vb(DB_STOPPINGSEGMENTS);

    if (db && T.update())
      T.msg("step_StoppingSegments");

    while (true) {
      Segment s = stopPts.peek();
      if (s == null)
        break;

      IPoint2 ep = s.pt(1);
      if (ep.x != hpCol.x())
        break;
      if (db && T.update())
        T.msg("processing stoppoint event for " + s);
      stopPts.pop();
      tree.remove(s);
    }
  }

  /**
   * Process all intersection events that occur in the current sweep column,
   * then advance sweep line to right side of column.
   */
  private void step_Sweep() {
    boolean db = tr && C.vb(DB_SWEEP);
    if (db && T.update())
      T.msg("step_Sweep");

    DQueue intersectionEvents = new DQueue();
    tree.extractIntersectEvents(intersectionEvents);

    if (db && T.update())
      T.msg("processing intercept event stack: " + intersectionEvents);

    while (!intersectionEvents.isEmpty()) {
      IsectEvent e = (IsectEvent) intersectionEvents.pop();
      if (db && T.update())
        T.msg(e);

      Segment a = e.a(), b = e.b();

      // if the segments in this event are no longer neighbors, ignore.
      Segment sb = a.neighbor(true);
      if (sb == null || sb.id() != b.id())
        continue;

      // process marks
      if (!C.vb(SKIPSWEEPMARK)) {
        HotPixel ma = a.getMark();
        HotPixel mb = b.getMark();
        if (mb != null) {
          if (mb.x == hpCol.x()) {
            //                    if (a.getMark() != null && a.getMark().x != mb.x) 
            //                      Streams.out.println("mark "+a+" with "+mb+", was +"+a.getMark());
            markSegment(a, mb);
          }
        }
        if (ma != null) {
          if (ma.x == hpCol.x()) {
            //                      if (b.getMark() != null && b.getMark().x != ma.x) 
            //                        Streams.out.println("mark "+b+" with "+ma+", was +"+b.getMark());
            markSegment(b, ma);
          }
        }
      }

      // exchange positions of the two segments using their handles
      Segment.exchangeTreePositions(a, b);

      // Invalidate the heap information for every page on paths
      // from each segment to the tree root. We use node coloring
      // to stop if we have already invalidated the page.

      tree.invalidateSegmentPath(a);
      tree.invalidateSegmentPath(b);

      // Predict intersection events that may occur between segments
      // that are now neighbors.
      for (int nbr = 0; nbr < 2; nbr++) {
        Segment s = (nbr == 0) ? a : b;
        Segment sl = (nbr == 0) ? s : s.neighbor(false);
        e = new IsectEvent(sl);
        if (e.valid() && e.occursWithin(sweepLine))
          intersectionEvents.push(e);
      }
    }
    // Advance sweep column 
    sweepLine.moveTo(1 + sweepLine.stripX());
  }

  /**
   * Insert segments that are starting in this pixel column
   */
  private void step_StartingSegments() {
    boolean db = tr && C.vb(DB_STARTINGSEGMENTS);

    if (db && T.update())
      T.msg("step_StartingSegments");
    while (true) {
      Segment s = startPts.peek();
      if (s == null || s.pt(0).x != hpCol.x())
        break;
      startPts.pop();
      if (db && T.update())
        T.msg("processing startpoint event for " + s);
      tree.add(s);
    }
  }

  /**
   * Compare segment to hot pixel.
   * If it passes below the hot pixel, or intersects a lower one beforehand,
   * the seg is less than the hot pixel.
   * If it passes above the hot pixel, or intersects a higher one beforehand,
   * the seg is greater than the hot pixel.
   * @param hotPixel
   * @param hotPixelIndex
   * @param seg
   * @return 0 if equal, -1 if seg is lower, 1 if seg is higher
   */
  private int compare(IPoint2 hotPixel, int hotPixelIndex, Segment seg,
      boolean entering) {
    boolean db = tr && C.vb(DB_COMPAREPIXSEG);

    int ret = 0;
    int hMax = Integer.MAX_VALUE;
    int hMin = Integer.MIN_VALUE;
    if (hotPixelIndex + 1 < hpCol.size())
      hMax = hpCol.get(hotPixelIndex + 1).y;
    if (hotPixelIndex - 1 >= 0)
      hMin = hpCol.get(hotPixelIndex - 1).y;

    Range r = blackBox.getClipRangeWithinPixelColumn(seg, hotPixel.x);
    boolean posSlope = seg.y1() >= seg.y0();
    if (!r.contains(hotPixel.y))
      ret = MyMath.sign(r.y0() - hotPixel.y);

    if (r.contains(hMin) && !(entering ^ posSlope))
      ret = -1;
    if (r.contains(hMax) && (entering ^ posSlope))
      ret = 1;

    if (db && T.update())
      T.msg("compare hot pixel " + SnapUtils.plot(hotPixel) + "\n with seg "
          + seg + "\n hMin=" + hMin + "\n hMax=" + hMax + "\n range=" + r
          + "\n returning " + ret);

    return ret;
  }

  /**
   * Find bounding segments first entering a particular hot pixel.
   * The segments must enter the hot pixel before any other hot pixels within
   * the column.
   * 
   * Since segments can only intersect within hot pixels, we can determine
   * these two segments in O(lg n) time.
   * 
   * @param pc : pixel column
   * @param hotPixel : hot pixel
   * @param hotPixelIndex : index of hot pixel
   * @return array of segments: 
   *   0, last segment below pixel
   *   1, first segment entering pixel
   *   2, last segment entering pixel
   *   3, first segment above pixel
   */
  private Segment[] findSegmentsIntersectingPixel(IPoint2 hotPixel,
      int hotPixelIndex, boolean entering) {
    boolean db = tr && C.vb(DB_FINDSEGMENTS);

    Segment[] sa = new Segment[4];

    if (db && T.update())
      T.msg("findSegmentsIntersectingPixel " + SnapUtils.plot(hotPixel)
          + " entering=" + entering);

    // We could construct a special type of wedge here, that locates
    // the above two segs.  For now, simulate using iteration.

    int state = 0; // 0: before segs 1:within segs 2:after segs
    for (Iterator it = tree.iterator(null); it.hasNext();) {
      Segment s = (Segment) it.next();

      int res = compare(hotPixel, hotPixelIndex, s, entering);

      if (db && T.update())
        T.msg(" state=" + state + " iterated to " + s + " compare=" + res);
      if (res < 0) {
        sa[0] = s;
      } else if (res == 0) {
        if (state > 1)
          T.err(" bundle not contiguous");
        if (state == 0) {
          state = 1;
          sa[1] = s;
        }
        sa[2] = s;
      } else {
        if (state < 2) {
          sa[3] = s;
          state = 2;
        }
      }
    }
    if (db && T.update())
      T.msg(" returning\n" + DArray.toString(sa, true));
    return sa;
  }

  /**
   * Create new bundles for segments emerging from right side of hot
   * pixels in column we've just finished
   */
  private void step_CreateBundles() {

    boolean db = tr && C.vb(DB_CREATEBUNDLES);

    if (db && T.update())
      T.msg("step_CreateBundles\n " + tree);

    for (int i = 0; i < hpCol.size(); i++) {
      HotPixel hotPixel = hpCol.get(i);

      if (db && T.update())
        T.msg(" creating bundle for segs leaving hot pixel "
            + SnapUtils.plot(hotPixel));

      Segment[] segs = findSegmentsIntersectingPixel(hotPixel, i, false);
      if (segs[1] != null) {
        if (db && T.update())
          T.msg("creating new bundle from " + segs[1] + " through " + segs[2]);

        Bundle bundle = new Bundle(hotPixel, segs[1], segs[2]);
        bundleSet.add(bundle);
      }
    }
  }

  // Algorithm output: SnapArrangement being constructed
  private SnapArrangement snapArrangement;

  // B+ tree containing sorted active segments, plus the heap of intersection
  // events
  private SegTree tree;

  // Queues for segment startpoint, endpoint events
  private SegPriorityQueue startPts, stopPts;

  // Current sweep line
  private SweepStrip sweepLine;

  // If not null, the compare function for sorting segments
  private SegmentComparator segComparator;

  // Must be true to enable algorithm tracing messages:
  private static boolean tr;

  // Sorted set of active bundles
  private BundleSet bundleSet;

  // sorted list of hot pixels over all columns
  private DArray hotPixels = new DArray();

  // index of start of next hot pixel column within hotPixels array
  private int startNextPixCol;

  // Segments being snapped.  These may have been flipped, for the vertical
  // sweep, so we can use the horizontal sweep code and just plot things
  // flipped instead.
  private Segment[] segs;

  // BlackBox being used for snapping
  private static BlackBox blackBox;

  // true if doing hex grid version of algorithm
  private boolean hex;

  // List of problems detected in snapList
  private FPoint2[] snapListProblems;

  // current hot pixel column being processed
  private HotPixelColumn hpCol;

  // Debugging: if not null, used to detect infinite loops
  private Inf inf;

  // If not null, segments used to perform simple snapping
  private Segment[] simpleSegs;
}
