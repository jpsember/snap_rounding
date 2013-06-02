package snap;

import base.*;
import snaptree.*;
import testbed.*;
import java.awt.*;
import java.util.*;

public class SnapOper implements Globals, TestBedOperation {

  /*! .enum  .private 1500
  db_sweepproc _ db_updatesweepcolumnpos db_predict_nbr 
  db_startpt db_endpt 
  plot_original show_hotpix _ plot_snapped plot_snaplist
  show_list_problems simple db_hotpixelproc db_snapproc rndtest
  db_vert _ panel _ create_errs timed_run total_time _ _ _ oldhex oldhl
  nowarm sreset usestats
  */

    private static final int DB_SWEEPPROC     = 1500;//!
    private static final int DB_UPDATESWEEPCOLUMNPOS = 1502;//!
    private static final int DB_PREDICT_NBR   = 1503;//!
    private static final int DB_STARTPT       = 1504;//!
    private static final int DB_ENDPT         = 1505;//!
    private static final int PLOT_ORIGINAL    = 1506;//!
    private static final int SHOW_HOTPIX      = 1507;//!
    private static final int PLOT_SNAPPED     = 1509;//!
    private static final int PLOT_SNAPLIST    = 1510;//!
    private static final int SHOW_LIST_PROBLEMS = 1511;//!
    private static final int SIMPLE           = 1512;//!
    private static final int DB_HOTPIXELPROC  = 1513;//!
    private static final int DB_SNAPPROC      = 1514;//!
    private static final int RNDTEST          = 1515;//!
    private static final int DB_VERT          = 1516;//!
    private static final int PANEL            = 1518;//!
    private static final int CREATE_ERRS      = 1520;//!
    private static final int TIMED_RUN        = 1521;//!
    private static final int TOTAL_TIME       = 1522;//!
    private static final int OLDHEX           = 1526;//!
    private static final int OLDHL            = 1527;//!
    private static final int NOWARM           = 1528;//!
    private static final int SRESET           = 1529;//!
    private static final int USESTATS         = 1530;//!
/* !*/

  public void addControls() {
    C.sOpenTab("Snap: Sweep");
    C.sStaticText("Performs plane sweep to snap segments to grid");
    {
      C.sOpenTabSet(PANEL);
      C.sOpenTab("Display / Logic");
      {
        C.sOpen();
        C.sCheckBox(PLOT_ORIGINAL, "Plot original", null, true);
        C.sCheckBox(PLOT_SNAPPED, "Plot snapped", null, true);
        C.sCheckBox(SHOW_HOTPIX, "Plot hot pixels", null, false);
        C.sCheckBox(PLOT_SNAPLIST, "Plot snap list", null, false);
        C.sNewColumn();
        C.sCheckBox(SHOW_LIST_PROBLEMS, "Find problems",
            "Detect problems with snapped arrangement", false);
        C.sCheckBox(SIMPLE, "Simple",
            "Generate using simple, slow algorithm; based on 'old' definition",
            false);
        C
            .sCheckBox(
                NOWARM,
                "Don't snap warm",
                "Doesn't snap warm segments to hot pixels (simple method only);\nfor finding examples where snap rounding is necessary",
                false);
        C.sHide();
        C.sCheckBox(OLDHEX, "Old hex method", null, false);
        C.sHide();
        C.sCheckBox(OLDHL, "Old HeatList method", null, false);
        C.sHide();
        C.sCheckBox(CREATE_ERRS, "Create errors",
            "Intentionally introduce errors "
                + "to test problem detection (simple alg only)", false);
        C.sClose();
      }
      C.sCloseTab();
      C.sOpenTab("Tracing");
      {
        C.sOpen();

        C.sCheckBox(DB_UPDATESWEEPCOLUMNPOS, "calcNextSweep", null, false);
        C.sHide();
        C.sCheckBox(DB_PREDICT_NBR, "predictNbrEvents", null, false);
        C.sCheckBox(DB_STARTPT, "startpt events", null, false);
        C.sCheckBox(DB_ENDPT, "stoppt events", null, false);

        C.sNewColumn();

        C.sCheckBox(DB_SWEEPPROC, "sweep process", null, false);
        C.sCheckBox(DB_HOTPIXELPROC, "hot pixel process", null, false);
        C.sCheckBox(DB_SNAPPROC, "snap process", null, false);
        C.sCheckBox(DB_VERT, "vertical segments", null, false);

        C.sClose();
      }
      C.sCloseTab();
      C.sCloseTabSet();

      if (true && !TestBed.isApplet()) {
        C.sOpen();
        C.sButton(TIMED_RUN, "Timed Search",
            "Generates random segments, searches for problems during snapping");
        C.sNewColumn();
        C.sIntSpinner(TOTAL_TIME, "# seconds:",
            "Amount of time to spend looking", 1, 3600, 10, 5);
        C.sClose();
      }

      //      C.sTextArea(STATS, "Statistics","Displays statistics for last operation",
      //          true,null);
      //      
      if (false && !TestBed.isApplet()) {
        C.sButton(RNDTEST, "Compare",
            "Compare random-generated segments with simple algorithm");
      }
      C.sCheckBox(USESTATS, "Gather stats", null, false);
      C.sButton(SRESET, "Reset stats", "Reset accumulated statistics");

    }
    C.sCloseTab();
  }

  /**
   * Perform snap rounding on array of Segments
   * @param segs
   * @return DArray of any problems found during snapping
   */
  public FPoint2[] doSnap(Segment[] segs) {
    // make sure tracing is off, and we are finding problems
    C.setb(SHOW_LIST_PROBLEMS, true);
    Tools.warn("no longer clearing trace step");
    //    C.seti(TRACESTEP, 0);

    this.segs = segs;
    runAlgorithm();
    return snapListProblems;
  }

