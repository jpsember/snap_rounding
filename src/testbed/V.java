package testbed;

import base.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

/**
 * Main view of TestBed applications.
 * It has the short name 'V' to minimize typing.
 */
public class V implements Globals {

  public static FRect viewRect;

  /**
   * Determine if a mouse event involves the second button (button1)
   * 
   * @param e
   *          MouseEvent
   * @return boolean
   */
  private static boolean button1(MouseEvent e) {
    return ((e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK);
  }

  /**
   * Get the JPanel containing the view
   * @return JPanel
   */
  static JPanel getPanel() {
    return panel;
  }

    static Color getBackgroundColor() {
    return new Color(C.vi(TBGlobals.sFILLCOLOR));
  }

  private static class ourPanel extends JPanel {

    /**
     * Paint the view
     * 
     * @param g
     *          Graphics
     */
    public void paintComponent(Graphics g) {

      final boolean db = false;
      if (db)
        Streams.out.println("vp.paintComponent");

      updateScaleFactor();

      recalcLogicalView(g);

      FPoint2 logSize = logicalSize();
      int screenWidth = getWidth();
      int screenHeight = getHeight();
      int screenOffsetX = 0;
      int screenOffsetY = 0;

      if (C.vb(TBGlobals.ENFORCE_ASP)) {
        double aspRatio = C.vd(TBGlobals.ASPECTRATIO);
        double logWidth = 100.0 * aspRatio;
        double logHeight = 100.0;

        if (aspRatio > screenWidth / (double) screenHeight) {
          int q = (int) (screenWidth / aspRatio);
          screenOffsetY = (screenHeight - q) / 2;

          screenHeight = q;
        } else {
          int q = (int) (screenHeight * aspRatio);
          screenOffsetX = (screenWidth - q) / 2;
          screenWidth = q;
        }
        logSize = new FPoint2(logWidth, logHeight);
      }
      FPoint2 depscr0 = new FPoint2(screenOffsetX, screenOffsetY);
      FPoint2 depscr1 = new FPoint2(screenWidth + screenOffsetX, screenHeight
          + screenOffsetY);

      if (TestBed.parms.includeGrid)
        grid.setSize(C.vi(TBGlobals.GRIDSIZE), logSize);

      TBFont.prepare();

      Graphics2D g2 = null;
      boolean generatingEPS = epsMode;
      boolean generatingIPE = ipeMode;

      boolean aliasing = true;

      if (generatingEPS) {
        EpsGraphics2D g3 = new EpsGraphics2D();
        g2 = g3;
        g3.setColorDepth(EpsGraphics2D.RGB);
        g3.setAccurateTextMode(true);

        g3.setMaxBounds(new FRect(clipScreen0.x, clipScreen0.y, clipScreen1.x
            - clipScreen0.x + 1, clipScreen1.y - clipScreen0.y + 1));

      } else if (generatingIPE) {
        IPEGraphics g3 = new IPEGraphics(new FRect(0, 0, clipScreen1.x
            - clipScreen0.x + 1, clipScreen1.y - clipScreen0.y + 1));
        g2 = g3;
      } else {
        g2 = (Graphics2D) g;

      }

      g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
          aliasing ? RenderingHints.VALUE_ANTIALIAS_ON
              : RenderingHints.VALUE_ANTIALIAS_OFF));

      // save current drawing context
      vu_setGraphics(g2);

      V.setFont(FNT_MEDIUM);

      // determine
      // (1) translation vector
      // so 0,0 is at center of logical view
      // (2) scale factor (also y is flipped)
      // (3) translation vector
      // so 0,0 is at center of physical view
      //

      // center logical view within physical with fixed # pixels
      // padding around border

      // compensate for aspect ratio, to extend
      // in the dimension that isn't critical

      double sfx = Math.min((depscr1.x - depscr0.x) / logSize.x,
          (depscr1.y - depscr0.y) / logSize.y);

      double sfy = -sfx;

      logPixelSize = sfx;

      if (!generatingIPE) {
        // negate the y scale factor to change the orientation of the y axis (we
        // want it to point up)
        logicToViewTF.setToIdentity();
        // translation so 0,0 is at center of physical
        logicToViewTF.translate((depscr0.x + depscr1.x) * .5,
            (depscr0.y + depscr1.y) * .5);
        logicToViewTF.scale(sfx, sfy);
        // translation so 0,0 is at center of logical coords
        logicToViewTF.translate(-logSize.x * .5, -logSize.y * .5);
      } else {
        // negate the y scale factor to change the orientation of the y axis (we
        // want it to point up)
        logicToViewTF.setToIdentity();
        logicToViewTF.scale(sfx, sfy);
        logicToViewTF.translate(0, -logSize.y);
      }

      // calculate inverse
      AffineTransform inv = null;
      try {
        inv = logicToViewTF.createInverse();
      } catch (NoninvertibleTransformException e) {
        throw new FPError(e.toString());
      }

      viewToLogicTF.setTransform(inv);

      Color bgColor =  getBackgroundColor(); //new Color(C.vi(TBGlobals.sFILLCOLOR));

      if (C.vb(TBGlobals.ENFORCE_ASP) && !(epsMode || ipeMode)) {
        pushColor(Color.gray);
        fillRect(new FRect(g2.getClipBounds()));
        popColor();
      }

      // set transform so 0,0 is top left of plottable region of view, 
      // but with no scaling
      AffineTransform origTrans = g2.getTransform();
      g2.translate(depscr0.x, depscr0.y);
      g2.setTransform(origTrans);
      g2.transform(logicToViewTF);

      {
        // calculate desired clipping bounds, in view space.
        // this is what the PDF, EPS, IPE graphics will be clipped to.
        FPoint2 c0 = new FPoint2(), c1 = new FPoint2();
        logicToViewTF.transform(new FPoint2(0, 0), c0);

        logicToViewTF.transform(logSize, c1);
        clipScreen0 = new FPoint2(c0.x, c1.y);
        clipScreen1 = new FPoint2(c1.x, c0.y);
      }

      {
        boolean fill = !bgColor.equals(Color.white);
        if (fill || !(epsMode || ipeMode)) {
          pushColor(bgColor);
          fillRect(0, 0, logSize.x, logSize.y);
          popColor();

        }
      }
      {

        // construct strokes so we have uniform thickness despite scaling
        buildStroke(STRK_NORMAL, 1.0);
        buildStroke(STRK_THICK, 2.0);
        buildStroke(STRK_THIN, .4);
        buildStroke(STRK_VERYTHICK, 3.0);
        {
          float[] dash = new float[2];
          double sc = calcStrokeWidth(8);
          dash[0] = (float) (sc);
          dash[1] = (float) (sc * .5);

          BasicStroke s = new BasicStroke(calcStrokeWidth(.4),
              BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash, 0);
          strokes[STRK_RUBBERBAND] = s;
        }
        scale = screenScaleFactor() / logicalPixelSize();

        g2.setStroke(strokes[STRK_NORMAL]);
        g2.setPaint(Color.BLACK);
        V.setFont(FNT_MEDIUM);

        if (TestBed.parms.includeGrid) {
          if (C.vb(TBGlobals.GRIDON))
            grid.render(C.vb(TBGlobals.GRIDLABELS));
        }

        Editor.preparePaint();
        TestBed.app.paintView();
      }

      epsMode = false;
      ipeMode = false;

      // ----------------------------------------------
      // restore drawing context
      if (db)
        Streams.out.println(" vp, restoring original transform");

      g2.setTransform(origTrans);
      g2.translate(depscr0.x, depscr0.y);

      vu_setGraphics(null);
      // ----------------------------------------------
      if (generatingEPS) {
        // save the eps file in a string to be processed
        // by the event thread later.
        epsFile = g2.toString();
      }
      if (generatingIPE) {
        ipeFile = g2.toString();
      }
      flushEPSFile();
    }
    public ourPanel() {

      setDoubleBuffered(true);
      setOpaque(true);

      Border loweredbevel = BorderFactory.createLoweredBevelBorder();
      setBorder(loweredbevel);

      setBackground(Color.white);
      setForeground(Color.black);

      addMouseListener(new MouseListener() {
        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
          TestBed.procAction(new TBAction(!button1(e) ? TBAction.DOWN2
              : TBAction.DOWN1, e));
        }

        public void mouseReleased(MouseEvent e) {
          TestBed.procAction(new TBAction(!button1(e) ? TBAction.UP2
              : TBAction.UP1, e));
        }

      });
      addMouseMotionListener(new MouseMotionListener() {
        public void mouseMoved(MouseEvent e) {
          TestBed.procAction(new TBAction(TBAction.HOVER, e));
        }

        public void mouseDragged(MouseEvent e) {
          TestBed.procAction(new TBAction(TBAction.DRAG, e));
        }
      });
    }
  }
  private static ourPanel panel;

  static void init() {
    setLogicalView(100, 100);
    panel = new ourPanel();
  }

  /**
   * Set logical view size
   * @param width 
   * @param height dimensions of view 
   */
  private static void setLogicalView(double width, double height) {
    logicalSize = new FPoint2(width, height);
    viewRect = new FRect(0, 0, width, height);
  }

  /**
   * Draw a string (with flags set to zero)
   * @param str 
   * @param loc view coordinates 
   */
  public static void draw(String str, FPoint2 loc) {
    draw(str, loc.x, loc.y, 0);
  }

  /**
   * Mark a location with a small 'x'
   * @param pt location
   */
  public static void mark(FPoint2 pt) {
    mark(pt, MARK_X);
  }

  /**
   * Mark a location 
   * @param pt location
   * @param markType @see {@link #mark(FPoint2, int, double)}
   */
  public static void mark(FPoint2 pt, int markType) {
    mark(pt, markType, 1.0);
  }

  /**
   * Mark a location 
   * @param pt location
   * @param markType MARK_xxx
   */
  public static void mark(FPoint2 pt, int markType, double scale) {
    double pad = getScale() * scale * .4;
    switch (markType) {
    case MARK_X:
      drawLine(pt.x - pad, pt.y - pad, pt.x + pad, pt.y + pad);
      drawLine(pt.x - pad, pt.y + pad, pt.x + pad, pt.y - pad);
      break;
    default:
    case MARK_DISC:
      fillCircle(pt, pad * 1.5);
      break;
    case MARK_CIRCLE:
      drawCircle(pt, pad * 1.5);
      break;
    case MARK_SQUARE:
      drawRect(pt.x - pad, pt.y - pad, pad * 2, pad * 2);
      break;
    case MARK_FSQUARE:
      fillRect(pt.x - pad, pt.y - pad, pad * 2, pad * 2);
      break;
    case MARK_NONE:
      break;
    }
  }

  /**
   * Transform a viewspace point to a logicspace point
   * 
   * @param view
   *          FPoint2
   * @param logic
   *          FPoint2
   */
  static void viewToLogic(FPoint2 view, FPoint2 logic) {
    viewToLogicTF.transform(view, logic);
  }

  /**
   * Draw a string 
   * @param str string to draw
   * @param loc view coordinates
   * @param flags @see {@link #draw(String, double, double, int)}
   */
  public static void draw(String str, FPoint2 loc, int flags) {
    draw(str, loc.x, loc.y, flags);
  }

  /**
   * Get size in viewspace of a 1x1 logical pixel
   * 
   * @return width in viewspace
   */
  private static double logicalPixelSize() {
    return logPixelSize;
  }

  private V() {
  }

  private static float calcStrokeWidth(double width) {
    double d = width * screenScaleFactor * 1.8;
    if (!epsMode && !ipeMode) {
      d /= logPixelSize;
    }
    return (float) d;
  }

  private static void buildStroke(int index, double width) {
    float f = calcStrokeWidth(width);
    strokes[index] = new BasicStroke(f);
  }

  static void plotToEPS(boolean cvtToPDF0) {
    cvtToPDF = cvtToPDF0;
    epsMode = true;
  }

  static void plotToIPE() {
    ipeMode = true;
  }

  private static boolean epsMode;

  private static boolean cvtToPDF;

  private static boolean ipeMode;

  /**
   * Get the constructed EPS file in a string, and clear it.
   * 
   * @return EPS file, or null if none exists
   */
  private static String getEPSFile() {
    String s = epsFile;
    epsFile = null;
    return s;
  }

  /**
   * Get the constructed IPE file in a string, and clear it.
   * 
   * @return IPE file, or null if none exists
   */
  private static String getIPEFile() {
    String s = ipeFile;
    ipeFile = null;
    return s;
  }

  private static String epsFile;

  private static String ipeFile;

  /**
   * Set stroke 
   * @param s  stroke (STRK_xxx)
   */
  public static void setStroke(int s) {
    g.setStroke(strokes[s]);
  }

  /**
   * Modify internal variables to reflect current 'GLOBALSCALE' gadget value.
   * Sets screenScaleFactor to be 240 * GLOBALSCALE / screen width.
   */
  private static void updateScaleFactor() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenScaleFactor = (240.0 * C.vi(TBGlobals.GLOBALSCALE))
        / screenSize.width;
  }

  private static void recalcLogicalView(Graphics g) {
    final boolean db = false;

    double xs, ys;

    Rectangle r = g.getClipBounds();
    double n = Math.min(r.width, r.height);
    xs = r.width / n;
    ys = r.height / n;
    if (db)
      Streams.out.println("setLogicalView xs=" + Tools.f(xs) + " ys="
          + Tools.f(ys));

    setLogicalView(xs * 100, ys * 100);
  }
  /**
   * Save current scaling factor on stack, scale by some factor
   * @param scaleAdj amount to scale current factor by
   */
  public static void pushScale(double scaleAdj) {
    plotStack.push(new Double(screenScaleFactor));
    screenScaleFactor *= scaleAdj;
    scale = screenScaleFactor / logicalPixelSize();
    plotStack.push(ST_SCALE);
  }

  /**
   * Pop scale factor from stack
   */
  public static void popScale() {

    Double val = (Double) popValue(ST_SCALE);
    if (val != null) {
      screenScaleFactor = val.doubleValue();
      scale = screenScaleFactor / logicalPixelSize();
    }
  }

  private static double screenScaleFactor() {
    return screenScaleFactor;
  }

  private static double scale;

  private static DArray plotStack = new DArray();

  private static int activeFont;

  /**
   * Set font
   * @param font FNT_xx
   */
  public static void setFont(int font) {
    activeFont = font;
    g.setFont(TBFont.getFont(activeFont));
  }

  /**
   * Save current font on stack, set to new
   * @param font new font (FNT_xx)
   */
  public static void pushFont(int font) {
    if (font >= 0) {
      plotStack.pushInt(activeFont);
      setFont(font);
    } else {
      plotStack.push(ST_IGNORE);
    }
    plotStack.push(ST_FONT);
  }

  /**
   * Pop font from stack
   */
  public static void popFont() {
    Integer val = (Integer) popValue(ST_FONT);
    if (val != null)
      setFont(val.intValue());
  }

  /**
   * Draw a string (with flags set to zero)
   * @param str 
   * @param x 
   * @param y view coordinates 
   */
  public static void draw(String str, double x, double y) {
    draw(str, x, y, 0);
  }

  /**
   * Draw a string 
   * @param str string to draw
   * @param x 
   * @param y view coordinates
   * @param flags Flags controlling string's appearance.  
   *   These include:
   *  <pre>
      TX_LINEWIDTH  if not zero, plots string in multiple rows, breaking at 
                     word boundaries (if possible) so no row has length 
                     greater than this value
      TX_BGND       if set, clears background of string
      TX_FRAME      if set, draws a frame around the string
      TX_CLAMP      if set, clamps coordinates into range of view so entire
                     string is guaranteed to be visible
   *   </pre>
   */
  public static void draw(String str, double x, double y, int flags) {

    TBFont f = TBFont.get(activeFont);

    DArray strings = new DArray();

    // determine the number of rows

    int maxStrLen = 0;
    int lineWidth = (flags & TX_LINEWIDTH);
    final boolean db = false && (lineWidth > 0);

    boolean centered = true;

    if (lineWidth != 0) {
      centered = false;
      int s = 0;
      int lastSpace = -1;
      int c = 0;
      // int cnt = 0;
      while (true) {
        // Tools.ASSERT(++cnt < 20000,
        // "overflow splitting string: \n[" + str + "]");
        char ch = ' ';
        if (c < str.length()) {
          ch = str.charAt(c);
        }
        if (ch == ' ' || ch == '\n') {
          lastSpace = c;
        }

        if (db) {
          System.out.println(" ch=" + ch + ", c=" + c + ", s=" + s
              + ", lastSpace=" + lastSpace);
        }
        // If beyond maximum width, back up to last space printed

        if (ch == '\n' || c - s > lineWidth || c == str.length()) {
          if (lastSpace > s || ch == '\n') {
            String ns = str.substring(s, lastSpace);
            strings.add(ns);
            maxStrLen = Math.max(maxStrLen, ns.length());
            c = lastSpace + 1;
            s = c;
          } else {
            String ns = str.substring(s, c);
            strings.add(ns);
            maxStrLen = Math.max(maxStrLen, ns.length());
            s = c;
            c++;
          }
        } else {
          c++;
        }
        if (c > str.length()) {
          break;
        }
      }
    } else {
      strings.add(str);
      maxStrLen = str.length();
    }
    if (db) {
      System.out.println(" strings: " + strings);
    }

    // modify the transform so text is not upside-down

    AffineTransform saveXform = g.getTransform();

    double fsize = f.charWidth();
    //    if (TestBed.DEBUG) {
    //      Tools.ASSERT(fsize >= 0);
    //    }

    double ascent = f.metrics().getAscent();
    double descent = f.metrics().getDescent();

    double textW = maxStrLen * fsize;
    double rowH = (ascent + descent) * .8;
    double textH = rowH * (strings.size() + .2);
    /*
     * System.out.print("ascent="+ascent+" descent="+descent);
     * System.out.print(" width="+fontCharWidth); System.out.print("
     * height="+fontMetrics.getHeight()); System.out.print("
     * leading="+fontMetrics.getLeading()); System.out.println();
     */
    double scl = scale;

    double xs = scl * textW, ys = scl * textH;

    double x0 = x - xs * .5, y0 = y - ys * .5;

    // System.out.println(" before clamped="+x0+","+y0);
    // System.out.println("lx0="+lx0+" ly0="+ly0+" lx1="+lx1+" ly1="+ly1);
    if ((flags & TX_CLAMP) != 0) {
      x0 = MyMath.clamp(x0, 0, logicalSize.x - xs);
      y0 = MyMath.clamp(y0, 0, logicalSize.y - ys);
    }

    g.translate(x0, y0);
    g.scale(scl, -scl);

    if (flags != 0) {
      textRect.setFrame(0, -textH, textW, textH);
      if ((flags & TX_BGND) != 0) {
        pushColor(Color.white); //g.getBackground());
        g.fill(textRect);
        popColor();
      }
      if ((flags & TX_FRAME) != 0) {
        g.draw(textRect);
      }
    }
    double ry = (ascent - textH);
    for (int i = 0; i < strings.size(); i++) {
      String s = strings.getString(i);
      double px = 0;
      if (centered) {
        px = (textW + 1 - s.length() * fsize) * .5;
      }

      g.drawString(s, (float) px, (float) (ry) - 1);
      ry += rowH;
    }
    g.setTransform(saveXform);
  }
  private static final boolean ds = false;

  /**
   * Pop stroke from stack
   */
  public static void popStroke() {

    Stroke val = (Stroke) popValue(ST_STROKE);

    if (ds)
      Streams.out.println("popStroke, val=" + val);

    if (val != null) {
      if (ds)
        Streams.out.println(" setting stroke to " + val);

      g.setStroke(val);
    }
  }

  /**
   * Draw a circle
   * @param origin origin of circle
   * @param radius radius of circle
   */
  public static void drawCircle(FPoint2 origin, double radius) {
    g.draw(new Arc2D.Double(origin.x - radius, origin.y - radius, 2 * radius,
        2 * radius, 0, 360, Arc2D.CHORD));
  }

  /**
   * Draw a rectangle
   * @param r rectangle
   */
  public static void drawRect(FRect r) {
    g.draw(r);
  }

  /**
   * Set graphics context being updated by updateView() (should only be called
   * by the viewPanel class)
   * 
   * @param gr :
   *          graphics context
   */
  private static void vu_setGraphics(Graphics2D gr) {
    g = gr;
  }

  private static String epsExt = "eps";

  private static String ipeExt = "xml";

  private static String pdfExt = "pdf";

  /**
   * Display file requester, optionally save file
   * @param file
   * @param prompt
   * @param path
   * @param ext
   * @return path file was written to, or null if no writing occurred
   */
  private static String saveFile(String file, String prompt, String path,
      String ext) {

    String writtenTo = null;
    path = askForSaveFile(file, prompt, path, ext);
    if (path != null) {
      try {
        Writer w = Streams.writer(path);
        w.write(file);
        w.close();
        writtenTo = path;
      } catch (IOException e) {
        TestBed.showError(e.getMessage());
      }
    }
    return writtenTo;
  }

  /**
   * Display file requester, optionally save file
   * @param file
   * @param prompt
   * @param path
   * @param ext
   * @return path file was written to, or null if no writing occurred
   */
  private static String askForSaveFile(String file, String prompt, String path,
      String ext) {

    String writtenTo = null;

    if (path == null)
      path = "";

    IFileChooser ch = Streams.fileChooser();
    path = ch.doWrite(prompt, path, new PathFilter(ext));

    if (path != null) {
      path = Path.changeExtension(path, ext);
      writtenTo = path;
    }
    return writtenTo;
  }

  private static String epsFileToFlush;

  private static String ipeFileToFlush;

  //  private static int frameNum = 0;
  private static void flushEPSFile() {
    {
      // If EPS file is ready to be saved, do so.
      String s = getEPSFile();
      if (s != null) {
        epsFileToFlush = s;

        if (cvtToPDF)
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              //              String path = null;
              //              if (false) {
              //                Tools.warn("using anim frame");
              //                path = TestBed.getSpecialSavePath(null, pdfExt);
              //                path = Path.removeExt(path);
              //                path = path + (frameNum++);
              //                path = Path.addExtension(path, pdfExt);
              //
              //                path = askForSaveFile(epsFileToFlush, "Save .pdf file:", path,
              //                    pdfExt);
              //              }
              String path = askForSaveFile(epsFileToFlush, "Save .pdf file:",
                  TestBed.getSpecialSavePath(null, pdfExt), pdfExt);

              if (path != null) {
                // write to temp dir
                try {
                  File f = File.createTempFile("_testbed_", ".eps");
                  String epsPath = f.getAbsolutePath();
                  Writer w = Streams.writer(epsPath);
                  w.write(epsFileToFlush);
                  w.close();

                  /* Can't use ProcessBuilder with pre 5.0 compiler                 
                  
                  // using ProcessBuilder to spawn an process
                  ProcessBuilder pb = new ProcessBuilder("pstopdf", epsPath,
                      "-o", path);
                  // merge child's error and normal output streams.
                  // Note it is not called setRedirectErrorStream.
                  pb.redirectErrorStream(true);

                  //Process p = 
                  pb.start();
                  // From here on it, it behaves just like exec, since you have the
                  // exact same Process object.
                  // ...
                  */
                  String cmd = "pstopdf " + epsPath + " -o " + path;
                  Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                  TestBed.showError(e.getMessage());
                }
              }
              epsFileToFlush = null;
            }
          });
        else

          javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              saveFile(epsFileToFlush, "Save .eps file:", TestBed
                  .getSpecialSavePath(null, epsExt), epsExt);
              epsFileToFlush = null;
            }
          });
      }
    }
    {
      // If EPS file is ready to be saved, do so.
      String s = getIPEFile();
      if (s != null) {
        ipeFileToFlush = s;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            saveFile(ipeFileToFlush, "Save IPE file as .XML file:", TestBed
                .getSpecialSavePath(null, ipeExt), ipeExt);
            ipeFileToFlush = null;
          }
        });

      }
    }

  }
  /**
   * Draw a filled circle (disc)
   * @param origin origin of circle
   * @param radius radius of circle
   */
  public static void fillCircle(FPoint2 origin, double radius) {
    g.fill(new Arc2D.Double(origin.x - radius, origin.y - radius, 2 * radius,
        2 * radius, 0, 360, Arc2D.CHORD));
  }

  /**
   * Set color
   * @param c 
   */
  public static void setColor(Color c) {
    g.setColor(c);
  }

  /**
   * Pop a number of state attributes
   * @param count number to pop
   */
  public static void pop(int count) {
    for (int i = 0; i < count; i++)
      pop();
  }

  /**
   * Pop a state attribute
   */
  public static void pop() {
    if (plotStack.isEmpty())
      throw new IllegalStateException("render stack empty");
    Object tag = plotStack.peek(0);
    if (tag == ST_COLOR)
      popColor();
    else if (tag == ST_STROKE)
      popStroke();
    else if (tag == ST_SCALE)
      popScale();
    else if (tag == ST_FONT)
      popFont();
  }

  /**
   * Pop color from stack
   */
  public static void popColor() {
    Color nc = (Color) popValue(ST_COLOR);
    if (nc != null)
      g.setColor(nc);
  }

  private static Object popValue(Object expectedTag) {
    Object tag = plotStack.pop();
    if (tag != expectedTag) {
      throw new IllegalStateException("render stack problem: popped " + tag
          + ", expected " + expectedTag);
    }
    Object val = plotStack.pop();
    if (val == ST_IGNORE)
      val = null;
    return val;
  }

  /**
   * Get the current graphics context being updated by updateView(),
   * in case we need to manipulate it in ways not provided by this class.
   * 
   * @return Graphics2D
   */
  public static Graphics2D get2DGraphics() {
    return g;
  }

  /**
   * Save current color on stack, set to new
   * @param c new color
   */
  public static void pushColor(Color c) {
    pushColor(c, null);
  }
  /**
   * Save current color on stack, set to new
   * @param c new color
   */
  public static void pushColor(Color c, Color defaultColor) {
    if (c == null)
      c = defaultColor;

    if (c != null) {
      plotStack.push(g.getColor());
      setColor(c);
    } else {
      plotStack.push(ST_IGNORE);
    }
    plotStack.push(ST_COLOR);
  }

  // graphics being updated by updateView()
  private static Graphics2D g;

  /**
   * Draw a line segment
   * @param p0 first endpoint
   * @param p1 second endpoint
   */
  public static void drawLine(FPoint2 p0, FPoint2 p1) {
    if (false) {
      long start = System.currentTimeMillis();
      drawLine(p0.x, p0.y, p1.x, p1.y);
      long end = System.currentTimeMillis();
      if (end - start > 1) {
        Streams.out.println("drawling line " + p0 + ".." + p1 + " took "
            + (end - start) + " ms!");
      }
      return;
    }
    drawLine(p0.x, p0.y, p1.x, p1.y);
  }

  /**
   * Draw a pixel as a filled square
   * 
   * @param x 
   * @param y location
   * @param pixelSize width of square 
   */
  public static void drawPixel(double x, double y, double pixelSize) {
    fillRect(x - pixelSize * .5, y - pixelSize * .5, pixelSize, pixelSize);
  }

  /**
   * Draw a pixel as a filled square
   * 
   * @param pt location
   * @param pixelSize width of square 
   */
  public static void drawPixel(FPoint2 pt, double pixelSize) {
    drawPixel(pt.x, pt.y, pixelSize);
  }

  /**
   * Draw a line segment
   * @param x0
   * @param y0 first endpoint
   * @param x1
   * @param y1 second endpoint
   */
  public static void drawLine(double x0, double y0, double x1, double y1) {
    Line2D.Double wl = new Line2D.Double();
    wl.setLine(x0, y0, x1, y1);
    g.draw(wl);
  }

  /**
   * Save current stroke on stack, set to new
   * @param s new stroke (STRK_xxx)
   */
  public static void pushStroke(int s) {
    pushStroke(s, -1);
  }
  /**
   * Save current stroke on stack, set to new
   * @param s new stroke (STRK_xxx)
   */
  public static void pushStroke(int s, int defaultStroke) {
    if (s < 0)
      s = defaultStroke;

    if (ds)
      Streams.out.println("pushStroken s=" + s);

    if (s >= 0) {
      if (ds)
        Streams.out.println(" saving current stroke " + g.getStroke());

      plotStack.push(g.getStroke());
      setStroke(s);
    } else {
      plotStack.push(ST_IGNORE);
    }
    plotStack.push(ST_STROKE);
  }

  private static final Object ST_STROKE = "STROKE";
  private static final Object ST_IGNORE = "<no val>";
  private static final Object ST_COLOR = "COLOR";
  private static final Object ST_SCALE = "SCALE";
  private static final Object ST_FONT = "FONT";

  /**
   * Draw a filled rectangle
   * @param pos location
   * @param size size
   */
  public static void fillRect(FPoint2 pos, FPoint2 size) {
    fillRect(pos.x, pos.y, size.x, size.y);
  }

  /**
   * Draw a filled rectangle
   * @param r rectangle
   */
  public static void fillRect(FRect r) {
    fillRect(r.x, r.y, r.width, r.height);
  }

  /**
   * Draw a filled rectangle
   * @param x 
   * @param y location
   * @param w width
   * @param h height
   */
  public static void fillRect(double x, double y, double w, double h) {

    final Rectangle2D.Double r = new Rectangle2D.Double();
    r.x = x;
    r.y = y;
    r.width = w;
    r.height = h;
    g.fill(r);
  }

  /**
   * Draw a rectangle
   * @param x 
   * @param y location
   * @param w width
   * @param h height
   */
  public static void drawRect(double x, double y, double w, double h) {
    Rectangle2D.Double r = new Rectangle2D.Double();
    r.x = x;
    r.y = y;
    r.width = w;
    r.height = h;
    g.draw(r);
  }

  private static FRect textRect = new FRect();

  // table of BasicStroke objects for use by application
  private static BasicStroke[] strokes = new BasicStroke[STRK_TOTAL];

  private static FPoint2 clipScreen0, clipScreen1;

  // the transform to convert from logic -> view coords
  private static AffineTransform logicToViewTF = new AffineTransform(),
      viewToLogicTF = new AffineTransform();

  // size, in viewspace, of a 1x1 rectangle in logicspace
  private static double logPixelSize;

  private static double screenScaleFactor = 1.0;

  private static FPoint2 logicalSize;

  /**
   * Get current scale factor
   * @return scale factor
   */
  public static double getScale() {
    return screenScaleFactor;
  }

  /**
   * Get size of view, in view space.  Default is width, height both 100.
   * @return size of view
   */
  public static FPoint2 logicalSize() {
    return logicalSize;
  }

  //private static Dimension physicalSize;
  //  /**
  //   * Get size of view, in physical pixels.  Returns null
  //   * if unknown
  //   * @return size of view, in pixels, or null
  //   */
  //  public static Dimension physicalSize() {
  //    return physicalSize;
  //  }

  /**
   * Cause view to be repainted
   */
  public static void repaint() {
    repaint(0);
  }
  /**
   * Cause view to be repainted after a delay
   * @param tm number of milliseconds
   */
  public static void repaint(long tm) {
    panel.repaint(tm);
  }

  /**
   * Set grid
   * @param g Grid
   */
  public static void setGrid(Grid g) {
    grid = g;
  }

  static Grid grid;

  /**
   * Return a copy of a point, that has been snapped to 
   * the current grid (if it is active)
   * @param pt
   * @return copy of pt, possibly snapped to grid
   */
  public static FPoint2 snapToGrid(FPoint2 pt) {
    if (TestBed.parms.includeGrid && C.vb(TBGlobals.GRIDACTIVE)) {
      pt = grid.snap(pt);
    } else
      pt = new FPoint2(pt);
    return pt;
  }

  static void initGrid() {
    setGrid(new SquareGrid());
    grid.setSize(10, logicalSize());
    if (TestBed.parms.includeGrid)
      updateGridSize(C.vi(TBGlobals.GRIDSIZE));
  }

  static void updateGridSize(int size) {
    grid.setSize(size, logicalSize());
  }

  static void cleanUpRender() {
    if (!plotStack.isEmpty()) {
      Tools.warn("plot stack not empty");
      plotStack.clear();
    }
  }

}