  /**
   * Plot results after runAlgorithm(), which may have been interrupted
   */
  public void paintView() {

    //    vp V = TestBed.;
    if (sweepLine != null && blackBox != null
        && sweepLine.stripX() > Integer.MIN_VALUE
        && sweepLine.stripX() < Integer.MAX_VALUE) {
      blackBox.renderSweepLine(sweepLine.stripX());
    }

    //    boolean withLabels = Main.labelSegments();
    boolean snapped = C.vb(PLOT_SNAPPED);

    if (C.vb(PLOT_ORIGINAL)) {
      Color norm = snapped ? MyColor.get(MyColor.LIGHTGRAY, .4) : MyColor.get(
          MyColor.BLUE, .32);
      Color hl = MyColor.get(MyColor.BLUE, .44);

      for (int i = 0; i < segs.length; i++) {
        Segment s = segs[i];
        s.render(s.getPageId() == 0 ? norm : hl, -1, -1);
      }
    }

    SnapUtils.plotSnappedSegments(Main.grid(), segs,
        C.vb(PLOT_SNAPPED) ? MyColor.get(MyColor.BLUE, .31) : null, C
            .vb(SHOW_HOTPIX) ? MyColor.get(MyColor.GREEN, .4) : null, C
            .vb(PLOT_SNAPLIST) ? MyColor.get(MyColor.BLUE, .5) : null);
    if (snapListProblems != null) {
      V.pushColor(Color.red);
      SnapUtils.plotSnapErrors(Main.grid(), snapListProblems);
      V.popColor();
    }
    if (C.vb(SIMPLE)) {
      //      if (C.vb(TRACEPLOT))
      {
        V.pushScale(.7);
        V.draw("(simple algorithm)", 100, 98, TX_BGND | TX_FRAME | TX_CLAMP);
        V.popScale();
      }
    }
  }

  private boolean parityForRNDTEST;

  public void processAction(TBAction a) {
    if (a.code == TBAction.CTRLVALUE) {
      switch (a.ctrlId) {
      case TIMED_RUN:
        {
          long startTime = System.currentTimeMillis();
          DArray sa;
          int nSets = 0;
          int timeLeft = 0;
          do {
            nSets++;
            sa = Generator.generate(1);
            Segment[] segs = SnapEdSegment.getSegments(blackBox, sa.iterator(),
                true);
            timeLeft = Math.max(0, (int) (C.vi(TOTAL_TIME) * 1000 - (System
                .currentTimeMillis() - startTime)) / 1000);

            Streams.out.println("set# " + Tools.f(nSets, 5) + " seed "
                + Tools.fh(Generator.lastSeed()) + " time left=" + timeLeft);
            FPoint2[] probs = doSnap(segs);
            if (probs.length != 0) {
              Streams.out.println("*** problem found");
              break;
            }
          } while (timeLeft > 0);
          Editor.replaceAllObjects(sa);
          //          Main.replaceItems(sa);
        }
        break;
      case SRESET:
        tree.resetStats();
        Stats.clearHistory();
        break;
      case RNDTEST:
        if (parityForRNDTEST)
          C.toggle(SIMPLE);
        else {
          C.setb(SIMPLE, false);
          Main.doRandomize(1);
        }
        parityForRNDTEST ^= true;
        break;
      }
    }
  }

  /**
   * Run algorithm, stopping at trace point if necessary
   */
  public void runAlgorithm() {
    //    Main.plotGrid();
    segs = Main.getSegments();
    snapListProblems = null;
    sweepLine = null;
    heatSet = null;
    blackBox = Main.blackBox();
    BlackBox.setTransform(blackBox);
    blackBox.setOrientation(0);

    Stats.reset();
    if (C.vb(SIMPLE) || Main.grid() instanceof TriGrid) {
      SnapUtils.performSimpleSnap(segs, blackBox, C.vb(CREATE_ERRS), C
          .vb(NOWARM));
    } else {
      if (blackBox instanceof BlackBoxTriStrip)
        T.msg("tri grid not supported");

      sweepLine = new SweepStrip();
      tree = new SegTree(blackBox, sweepLine);
      startPts = new SegPriorityQueue(blackBox, 0);
      stopPts = new SegPriorityQueue(blackBox, 1);
      heatSet = new HeatListSet();

      for (int i = 0; i < segs.length; i++) {
        Segment s = segs[i];
        Stats.event(Stats.NSEGS);

        s.resetSnappedFlag();

        if (blackBox.isVertical(s)) {
          startPts.add(s);
          continue;
        }
        startPts.add(s);
        stopPts.add(s);
      }

      if (C.vb(DB_STARTPT) && T.update()) {
        T.msg("startPts=\n" + startPts);
      }
      if (C.vb(DB_ENDPT) && T.update()) {
        T.msg("stopPts=\n" + stopPts);
      }

      sweepLine.moveTo(getNextSweepColumnPosition());

      while (sweepLine.stripX() != Integer.MAX_VALUE)
        doSweepProcess();

      if (C.vb(USESTATS)) {

        Streams.out.println(getStats());
        //        Streams.out.println(Stats.summary());

      }

      //      C.sets(STATS,Stats.summary());
    }
    if (C.vb(SHOW_LIST_PROBLEMS)) {
      SnapArrangement s = new SnapArrangement(segs);

      snapListProblems = SnapUtils.findSnapErrors(Main.grid(), s); // segs);
    }
  }

  private String getStats() {
    StringBuilder sb = new StringBuilder();
    sb.append("events:  " + tree.statEventCount() + "\n");
    sb.append("edges:   " + tree.statEdgeCount() + "\n");
    sb.append("maxEdges:" + tree.statMaxEdgeCount() + "\n");

    double N = tree.statCalcN();
    double k = tree.statEdgeCount() / N;
    double h = tree.statHotPixels();
    sb.append("N:       " + Tools.f(N) + "\n");
    sb.append("k:       " + Tools.f(k) + "\n");
    sb.append("h:       " + Tools.f(h) + "\n");
    sb.append("k/h:     " + Tools.f(k / h) + "\n");

    return sb.toString();
  }

  private void doSweepProcess() {
    boolean db = C.vb(DB_SWEEPPROC);

    if (T.update())
      T.msg("SweepProcess " + sweepLine + " " + tree);

    // Construct list of segments starting at left side of this strip.
    // Also, constructs list of vertical segments.
    HeatedSegmentList hlStart = processStartingSegs();
    if (!hlStart.isEmpty())
      if (db && T.update())
        T.msg("hlStart=" + hlStart);

    // construct list of segments stopping at the right side of this strip
    HeatedSegmentList hlStop = processStoppingSegs();
    if (!hlStop.isEmpty())
      if (db && T.update())
        T.msg("hlStop=" + hlStop);

    // A list of segments that are intersecting within the current sweep
    // column.
    DQueue intersectionEvents = new DQueue();

    // Construct HeatedSegmentList for intersecting segments.
    // We store bidirectional links between these entries and segments,
    // to allow O(1) exchanging positions.
    HeatedSegmentList hlIntersect = tree
        .extractIntersectEvents(intersectionEvents);
    if (!hlIntersect.isEmpty())
      if (db && T.update())
        T.msg("hlIntersect=" + hlIntersect + "\n" + intersectionEvents);

    HeatedSegmentList hlVert = processVerticalSegments();
    if (!hlVert.isEmpty())
      if (db && T.update())
        T.msg("hlVert=\n" + hlVert);

    // merge the three HeatLists together into one representing
    // the ordering of segments entering the left side of the sweep strip

    HeatedSegmentList hl = hlStart;
    hl = HeatedSegmentList.merge(blackBox, hl, hlVert, "start+vert");
    hl = HeatedSegmentList.merge(blackBox, hl, hlStop, "temp+stop");
    hl = HeatedSegmentList.merge(blackBox, hl, hlIntersect, "leftside");

    if ((hlStart.isEmpty() ? 0 : 1) + (hlStop.isEmpty() ? 0 : 1)
        + (hlIntersect.isEmpty() ? 0 : 1) > 1) {
      if (db && T.update())
        T.msg("Merged start, stop, isect lists=\n" + hl);
    }

    // make a copy of the left side HeatList, which will get modified
    // as we process intersections.
    // This also makes all the segments point to the new HeatList entries.
    HeatedSegmentList hr = new HeatedSegmentList(hl, "rightside");

    if (db && T.update())
      T.msg("processing intercept event stack");

    while (!intersectionEvents.isEmpty()) {

      IsectEvent e = (IsectEvent) intersectionEvents.pop();

      Segment a = e.a(), b = e.b();

      // if the segments in this event are no longer neighbors, ignore.
      Segment sb = a.neighbor(true);
      if (sb == null || sb.id() != b.id()) {
        if (db && T.update()) {
          String msg = "segments " + a + " and " + b
              + " no longer neighbors; neighbor to first is " + sb;
          T.msg(msg);
        }
        continue;
      }

      Stats.event(Stats.ISECT);
      IPoint2 pt = e.iPt(false);
      if (db && T.update())
        T.msg("proc intersect event at " + pt + " between " + a + " and " + b);

      {
        if (db && T.update())
          T.msg(" adding hotPixel=" + pt + " to " + a + " and " + b
              + ", sweepLine=" + sweepLine);
        a.addToHeatRange(blackBox, sweepLine.stripX(), pt);
        b.addToHeatRange(blackBox, sweepLine.stripX(), pt);
      }
      if (db && T.update())
        T.msg("exchanging segments " + a + " and " + b + "\n" + a.getPageId()
            + "\n" + b.getPageId());

      // exchange positions of the two segments using their handles
      Segment.exchangeTreePositions(a, b);

      // exchange positions of segments within right-side heat list
      {
        if (db && T.update())
          T.msg("before exchange:" + hr);
        hr.exchangeSegPositions(a, b);

        if (db && T.update())
          T.msg("after exchange: " + hr);
      }

      if (db && T.update())
        T.msg("after exchange: " + tree);

      // Invalidate the heap information for every page on paths
      // from each segment to the tree root. We use node coloring
      // to stop if we have already invalidated the page.
      tree.invalidateSegmentPath(a);
      tree.invalidateSegmentPath(b);

      // Predict intersection events that may occur between segments
      // that are now neighbors.
      predictNeighborEvents(intersectionEvents, a, true);
      predictNeighborEvents(intersectionEvents, b, false);

    }

    if (db && T.update())
      T.msg(" after sweep strip, hr=\n" + hr);

    // merge brackets for vertical segments into left HeatList.  
    // There is no point in doing this before the right HeatList is constructed,
    // as the vertical segments do not extend to that side of the strip.
    hl = mergeBracketsForVerticalSegments(hl);

    // add the left- and right-side heated segment lists to the 
    // heatSet queue, for later processing by the Hot Pixel and Snap processes.
    heatSet.add(blackBox, sweepLine.stripX(), hl, hr);

    int lastSweepPosition = sweepLine.stripX();

    int nextSweepColumn = getNextSweepColumnPosition();
    if (db && T.update())
      T.msg("sweep: curr=" + lastSweepPosition + ", next=" + nextSweepColumn);

    int firstNewPC = blackBox
        .firstPixelColumnIntersectingStrip(lastSweepPosition);
    int lastNewPC = blackBox
        .lastPixelColumnIntersectingStrip(lastSweepPosition);
    int endOfNewPC = blackBox
        .firstPixelColumnIntersectingStrip(nextSweepColumn);

    lastNewPC = Math.min(lastNewPC, endOfNewPC - 1);

    if (firstNewPC <= lastNewPC && db && T.update())
      T.msg(" doing hot pixel, snap processes for pc=" + firstNewPC
          + " through " + lastNewPC);
    for (int pc = firstNewPC; pc <= lastNewPC; pc++) {
      doHotPixelProcess(pc);
      doSnapProcess(pc);
    }

    // move to next strip, so tree is valid again
    sweepLine.moveTo(sweepLine.stripX() + 1);
    removeEndingSegments(hlStop);

    sweepLine.moveTo(nextSweepColumn);
  }

  /**
   * Construct horizontal brackets for vertical segments,
   * and merge them into a HeatedSegmentList.  Also, register the 
   * associated endpoint with the bracket.
   * @param hl : HeatedSegmentList to merge brackets into
   * @return merged HeatedSegmentList
   */
  private HeatedSegmentList mergeBracketsForVerticalSegments(
      HeatedSegmentList hl) {
    boolean db2 = C.vb(DB_VERT);
    if (db2 && T.update())
      T.msg("merging horizontal brackets for vert segs");

    VertSegPriorityQueue vq = new VertSegPriorityQueue();
    for (Iterator it = verticalSegments.iterator(); it.hasNext();) {
      Segment sv = (Segment) it.next();
      if (db2 && T.update())
        T.msg(" hlVert seg=" + sv);
      vq.add(sv);
    }

    //    if (HeatedSegmentList.OLDMETHOD) {
    //      HeatedSegmentList brackets = new HeatedSegmentList(sweepLine.stripX(),
    //          "brackets for vert segs");
    //
    //      while (!vq.isEmpty()) {
    //        VertSegEntry ve = vq.pop();
    //        Segment bracket = ve.constructBracket();
    //        bracket.addToHeatRange(blackBox, sweepLine.stripX(), ve.pt());
    //        brackets.add(bracket, false);
    //      }
    //      hl = HeatedSegmentList.merge(blackBox, hl, brackets, "left+brackets");
    //      return hl;
    //    }

    HeatedSegmentList brackets = new HeatedSegmentList(sweepLine.stripX(),
        "brackets for vert segs"); // ,false);

    while (!vq.isEmpty()) {
      VertSegEntry ve = vq.pop();
      Segment bracket = ve.constructBracket();
      bracket.addToHeatRange(blackBox, sweepLine.stripX(), ve.pt());
      brackets.add(bracket);
    }
    hl = HeatedSegmentList.merge(blackBox, hl, brackets, "left+brackets");
    return hl;
  }

  /**
   * Remove segments that are stopping within current pixel column
   * @param stoppingSegs : HeatedSegmentList that will contain
   *  any segments that are stopping (and perhaps others, such as neighbors)
   */
  private void removeEndingSegments(HeatedSegmentList stoppingSegs) {
    boolean db = C.vb(DB_ENDPT);

    int pc = blackBox.firstPixelColumnIntersectingStrip(sweepLine.stripX());
    for (HeatedSegmentListIterator it = stoppingSegs.iterator(); it.hasNext();) {
      Segment s = it.next();
      // is this a segment that is stopping in the strip?
      if (s.x1() != pc)
        continue;
      if (db && T.update())
        T.msg(" removing stopping segment " + s + "\n from tree:" + tree);
      tree.remove(s);
    }
    if (db && T.update())
      T.msg(" after removing, tree=\n" + tree);
  }

  /**
   * Get next sweep strip position; nearest of 
   * [] segment startpoints 
   * [] segment stoppoints, minus 1
   * [] segment/segment intersections reported by tree
   * [] Integer.MAX_VALUE, if done
   * 
   * @return next sweep strip position 
   */
  private int getNextSweepColumnPosition() {
    boolean db = C.vb(DB_UPDATESWEEPCOLUMNPOS);

    if (db && T.update())
      T.msg("updateSweepColumnPos, currently " + sweepLine + "\n " + tree);

    int sx = Integer.MAX_VALUE;

    IsectEvent d = tree.peekNextIsectEvent();
    if (d != null && d.valid()) {
      sx = d.iPt(true).x;
      if (db && T.update())
        T.msg("next sweepLinePos init to " + sx + " for " + d);
    }

    Segment s = startPts.peek();
    if (s != null) {
      int px = blackBox.toStripSpace(s.pt(0)).x;
      if (px < sx) {
        sx = px;
        if (db && T.update())
          T.msg("next sweepLinePos set to " + sx + " for startPt " + s.str());
      }
    }

    s = stopPts.peek();
    if (s != null) {
      // stop at stopping point - 1, so it stops at the right edge of the strip
      int px = blackBox.toStripSpace(s.pt(1)).x - 1;
      if (px < sx) {
        sx = px;
        if (db && T.update())
          T.msg("next sweepLinePos set to " + sx + " for stopPt " + s);
      }
    }
    return sx;
  }

  /**
   * Pop any endpoints from the start point queue that are occurring in this
   * sweep column.
   *   [] add them to the tree 
   *   [] anchor the segment to its startpoint
   * Vertical segments are not treated in this way; instead, the
   * verticalSegments list is initialized to contain them.
   * @return HeatedSegmentList of these segments, including their neighbors
   */
  private HeatedSegmentList processStartingSegs() {

    boolean db = C.vb(DB_STARTPT);

    SegmentComparator segComparator = new SegmentComparator(blackBox, sweepLine);
    SortedSet set = new TreeSet(segComparator);
    verticalSegments = new DArray();

    while (true) {
      Segment s = startPts.peek();
      if (s == null)
        break;
      IPoint2 ep = s.pt(0);

      int px = blackBox.toStripSpace(ep).x;

      if (!(sweepLine.stripX() == px)) {
        if (db && T.update())
          T.msg("sweepLine doesn't contain segment " + s.toString(true));
        break;
      }
      startPts.pop();
      Stats.event(Stats.ENDPT, "start");
      if (db && T.update()) {
        T.msg("processing startpoint event for " + s);
      }
      if (blackBox.isVertical(s)) {
        if (db && T.update())
          T.msg("vert/point segment");
        verticalSegments.add(s);
        continue;
      }

      if (db && T.update())
        T.msg(" adding " + ep.y + " to snap range for " + s);
      s.addToHeatRange(blackBox, sweepLine.stripX(), ep);

      tree.add(s);
      set.add(s);
      if (db && T.update())
        T.msg("added segment " + s + ", tree now\n" + tree);
    }

    HeatedSegmentList hl = new HeatedSegmentList(sweepLine.stripX(),
        "startSegs");
    for (Iterator it = set.iterator(); it.hasNext();) {
      Segment s = (Segment) it.next();
      hl.add(s);
    }
    if (db && T.update())
      T.msg("processStartingSegs, returning\n" + hl);
    return hl;
  }

  /**
   * Pop any segment stopping point events from the endpoint queue that 
   * occur at the right side of the current sweep strip.
   * 
   * @return HeatList to construct of stopping segs, and immediate neighbors,
   *  in the order they appear as they enter the strip
   */
  private HeatedSegmentList processStoppingSegs() {
    boolean db = C.vb(DB_ENDPT);
    if (db && T.update())
      T.msg("processStoppingSegs for " + sweepLine + "\n tree=\n" + tree);

    SegmentComparator segComparator = new SegmentComparator(blackBox, sweepLine);

    SortedSet set = new TreeSet(segComparator);

    while (true) {
      Segment s = stopPts.peek();
      if (db && T.update())
        T.msg(" peek seg=" + s);
      if (s == null)
        break;

      IPoint2 endPt = s.pt(1);
      IPoint2 ep = blackBox.toStripSpace(endPt);
      int epx = ep.x;

      if (db && T.update())
        T.msg(" ep=" + ep + " epx=" + epx + " sweepPos=" + sweepLine.stripX());

      if (epx != sweepLine.stripX() + 1)
        break;

      if (db && T.update()) {
        T.msg("processing stoppoint event for " + s);
      }
      stopPts.pop();
      Stats.event(Stats.ENDPT, "stop");
      set.add(s);

      if (db && T.update())
        T.msg(" adding " + ep.y + " to snap range for stopping seg " + s + " "
            + sweepLine);
      s.addToHeatRange(blackBox, sweepLine.stripX(), endPt);
    }

    HeatedSegmentList hl = new HeatedSegmentList(sweepLine.stripX(),
        "stopSegs", true);
    for (Iterator it = set.iterator(); it.hasNext();) {
      Segment s = (Segment) it.next();
      if (db && T.update())
        T.msg("processStoppingSegs, adding " + s + "\n tree is\n" + tree);
      hl.add(s);
    }
    if (db && T.update())
      T.msg("processStoppingSegs, returning\n" + hl);
    return hl;
  }

  /**
   * Examine a neighboring pair of segments to see if they will intersect in
   * this sweep column. If so, push an intersection event onto the stack.
   * 
   * @param s :
   *          One of the segments in the pair
   * @param isUpper :
   *          true if s is the upper segment in the pair (vs lower)
   */
  private void predictNeighborEvents(DQueue intersectionEvents, Segment s,
      boolean isUpper) {

    boolean db = C.vb(DB_PREDICT_NBR);

    if (db && T.update()) {
      T.msg("predictNeighborEvents for " + s);
    }

    Segment sl = isUpper ? s : s.neighbor(false);
    IsectEvent e = new IsectEvent(sl);
    if (e.valid()) {
      if (db && T.update())
        T.msg(" potential isect= " + e);
      if (e.occursWithin(sweepLine)) {
        if (db && T.update()) {
          T.msg(" adding intersection event " + e);
        }
        intersectionEvents.push(e);
      }
    }
  }

  /**
   * Process vertical segments during sweep process.
   * Construct an ordered list of start/end points, then use this list
   * to distribute hot pixels for intersections between these vertical
   * segments and the existing non-vertical segments in the tree.
   * @return HeatedSegmentList of neighbors of bracket segments 
   *  for vertical segments, and any segments that intersected a vertical segment 
   */
  private HeatedSegmentList processVerticalSegments() {
    boolean db = C.vb(DB_VERT);
    if (db && T.update())
      T.msg("constructVerticalSegmentHeatList");

    VertSegPriorityQueue vs = new VertSegPriorityQueue();
    for (Iterator hi = verticalSegments.iterator(); hi.hasNext();) {
      Segment s = (Segment) hi.next();
      vs.add(s);
    }

    // Detect intersections of segments within the tree with these
    // ranges, adding hot pixels as required, and generating a new
    // HeatedSegmentList to be merged with others.

    HeatedSegmentList heatList = new HeatedSegmentList(sweepLine.stripX(),
        "neighbors of vert");

    Segment segVert = null;
    Segment segAboveBracket = null;
    Segment prevSTree = null;

    int prevY = Integer.MIN_VALUE;

    while (true) {
      if (db && T.update())
        T.msg("procVert loop, lasty=" + prevY + ", segVert=" + segVert);
      if (segVert == null) {
        if (vs.isEmpty())
          break;

        VertSegEntry ve = vs.pop();
        if (db && T.update())
          T.msg(" popped " + ve);

        // if popped upper endpoint, ignore
        if (ve.ptIndex() == 1) {
          if (db && T.update())
            T.msg(" upper endpoint, ignoring");
          continue;
        }

        // if this segment doesn't extend above last end point reached,
        // ignore
        if (ve.seg().y1() <= prevY) {
          if (db && T.update())
            T.msg(" doesn't extend above last end point reached, ignoring");
          continue;
        }

        // if this segment contains last end point reached,
        // just switch to it without choosing new seg from tree
        if (ve.seg().y0() <= prevY) {
          if (db && T.update())
            T.msg(" contains last end point reached;\n"
                + "switching without choosing new seg from tree");
          segVert = ve.seg();
          continue;
        }

        segVert = ve.seg();
        Segment segBelowBracket;
        {
          Segment bracketSeg = segVert.constructBracket(false);
          tree.add(bracketSeg);
          segAboveBracket = bracketSeg.neighborAbove();
          segBelowBracket = bracketSeg.neighborBelow();
          tree.remove(bracketSeg);
        }
        if (db && T.update())
          T.msg(" added to tree to find sTree=" + segAboveBracket + " sPrev="
              + segBelowBracket + " prevSTree=" + segAboveBracket);

        // Make sure neighbors of vertical segment are both in the heat list.

        // In case two vertical segments are consecutive, don't add
        // sentinel segments twice.

        if (segAboveBracket == null || prevSTree != segAboveBracket) {
          if (segBelowBracket != null)
            heatList.add(segBelowBracket);

          if (segAboveBracket != null)
            heatList.add(segAboveBracket);
        }
        prevSTree = segAboveBracket;
        continue;
      }

      // if tree segment undefined, clear vert seg so we 
      // pop all remaining endpoints
      if (segAboveBracket == null) {
        segVert = null;
        continue;
      }

      BlackBox b = blackBox.construct(segVert, segAboveBracket);
      if (db && T.update())
        T.msg("intersecting between " + segVert + " and " + segAboveBracket
            + " is\n " + b);

      if (!b.abWithinSegments()) {
        if (db && T.update())
          T.msg(" not within segments, skipping");
        // make sure we've included this non-hot neighbor
        //        if (!HeatedSegmentList.OLDMETHOD)
        heatList.add(segAboveBracket);
        segVert = null;
        continue;
      }

      {
        IPoint2 hp = b.getIntersectionPixel(false);
        segAboveBracket.addToHeatRange(blackBox, sweepLine.stripX(), hp);
        Tools.ASSERT(prevY <= hp.y);
        prevY = hp.y;
        if (db && T.update())
          T.msg(" adding sTree=" + segAboveBracket + " to heatedSegmentList");

        heatList.add(segAboveBracket);
        segAboveBracket = segAboveBracket.neighborAbove();
      }
    }
    if (db && T.update())
      T.msg(" returning:\n" + heatList);
    return heatList;
  }

  /**
   * Performs hot pixel sorting in linear time.
   */
  private void doHotPixelProcess(int pc) {
    boolean db = C.vb(DB_HOTPIXELPROC);

    if (db && T.update())
      T.msg("doHotPixelProcess, pc=" + pc);

    sortedHPix = new HotPixelList();

    // merge every HeatList that involves the current sweep pixel column
    for (Iterator it = heatSet.getSetsFor(blackBox, pc); it.hasNext();) {
      if (db && T.update())
        T.msg("processing next heatList pair");
      HeatListEntry ent = (HeatListEntry) it.next();
      HeatedSegmentList hl = ent.left();
      HeatedSegmentList hr = ent.right();
      if (db && T.update())
        T.msg(" hl=" + hl + "\n hr=" + hr);

      HeatedSegmentList h0 = ensureHeatRangeMonotonicity(pc, hl, true, true);
      HeatedSegmentList h1 = ensureHeatRangeMonotonicity(pc, hl, true, false);
      HeatedSegmentList h2 = ensureHeatRangeMonotonicity(pc, hr, false, true);
      HeatedSegmentList h3 = ensureHeatRangeMonotonicity(pc, hr, false, false);

      mergeSortSegs(pc, h0, true, true);
      mergeSortSegs(pc, h1, true, false);
      mergeSortSegs(pc, h2, false, true);
      mergeSortSegs(pc, h3, false, false);
    }

    tree.addHotPixels(sortedHPix.size());

    for (int i = 0; i < sortedHPix.size(); i++)
      Stats.event(Stats.HOTPIXEL);

    if (db && T.update())
      T.msg("sortedHPix= " + sortedHPix);
  }

  /**
   * Merge hot pixels from a HeatedSegmentList into existing hot pixel list.
   * 
   * @param pc : pixel column
   * @param segs : HeatedSegmentList to read
   * @param entering : true if we're interested in segments entering the
   *   sweep line, false if we're emerging
   * @param positive : true to select pos slopes, false for neg slopes
   */
  private void mergeSortSegs(int pc, HeatedSegmentList segs, boolean entering,
      boolean positive) {

    boolean db = false && C.vb(DB_HOTPIXELPROC) && pc == 1;

    if (db && T.update())
      T.msg("mergeSortSegs enter=" + entering + " positive=" + positive + "\n"
          + segs);

    HotPixelList newHotPixels = new HotPixelList();
    int segIndex = 0;
    int hpixIndex = 0;
    DArray segList = new DArray();

    HeatedSegmentListIterator it = segs.iterator();

    // Add only those segments that are hot, not their neighbors. 
    // We will use the neighbors later, during the snap process.
    while (it.hasNext()) {
      Segment s = it.next();
      if (db && T.update())
        T.msg(" examining candidate seg " + s);
      if (!(positive ^ s.hasNegativeSlope())) {
        if (db && T.update())
          T.msg("  incorrect slope");
        continue;
      }

      if (!s.isHeatedInPixelColWithinStrip(blackBox, segs.stripX(), pc)) {
        if (db && T.update())
          T.msg(" not heated in pix col " + pc + " within strip "
              + segs.stripX());
        continue;
      }

      segList.add(s);
      if (db && T.update())
        T.msg(" added this seg");
    }

    while (true) {
      Segment segment = null;
      IPoint2 hpix = null;

      if (segIndex < segList.size())
        segment = (Segment) segList.get(segIndex);
      //      if (hpixIndex < sortedHPix.size())
      hpix = /*(IPoint2)*/sortedHPix.get(hpixIndex);

      if (segment == null && hpix == null)
        break;

      int hay = Integer.MIN_VALUE;
      Range r = null;

      if (segment != null) {
        r = segment.getHeatPixelRange(pc, blackBox, segs.stripX());
        hay = entering ^ positive ? r.y1() : r.y0();

        if (db && T.update())
          T.msg(" sa=" + segment + " hpix range="
              + segment.getHeatPixelRange(pc, blackBox, segs.stripX()) //
              + " ha=" + hay);

        IPoint2 lastAdded = newHotPixels.last();
        if (lastAdded != null) {
          if (lastAdded.y > hay)
            T.msg("lastAdded=" + lastAdded + ", hay=" + hay);

          Tools.ASSERT(lastAdded.y <= hay);
          if (lastAdded.y == hay) {
            segIndex++;
            continue;
          }
        }
      }

      if (hpix == null || (hay != Integer.MIN_VALUE && hay < hpix.y)) {
        newHotPixels.add(new IPoint2(pc, hay));
        segIndex++;
        continue;
      }
      newHotPixels.add(hpix);
      hpixIndex++;
    }
    sortedHPix.replaceWith(newHotPixels);

    //     {
    //      Tools.warn("deleting particular hot pixel");
    //      for (int i = 0; ; i++) {
    //        IPoint2 pt = sortedHPix.get(i);
    //        if (pt == null) break;
    //        if (pt.x == 5 && pt.y ==9) {
    //          sortedHPix.remove(i);
    //          i--;
    //        }
    //      }
    //    }

    if (db && T.update())
      T.msg("sortedHPix now " + Tools.d(sortedHPix));
  }

  private void doSnapProcess(int pc) {
    boolean db = C.vb(DB_SNAPPROC);

    snappedSegs = new DArray();

    if (db && T.update())
      T.msg("SnapProcess pc=" + pc + " sweep=" + sweepLine.stripX());

    for (Iterator iterator = heatSet.getSetsFor(blackBox, pc); iterator
        .hasNext();) {
      HeatListEntry ent = (HeatListEntry) iterator.next();
      if (db && T.update())
        T.msg(" " + ent);
      for (int side = 0; side < 2; side++) {
        HeatedSegmentList hl = ent.getList(side != 0);
        doSnapPass(pc, hl, true, side == 1);
        doSnapPass(pc, hl, false, side == 0);
      }
    }
    integrateNewHotPixels();
  }

  private void integrateNewHotPixels() {
    boolean db = false;

    if (db && T.update())
      T.msg("integrateNewHotPixels " + snappedSegs.toString(true));
    for (Iterator it = snappedSegs.iterator(); it.hasNext();) {
      Segment s = (Segment) it.next();

      int nNewPts = s.nSnapPoints() - s.startOfNewPts();

      if (db && T.update())
        T.msg(" ==========\nsegment:" + s.str());

      // move new points to an array
      DArray newPts = s.popSnapPoints(nNewPts);
      // find insertion point of start of new points within old list
      IPoint2 firstNew = (IPoint2) newPts.get(0);
      if (db && T.update())
        T.msg(" new snap points start at " + s.startOfNewPts() + ", =\n"
            + newPts.toString(true));
      int insert = s.nSnapPoints();
      while (true) {
        if (insert == 0)
          break;
        IPoint2 pt = s.getSnapPoint(insert - 1);
        int r = blackBox.compareSnapPoints(s, firstNew, pt);
        if (db && T.update())
          T.msg("  compare firstNew=" + firstNew + " with pt=" + pt + " is "
              + r);
        if (r >= 0)
          break;
        insert--;
        if (db && T.update())
          T.msg("  decr'd insert pos");
      }

      DArray oldPts = s.popSnapPoints(s.nSnapPoints() - insert);

      int iNew = 0, iOld = 0;
      while (true) {
        IPoint2 new0 = null, old0 = null;
        if (iNew < newPts.size()) {
          new0 = (IPoint2) newPts.get(iNew);
        }
        if (iOld < oldPts.size())
          old0 = (IPoint2) oldPts.get(iOld);

        if (new0 == null && old0 == null)
          break;

        if (old0 == null) {
          s.addSnapPoint(new0);
          iNew++;
          continue;
        }

        if (new0 == null) {
          s.addSnapPoint(old0);
          iOld++;
          continue;
        }

        if (blackBox.compareSnapPoints(s, old0, new0) <= 0) {
          s.addSnapPoint(old0);
          iOld++;
        } else {
          s.addSnapPoint(new0);
          iNew++;
        }
      }
    }
  }

  /**
   * Perform snapping for segments in a HeatedSegmentList.
   * @param pc : pixel column
   * @param HeatedSegmentList : list of segments to process
   * @param positive : true to process segments with positive (or zero) slopes,
   *   false for negative
   * @param reverseOrder : if true, processes hot pixels from highest (last)
   *  to lowest (first) instead of the other direction
   */
  private void doSnapPass(int pc, HeatedSegmentList hl, boolean positive,
      boolean reverseOrder) {
    boolean db = C.vb(DB_SNAPPROC);
    HeatedSegmentListIterator segmentsIterator = hl.iterator(reverseOrder);

    if (db && T.update())
      T.msg("doSnapPass pc=" + pc + "\n positive slopes=" + positive
          + " reverse order=" + reverseOrder);

    int hi = reverseOrder ? sortedHPix.size() - 1 : 0;
    int hiInc = reverseOrder ? -1 : 1;

    outer: while (segmentsIterator.hasNext()) {
      Segment s = segmentsIterator.next();
      if (db && T.update())
        T.msg(" seg " + s);

      if (!(positive ^ s.hasNegativeSlope())) {
        if (db && T.update())
          T.msg("slope doesn't agree");
        continue;
      }

      // If this is a bracket, replace with original vertical segment
      Segment s2 = s.bracketToVertical();
      if (s2 != s) {
        if (db && T.update())
          T.msg("bracket for " + s2);
        // If this is the upper bracket of a pair, skip, since we
        // don't want to process the original segment twice
        if (s2.y1() > s2.y0() && s.y0() == s2.y1()) {
          if (db && T.update())
            T.msg(" upper bracket of pair, s2.y0=" + s2.y0() + " y1=" + s2.y1()
                + " s.y0=" + s.y0() + " s.y1()=" + s.y1());
          continue;
        }
        s = s2;
      }

      // if segment has already been heated, then skip
      if (s.snapped(pc)) {
        if (db && T.update())
          T.msg("already snapped");
        continue;
      }

      Range sRange = blackBox.getClipRangeWithinPixelColumn(s, pc);
      if (db && T.update())
        T.msg("range=" + sRange);

      IPoint2 hpixA = null;
      IPoint2 hpixB = null;

      while (true) {
        hpixA = sortedHPix.get(hi);
        if (db && T.update())
          T.msg("hot pixel #" + hi + " of " + sortedHPix.size() + "= " + hpixA);
        if (hpixA == null) {
          if (db && T.update())
            T.msg("  ran out of hot pixels");
          break outer;
        }

        // if we are processing hex grid, monotonicity doesn't strictly hold;
        // we may need to examine pairs of consecutive hot pixels.
        hpixB = sortedHPix.get(hi + hiInc);
        if (hpixB == null)
          hpixB = hpixA;

        if (!reverseOrder) {
          if (sRange.y0() <= Math.max(hpixA.y, hpixB.y))
            break;
        } else {
          if (sRange.y1() >= Math.min(hpixA.y, hpixB.y))
            break;
        }
        hi += hiInc;
      }

      int hpInd = 0;
      if (sRange.contains(hpixA.y))
        hpInd = hi;
      else if (sRange.contains(hpixB.y))
        hpInd = hi + hiInc;
      else
        continue;

      if (db && T.update())
        T.msg(" snapping to hot pixels near " + hpInd + ":"
            + sortedHPix.get(hpInd));

      snapSegmentToHotPixelsNear(s, hpInd);
    }
  }

  /**
   * Snap a segment to every hot pixel that it intersects.
   * Snaps hot pixels in an order that is appropriate, with respect to the
   * segment's slope.
   * 
   * @param seg : Segment
   * @param hpInd : index of hot pixel within hpList; segment must intersect
   *   this pixel (or one of its immediate neighbors, if hex grid)
   */
  private void snapSegmentToHotPixelsNear(Segment seg, int hpInd) {
    boolean db = C.vb(DB_SNAPPROC);

    IPoint2 hp = sortedHPix.get(hpInd);
    seg.setSnappedFlag(hp.x);
    Range segRange = blackBox.getClipRangeWithinPixelColumn(seg, hp.x);

    // Find lowest (highest, if neg slope) hot pixel this segment intersects
    while (true) {
      int iNext = !seg.hasNegativeSlope() ? hpInd - 1 : hpInd + 1;

      IPoint2 hprev = sortedHPix.get(iNext);
      if (db && T.update())
        T.msg(" finding lowest/highest hot pixel, hprev=" + hprev);
      if (hprev == null)
        break;
      if (!segRange.contains(hprev.y)) {
        if (db && T.update())
          T.msg(" range " + segRange + " doesn't contain hprev");
        break;
      }
      hp = hprev;
      hpInd = iNext;
    }

    int start = hpInd;

    int snapCount = 0;
    int end = -1;

    while (hp != null && segRange.contains(hp.y)) {
      if (db && T.update())
        T.msg("snapping " + seg + " to " + hp);
      if (snapCount == 0)
        seg.startNewPts();

      snapCount++;
      end = hpInd;
      seg.addToHeatRange(blackBox, sweepLine.stripX(), hp);
      seg.addSnapPoint(hp);
      hpInd = !seg.hasNegativeSlope() ? hpInd + 1 : hpInd - 1;
      hp = sortedHPix.get(hpInd);
    }

    snappedSegs.add(seg);
    for (int nbrPass = 0; nbrPass < 2; nbrPass++) {
      Segment neighborSeg = seg.neighbor(nbrPass == 1);
      if (neighborSeg == null)
        continue;
      int hi = (nbrPass == 0) ? Math.min(start, end) : Math.max(start, end);
      IPoint2 h = sortedHPix.get(hi);

      if (neighborSeg.snapped(h.x)) {
        if (db && T.update())
          T.msg(" already heated");
        continue;
      }
      if (db && T.update())
        T.msg(" recursively processing neighbor " + neighborSeg);

      Range r = blackBox.getClipRangeWithinPixelColumn(neighborSeg, h.x);
      if (!r.contains(h.y)) {
        if (db && T.update())
          T.msg(" segment doesn't intersect hot pixel");
        continue;
      }
      snapSegmentToHotPixelsNear(neighborSeg, hi);
    }
  }

  /** 
   * Ensure heat pixels are monotonic with respect to an ordered list of segments.
   * We iterate through the list, in linear time,
   * extending hot ranges as necessary to ensure
   * these conditions hold:
   *   enter+positive: lower heat pixels are monotonically increasing
   *   enter+negative: upper heat pixels are monotonically increasing
   *   leave+positive: upper heat pixels are monotonically increasing
   *   leave+negative: lower heat pixels are monotonically increasing
   * We ignore non-heated (and incorrectly-sloped) segments during 
   * this iteration.
   * 
   * @param pixelColumn pixel column containing heat pixels (in hex grids,
   *   some strips intersect two pixel columns)
   * @param hs HeatedSegentList
   * @param entering true if this list represents order of segments 
   *   when entering sweep strip
   * @param positiveSlopes true to process positive sloped segments only vs negative
   */
  private HeatedSegmentList ensureHeatRangeMonotonicity(int pixelColumn,
      HeatedSegmentList hs, boolean entering, boolean positiveSlopes) {

    boolean db = false && C.vb(DB_HOTPIXELPROC);

    if (db && T.update())
      T.msg("ensureHeatRangeMonotonicity, pc=" + pixelColumn + " pos="
          + positiveSlopes + " ent=" + entering + "\n " + hs);

    if (!C.vb(OLDHEX))
      hs = correctHexOrder(pixelColumn, hs, entering, positiveSlopes);

    boolean testMin = !(entering ^ positiveSlopes);
    HeatedSegmentListIterator it = hs.iterator(testMin);
    int prevExtremeY = testMin ? Integer.MAX_VALUE : Integer.MIN_VALUE;
    while (it.hasNext()) {
      Segment s = it.next();
      if (db && T.update())
        T.msg(" next segment is " + s);
      if (!s.hasNegativeSlope() != positiveSlopes) {
        if (db && T.update())
          T.msg(" incorrect slope");
        continue;
      }

      if (!s.isHeatedInPixelColWithinStrip(blackBox, hs.stripX(), pixelColumn)) {
        if (db && T.update())
          T.msg(" not heated in pixel column");
        continue;
      }

      Range range = s.getHeatPixelRange(pixelColumn, blackBox, hs.stripX());

      int thisExtremeY = range.y(!testMin);

      if (db && T.update())
        T.msg(" hot pixel range in pc=" + pixelColumn + ", strip "
            + hs.stripX() + " is " + range + "\n thisExtreme=" + thisExtremeY
            + ", prevExtreme=" + prevExtremeY);
      if ((testMin && thisExtremeY <= prevExtremeY) //
          || (!testMin && thisExtremeY >= prevExtremeY)) {
        if (db && T.update())
          T.msg("  replacing prevExtreme with this");
        prevExtremeY = thisExtremeY;
      } else {
        if (db && T.update())
          T.msg(" extending heat range of " + s //
              + " to include " + prevExtremeY);
        s.addToHeatRange(blackBox, hs.stripX(), new IPoint2(pixelColumn,
            prevExtremeY));
      }
    }
    return hs;
  }

  /**
   * Rearrange the ordering of hot segments in a HeatedSegmentList
   * so order corresponds to their order with respect to the boundary
   * of a hex pixel column, as opposed to their order with respect to
   * a strip boundary.
   * 
   * Ignores cold (neighbor) segments in the list, and incorrectly sloped segs.
   * 
   * @param pixelColumn
   * @param hs : HeatedSegmentList to examine
   * @param entering : true if entering strip
   * @param positiveSlopes : true to examine only positive slopes
   * @return HeatedSegmentList with correct order, containing only heated
   *  segments with the correct slope
   */
  private HeatedSegmentList correctHexOrder(int pixelColumn,
      HeatedSegmentList hs, boolean entering, boolean positiveSlopes) {
    boolean db = true && C.vb(DB_HOTPIXELPROC);

    HeatedSegmentList ret = hs;
    do {

      // make sure correction process is necessary
      if (blackBox != BlackBoxHexStrip.S)
        break;

      // only necessary if entering first strip, or exiting last strip, in
      // pixel column
      if (hs.stripX() == blackBox.firstStripInPixel(pixelColumn)) {
        if (!entering)
          break;
      } else if (hs.stripX() == blackBox.lastStripInPixel(pixelColumn)) {
        if (entering)
          break;
      } else
        break;

      boolean parity = (positiveSlopes ^ entering);

      if (db && T.update())
        T.msg("correctHexOrder pc=" + pixelColumn + " strip=" + hs.stripX()
            + " enter=" + entering + " pos=" + positiveSlopes + "\n" + hs
            + "\n" + "first=" + blackBox.firstStripInPixel(pixelColumn)
            + " last=" + blackBox.lastStripInPixel(pixelColumn));

      DArray rList = new DArray();

      for (HeatedSegmentListIterator it = hs.iterator(parity);;) {

        if (!it.hasNext())
          break;
        if (it.peek().hasNegativeSlope() != !positiveSlopes) {
          it.next();
          continue;
        }

        int yPrime = blackBox.getClipRangeWithinPixelColumn(it.peek(),
            pixelColumn).y(parity);
        int y = yPrime + (parity ? 2 : -2);

        DArray sList = new DArray();
        DArray sListPrime = new DArray();

        if (db && T.update())
          T.msg("seg " + it.peek() + " y'=" + yPrime + " y=" + y);
        while (true) {
          Segment seg = it.peek();
          if (seg == null)
            break;
          if (seg.hasNegativeSlope() != !positiveSlopes) {
            it.next();
            continue;
          }

          Range r = blackBox.getClipRangeWithinPixelColumn(seg, pixelColumn);
          if (db && T.update())
            T.msg("next seg=" + seg + " range=" + r);

          if (r.contains(y))
            sList.add(it.next());
          else if (r.contains(yPrime))
            sListPrime.add(it.next());
          else
            break;
        }
        if (db && T.update())
          T.msg("S =" + sList + "\nS'=" + sListPrime);
        rList.addAll(sList);
        rList.addAll(sListPrime);
      }

      ret = new HeatedSegmentList(hs.stripX(), "corrected:" + hs.debugName(),
          false); // HeatedSegmentList.OLDMETHOD);

      // add segments to output list in appropriate order
      for (Iterator it = rList.iterator(parity); it.hasNext();)
        ret.add((Segment) it.next(), false);

      if (db && T.update())
        T.msg("corrected\n" + hs + " =>\n" + ret);
    } while (false);
    return ret;
  }

  private BlackBox blackBox;

  private Segment[] segs;

  // B+ tree containing sorted active segments, plus the heap of intersection
  // events
  private SegTree tree;

  // Queues for segment startpoint, endpoint events
  private SegPriorityQueue startPts, stopPts;

  // Current sweep line
  private SweepStrip sweepLine;

  private HeatListSet heatSet;

  // List of vertical/point segments within current strip
  private DArray verticalSegments;

  // sorted list of hot pixels
  private HotPixelList sortedHPix;

  // list of segments that underwent any snapping during snap process
  private DArray snappedSegs;

  // List of problems detected in snapList
  private FPoint2[] snapListProblems;
}
