package testbed;

import java.io.*;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import base.*;

class IPEGraphics extends java.awt.Graphics2D {

  public static final String VERSION = "0.9.0";

  public static final int BLACK_AND_WHITE = 1;

  public static final int GRAYSCALE = 2;

  public static final int RGB = 3; // Default

  /**
   * Constructs a new EPS document that is initially empty and can be
   * drawn on like a Graphics2D object.  The EPS document is stored in
   * memory.
   */
  public IPEGraphics(FRect bounds) {
    this("Untitled", bounds);
  }

  /**
   * Constructs a new EPS document that is initially empty and can be
   * drawn on like a Graphics2D object.  The EPS document is stored in
   * memory.
   */
  private IPEGraphics(String title, FRect bounds) {
    this.bounds = bounds;
    xml = new IPEDocument(bounds);

    _backgroundColor = Color.white;
    _clip = null;
    _transform = new AffineTransform();
    _clipTransform = new AffineTransform();
    //    _accurateTextMode = true;
    _colorDepth = EpsGraphics2D.RGB;
    setColor(Color.black);
    setPaint(Color.black);
    setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
    setFont(Font.decode(null));
    setStroke(new BasicStroke());
    //    setFixedBounds(bounds);
  }

  /**
   * Constructs a new EpsGraphics2D instance that is a copy of the
   * supplied argument and points at the same EpsDocument.
   */
  protected IPEGraphics(IPEGraphics g) {
    xml = g.xml;
    _backgroundColor = g._backgroundColor;
    _clip = g._clip;
    _clipTransform = (AffineTransform) g._clipTransform.clone();
    _transform = (AffineTransform) g._transform.clone();
    _color = g._color;
    _paint = g._paint;
    _composite = g._composite;
    _font = g._font;
    _stroke = g._stroke;
    _accurateTextMode = g._accurateTextMode;
    _colorDepth = g._colorDepth;
  }

  /**
   * This method is called to indicate that a particular method is not
   * supported yet.  The stack trace is printed to the standard output.
   */
  private void methodNotSupported() {
    EpsException e = new EpsException(
        "Method not currently supported by EpsGraphics2D version " + VERSION);
    e.printStackTrace(System.err);
  }

  /////////////// Specialist methods ///////////////////////

  /**
   * Sets whether to use accurate text mode when rendering text in EPS.
   * This is enabled (true) by default. When accurate text mode is used,
   * all text will be rendered in EPS to appear exactly the same as it
   * would do when drawn with a Graphics2D context. With accurate text
   * mode enabled, it is not necessary for the EPS viewer to have the
   * required font installed.
   * <p>
   * Turning off accurate text mode will require the EPS viewer to have
   * the necessary fonts installed. If you are using a lot of text, you
   * will find that this significantly reduces the file size of your EPS
   * documents.  AffineTransforms can only affect the starting point of
   * text using this simpler text mode - all text will be horizontal.
   */
  public void setAccurateTextMode(boolean b) {
    _accurateTextMode = b;

    if (!getAccurateTextMode()) {
      setFont(getFont());
    }
  }

  /**
   * Returns whether accurate text mode is being used.
   */
  public boolean getAccurateTextMode() {
    return _accurateTextMode;
  }

  /**
   * Sets the number of colours to use when drawing on the document.
   * Can be either
   * EpsGraphics2D.RGB (default) or EpsGraphics2D.GREYSCALE.
   */
  public void setColorDepth(int c) {
    if (c == RGB || c == GRAYSCALE || c == BLACK_AND_WHITE) {
      _colorDepth = c;
    }
  }

  /**
   * Returns the color depth used for all drawing operations. This can be
   * either EpsGraphics2D.RGB (default) or EpsGraphics2D.GREYSCALE.
   */
  public int getColorDepth() {
    return _colorDepth;
  }

  /**
   * Flushes the buffered contents of this EPS document to the underlying
   * OutputStream it is being written to.
   */
  public void flush() throws IOException {
    xml.flush();
  }

  /**
   * Closes the EPS file being output to the underlying OutputStream.
   * The OutputStream is automatically flushed before being closed.
   * If you forget to do this, the file may be incomplete.
   */
  public void close() throws IOException {
    flush();
    xml.close();
  }

  /**
   * Returns the point after it has been transformed by the transformation.
   */
  private Point2D transform(float x, float y) {
    Point2D result = new Point2D.Float(x, y);
    result = _transform.transform(result, result);
    result.setLocation(result.getX(), -result.getY());
    return result;
  }

  private void ta() {
    tSaved = new AffineTransform(_transform);
    _transform.preConcatenate(AffineTransform.getTranslateInstance(0,
        -bounds.height));
  }

  private void tb() {
    _transform = tSaved;
  }

  private AffineTransform tSaved;

  /**
   * Appends the commands required to draw a shape on the EPS document.
   */
  private void draw(Shape s, String action) {
    boolean db = false;
    if (db)
      Streams.out.println("draw shape: " + s);

    if (s != null) {
      ta();
      s = _transform.createTransformedShape(s);
      synchronized (xml) {
        XMLTree path = xml.addTag("path");
        path.addAttribute(action, colorString());
        path.addAttribute("pen", penStr());
        String dashStr = dashStr();
        if (dashStr != null) {
          path.addAttribute("dash", dashStr); // not sure about this
        }

        //        xml.addAttribute(path, "cap", "1");
        //        xml.addAttribute(path, "join", "1");
        int type = 0;
        float[] coords = new float[6];
        PathIterator it = s.getPathIterator(null);
        float x0 = 0;
        float y0 = 0;
//        int count = 0;
        while (!it.isDone()) {
          type = it.currentSegment(coords);
          float x1 = coords[0];
          float y1 = -coords[1];
          float x2 = coords[2];
          float y2 = -coords[3];
          float x3 = coords[4];
          float y3 = -coords[5];

          if (type == PathIterator.SEG_CLOSE) {
            cr();
            xml.addContent("h"); //Element("h");
            //            xml.addText("h");
            cr();
          } else if (type == PathIterator.SEG_CUBICTO) {
            add(x1);
            add(y1);
            add(x2);
            add(y2);
            add(x3);
            add(y3);
            add("c");
            cr();
            x0 = x3;
            y0 = y3;
          } else if (type == PathIterator.SEG_LINETO) {
            add(x1);
            add(y1);
            add("l");
            cr();
            x0 = x1;
            y0 = y1;
          } else if (type == PathIterator.SEG_MOVETO) {
            add(x1);
            add(y1);
            add("m");
            cr();
            x0 = x1;
            y0 = y1;
          } else if (type == PathIterator.SEG_QUADTO) {
            // Convert the quad curve into a cubic.
            float _x1 = x0 + 2 / 3f * (x1 - x0);
            float _y1 = y0 + 2 / 3f * (y1 - y0);
            float _x2 = x1 + 1 / 3f * (x2 - x1);
            float _y2 = y1 + 1 / 3f * (y2 - y1);
            float _x3 = x2;
            float _y3 = y2;
            add(_x1);
            add(_y1);
            add(_x2);
            add(_y2);
            add(_x3);
            add(_y3);
            add("c");
            cr();
            x0 = _x3;
            y0 = _y3;
          } else if (type == PathIterator.WIND_EVEN_ODD) {
            // Ignore.
          } else if (type == PathIterator.WIND_NON_ZERO) {
            // Ignore.
          }
          it.next();
        }
      }
      tb();
    }
  }
  /////////////// Graphics2D methods ///////////////////////

  /**
   * Draws a 3D rectangle outline.  If it is raised, light appears to come
   * from the top left.
   */
  public void draw3DRect(int x, int y, int width, int height, boolean raised) {
    Color originalColor = getColor();
    Stroke originalStroke = getStroke();

    setStroke(new BasicStroke(1.0f));

    if (raised) {
      setColor(originalColor.brighter());
    } else {
      setColor(originalColor.darker());
    }

    drawLine(x, y, x + width, y);
    drawLine(x, y, x, y + height);

    if (raised) {
      setColor(originalColor.darker());
    } else {
      setColor(originalColor.brighter());
    }

    drawLine(x + width, y + height, x, y + height);
    drawLine(x + width, y + height, x + width, y);

    setColor(originalColor);
    setStroke(originalStroke);
  }

  /**
   * Fills a 3D rectangle.  If raised, it has bright fill and light appears
   * to come from the top left.
   */
  public void fill3DRect(int x, int y, int width, int height, boolean raised) {
    Color originalColor = getColor();

    if (raised) {
      setColor(originalColor.brighter());
    } else {
      setColor(originalColor.darker());
    }
    draw(new Rectangle(x, y, width, height), "fill");
    setColor(originalColor);
    draw3DRect(x, y, width, height, raised);
  }

  /**
   * Draws a Shape on the EPS document.
   */
  public void draw(Shape s) {
    draw(s, "stroke");
  }

  /**
   * Draws an Image on the EPS document.
   */
  public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
    AffineTransform at = getTransform();
    transform(xform);
    boolean st = drawImage(img, 0, 0, obs);
    setTransform(at);
    return st;
  }

  /**
   * Draws a BufferedImage on the EPS document.
   */
  public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
    BufferedImage img1 = op.filter(img, null);
    drawImage(img1, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
  }

  /**
   * Draws a RenderedImage on the EPS document.
   */
  public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
    Hashtable properties = new Hashtable();
    String[] names = img.getPropertyNames();
    for (int i = 0; i < names.length; i++) {
      properties.put(names[i], img.getProperty(names[i]));
    }

    ColorModel cm = img.getColorModel();
    WritableRaster wr = img.copyData(null);
    BufferedImage img1 = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(),
        properties);
    AffineTransform at = AffineTransform.getTranslateInstance(img.getMinX(),
        img.getMinY());
    at.preConcatenate(xform);
    drawImage(img1, at, null);
  }

  /**
   * Draws a RenderableImage by invoking its createDefaultRendering method.
   */
  public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
    drawRenderedImage(img.createDefaultRendering(), xform);
  }

  /**
   * Draws a string at (x,y)
   */
  public void drawString(String str, int x, int y) {
    drawString(str, (float) x, (float) y);
  }

  /**
   * Draws a string at (x,y)
   */
  public void drawString(String s, float x, float y) {
    if (s != null && s.length() > 0) {
      AttributedString as = new AttributedString(s);
      as.addAttribute(TextAttribute.FONT, getFont());
      drawString(as.getIterator(), x, y);
    }
  }

  /**
   * Draws the characters of an AttributedCharacterIterator, starting from
   * (x,y).
   */
  public void drawString(AttributedCharacterIterator iterator, int x, int y) {
    drawString(iterator, (float) x, (float) y);
  }

  /**
   * Draws the characters of an AttributedCharacterIterator, starting from
   * (x,y).
   */
  public void drawString(AttributedCharacterIterator iterator, float x, float y) {
    if (getAccurateTextMode()) {
      TextLayout layout = new TextLayout(iterator, getFontRenderContext());
      Shape shape = layout.getOutline(AffineTransform
          .getTranslateInstance(x, y));
      draw(shape, "fill");
    } else {
      ta();
      Point2D loc = transform(x, y);

      FontMetrics m = getFontMetrics();

      synchronized (xml) {
        int wd = 0;
        StringBuffer buffer = new StringBuffer();
        for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch = iterator
            .next()) {
          buffer.append(ch);
          wd += m.charWidth(ch);
        }
        //        Streams.out.println("wd="+wd+", height="+m.getHeight());

        XMLTree e = xml.addTag("text");
        e.addAttribute("stroke", colorString());
        e.addAttribute("pos", str(loc.getX()) + str(loc.getY()));
        e.addAttribute("type", "minipage");
        e.addAttribute("width", "" + wd);
        e.addAttribute("valign", "top");
        e.addAttribute("size", "huge");
        //        "normal");

        xml.addContent(e, buffer.toString());
      }
      tb();
    }
  }

  /**
   * Draws a GlyphVector at (x,y)
   */
  public void drawGlyphVector(GlyphVector g, float x, float y) {
    Shape shape = g.getOutline(x, y);
    draw(shape, "fill");
  }

  /**
   * Fills a Shape on the EPS document.
   */
  public void fill(Shape s) {
    draw(s, "fill");
  }

  /**
   * Checks whether or not the specified Shape intersects the specified
   * Rectangle, which is in device space.
   */
  public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
    return s.intersects(rect);
  }

  /**
   * Returns the device configuration associated with this EpsGraphics2D
   * object.
   */
  public GraphicsConfiguration getDeviceConfiguration() {
    GraphicsConfiguration gc = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gds = ge.getScreenDevices();
    for (int i = 0; i < gds.length; i++) {
      GraphicsDevice gd = gds[i];
      GraphicsConfiguration[] gcs = gd.getConfigurations();
      if (gcs.length > 0) {
        return gcs[0];
      }
    }
    return gc;
  }

  /**
   * Sets the Composite to be used by this EpsGraphics2D.  EpsGraphics2D
   * does not make use of these.
   */
  public void setComposite(Composite comp) {
    _composite = comp;
  }

  /**
   * Sets the Paint attribute for the EpsGraphics2D object.  Only Paint
   * objects of type Color are respected by EpsGraphics2D.
   */
  public void setPaint(Paint paint) {
    _paint = paint;
    if (paint instanceof Color) {
      setColor((Color) paint);
    }
  }

  /**
   * Sets the stroke.  Only accepts BasicStroke objects (or subclasses of
   * BasicStroke).
   */
  public void setStroke(Stroke s) {
    if (s instanceof BasicStroke) {
      _stroke = (BasicStroke) s;
    }
  }

  /**
   * Sets a rendering hint. These are not used by EpsGraphics2D.
   */
  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
    // Do nothing.
  }

  /**
   * Returns the value of a single preference for the rendering
   * algorithms.  Rendering hints are not used by EpsGraphics2D.
   */
  public Object getRenderingHint(RenderingHints.Key hintKey) {
    return null;
  }

  /**
   * Sets the rendering hints.  These are ignored by EpsGraphics2D.
   */
  public void setRenderingHints(Map hints) {
    // Do nothing.
  }

  /**
   * Adds rendering hints.  These are ignored by EpsGraphics2D.
   */
  public void addRenderingHints(Map hints) {
    // Do nothing.
  }

  /**
   * Returns the preferences for the rendering algorithms.
   */
  public RenderingHints getRenderingHints() {
    return new RenderingHints(null);
  }

  /**
   * Translates the origin of the EpsGraphics2D context to the point (x,y)
   * in the current coordinate system.
   */
  public void translate(int x, int y) {
    translate((double) x, (double) y);
  }

  /**
   * Concatenates the current EpsGraphics2D Transformation with a
   * translation transform.
   */
  public void translate(double tx, double ty) {
    transform(AffineTransform.getTranslateInstance(tx, ty));
  }

  /**
   * Concatenates the current EpsGraphics2D Transform with a rotation
   * transform.
   */
  public void rotate(double theta) {
    rotate(theta, 0, 0);
  }

  /**
   * Concatenates the current EpsGraphics2D Transform with a translated
   * rotation transform.
   */
  public void rotate(double theta, double x, double y) {
    transform(AffineTransform.getRotateInstance(theta, x, y));
  }

  /**
   * Concatenates the current EpsGraphics2D Transform with a scaling
   * transformation.
   */
  public void scale(double sx, double sy) {
    transform(AffineTransform.getScaleInstance(sx, sy));
  }

  /**
   * Concatenates the current EpsGraphics2D Transform with a shearing
   * transform.
   */
  public void shear(double shx, double shy) {
    transform(AffineTransform.getShearInstance(shx, shy));
  }

  /**
   * Composes an AffineTransform object with the Transform in this
   * EpsGraphics2D according to the rule last-specified-first-applied.
   */
  public void transform(AffineTransform Tx) {
    _transform.concatenate(Tx);
    setTransform(getTransform());
  }

  /**
   * Sets the AffineTransform to be used by this EpsGraphics2D.
   */
  public void setTransform(AffineTransform Tx) {
    if (Tx == null) {
      _transform = new AffineTransform();
    } else {
      _transform = new AffineTransform(Tx);
    }
    // Need to update the stroke and font so they know the scale changed
    setStroke(getStroke());
    setFont(getFont());
  }

  /**
   * Gets the AffineTransform used by this EpsGraphics2D.
   */
  public AffineTransform getTransform() {
    return new AffineTransform(_transform);
  }

  /**
   * Returns the current Paint of the EpsGraphics2D object.
   */
  public Paint getPaint() {
    return _paint;
  }

  /**
   * returns the current Composite of the EpsGraphics2D object.
   */
  public Composite getComposite() {
    return _composite;
  }

  /**
   * Sets the background color to be used by the clearRect method.
   */
  public void setBackground(Color color) {
    if (color == null) {
      color = Color.black;
    }
    _backgroundColor = color;
  }

  /**
   * Gets the background color that is used by the clearRect method.
   */
  public Color getBackground() {
    return _backgroundColor;
  }

  /**
   * Returns the Stroke currently used.  Guaranteed to be an instance of
   * BasicStroke.
   */
  public Stroke getStroke() {
    return _stroke;
  }

  /**
   * Intersects the current clip with the interior of the specified Shape
   * and sets the clip to the resulting intersection.
   */
  public void clip(Shape s) {
    if (_clip == null) {
      setClip(s);
    } else {
      Area area = new Area(_clip);
      area.intersect(new Area(s));
      setClip(area);
    }
  }

  /**
   * Returns the FontRenderContext.
   */
  public FontRenderContext getFontRenderContext() {
    return _fontRenderContext;
  }

  /////////////// Graphics methods ///////////////////////

  /**
   * Returns a new Graphics object that is identical to this EpsGraphics2D.
   */
  public Graphics create() {
    return new IPEGraphics(this);
  }

  /**
   * Returns an EpsGraphics2D object based on this
   * Graphics object, but with a new translation and clip
   * area.
   */
  public Graphics create(int x, int y, int width, int height) {
    Graphics g = create();
    g.translate(x, y);
    g.clipRect(0, 0, width, height);
    return g;
  }

  /**
   * Returns the current Color.  This will be a default value (black)
   * until it is changed using the setColor method.
   */
  public Color getColor() {
    return _color;
  }

  /**
   * Sets the Color to be used when drawing all future shapes, text, etc.
   */
  public void setColor(Color c) {
    if (c == null) {
      c = Color.black;
    }
    if (getColorDepth() == BLACK_AND_WHITE) {
      float value = 0;
      if (c.getRed() + c.getGreen() + c.getBlue() > 255 * 1.5 - 1) {
        value = 1;
      }
      c = new Color(value, value, value);
      //      append(value + " setgray");
    } else if (getColorDepth() == GRAYSCALE) {
      float value = ((c.getRed() + c.getGreen() + c.getBlue()) / (3 * 255f));
      c = new Color(value, value, value);
      //      append(value + " setgray");
    } else {
      //
      //      append((c.getRed() / 255f) + " " + (c.getGreen() / 255f) + " "
      //          + (c.getBlue() / 255f) + " setrgbcolor");
    }
    _color = c;
  }

  /**
   * Sets the paint mode of this EpsGraphics2D object to overwrite the
   * destination EpsDocument with the current color.
   */
  public void setPaintMode() {
    // Do nothing - paint mode is the only method supported anyway.
  }

  /**
   * <b><i><font color="red">Not implemented</font></i></b> - performs no action.
   */
  public void setXORMode(Color c1) {
    methodNotSupported();
  }

  /**
   * Returns the Font currently being used.
   */
  public Font getFont() {
    return _font;
  }

  /**
   * Sets the Font to be used in future text.
   */
  public void setFont(Font font) {
    if (font == null) {
      font = Font.decode(null);
    }
    _font = font;
    //    if (!getAccurateTextMode()) {
    //      append("/" + _font.getPSName() + " findfont " + ((int) _font.getSize())
    //          + " scalefont setfont");
    //    }
  }

  /**
   * Gets the font metrics of the current font.
   */
  public FontMetrics getFontMetrics() {
    return getFontMetrics(getFont());
  }

  /**
   * Gets the font metrics for the specified font.
   */
  public FontMetrics getFontMetrics(Font f) {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    FontMetrics out = null;
    Graphics g = null;
    try {
      g = image.getGraphics();
      out = g.getFontMetrics(f);
    } finally {
      g.dispose();
    }
    return out;
  }

  /**
   * Returns the bounding rectangle of the current clipping area.
   */
  public Rectangle getClipBounds() {
    if (_clip == null) {
      return null;
    }
    Rectangle rect = getClip().getBounds();
    return rect;
  }

  /**
   * Intersects the current clip with the specified rectangle.
   */
  public void clipRect(int x, int y, int width, int height) {
    clip(new Rectangle(x, y, width, height));
  }

  /**
   * Sets the current clip to the rectangle specified by the given
   * coordinates.
   */
  public void setClip(int x, int y, int width, int height) {
    setClip(new Rectangle(x, y, width, height));
  }

  /**
   * Gets the current clipping area.
   */
  public Shape getClip() {
    if (_clip == null) {
      return null;
    } else {
      try {
        AffineTransform t = _transform.createInverse();
        t.concatenate(_clipTransform);
        return t.createTransformedShape(_clip);
      } catch (Exception e) {
        throw new EpsException("Unable to get inverse of matrix: " + _transform);
      }
    }
  }

  /**
   * Sets the current clipping area to an arbitrary clip shape.
   */
  public void setClip(Shape clip) {
    Tools.unimp();
  }

  /**
   * <b><i><font color="red">Not implemented</font></i></b> - performs no action.
   */
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    methodNotSupported();
  }

  /**
   * Draws a straight line from (x1,y1) to (x2,y2).
   */
  public void drawLine(int x1, int y1, int x2, int y2) {
    Shape shape = new Line2D.Float(x1, y1, x2, y2);
    draw(shape);
  }

  /**
   * Fills a rectangle with top-left corner placed at (x,y).
   */
  public void fillRect(int x, int y, int width, int height) {
    Shape shape = new Rectangle(x, y, width, height);
    draw(shape, "fill");
  }

  /**
   * Draws a rectangle with top-left corner placed at (x,y).
   */
  public void drawRect(int x, int y, int width, int height) {
    Shape shape = new Rectangle(x, y, width, height);
    draw(shape);
  }

  /**
   * Clears a rectangle with top-left corner placed at (x,y) using the
   * current background color.
   */
  public void clearRect(int x, int y, int width, int height) {
    Color originalColor = getColor();

    setColor(getBackground());
    Shape shape = new Rectangle(x, y, width, height);
    draw(shape, "fill");

    setColor(originalColor);
  }

  /**
   * Draws a rounded rectangle.
   */
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
      int arcHeight) {
    Shape shape = new RoundRectangle2D.Float(x, y, width, height, arcWidth,
        arcHeight);
    draw(shape);
  }

  /**
   * Fills a rounded rectangle.
   */
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
      int arcHeight) {
    Shape shape = new RoundRectangle2D.Float(x, y, width, height, arcWidth,
        arcHeight);
    draw(shape, "fill");
  }

  /**
   * Draws an oval.
   */
  public void drawOval(int x, int y, int width, int height) {
    Shape shape = new Ellipse2D.Float(x, y, width, height);
    draw(shape);
  }

  /**
   * Fills an oval.
   */
  public void fillOval(int x, int y, int width, int height) {
    Shape shape = new Ellipse2D.Float(x, y, width, height);
    draw(shape, "fill");
  }

  /**
   * Draws an arc.
   */
  public void drawArc(int x, int y, int width, int height, int startAngle,
      int arcAngle) {
    Shape shape = new Arc2D.Float(x, y, width, height, startAngle, arcAngle,
        Arc2D.OPEN);
    draw(shape);
  }

  /**
   * Fills an arc.
   */
  public void fillArc(int x, int y, int width, int height, int startAngle,
      int arcAngle) {
    Shape shape = new Arc2D.Float(x, y, width, height, startAngle, arcAngle,
        Arc2D.PIE);
    draw(shape, "fill");
  }

  /**
   * Draws a polyline.
   */
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    if (nPoints > 0) {
      GeneralPath path = new GeneralPath();
      path.moveTo(xPoints[0], yPoints[0]);
      for (int i = 1; i < nPoints; i++) {
        path.lineTo(xPoints[i], yPoints[i]);
      }
      draw(path);
    }
  }

  /**
   * Draws a polygon made with the specified points.
   */
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    Shape shape = new Polygon(xPoints, yPoints, nPoints);
    draw(shape);
  }

  /**
   * Draws a polygon.
   */
  public void drawPolygon(Polygon p) {
    draw(p);
  }

  /**
   * Fills a polygon made with the specified points.
   */
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    Shape shape = new Polygon(xPoints, yPoints, nPoints);
    draw(shape, "fill");
  }

  /**
   * Fills a polygon.
   */
  public void fillPolygon(Polygon p) {
    draw(p, "fill");
  }

  /**
   * Draws the specified characters, starting from (x,y)
   */
  public void drawChars(char[] data, int offset, int length, int x, int y) {
    String string = new String(data, offset, length);
    drawString(string, x, y);
  }

  /**
   * Draws the specified bytes, starting from (x,y)
   */
  public void drawBytes(byte[] data, int offset, int length, int x, int y) {
    String string = new String(data, offset, length);
    drawString(string, x, y);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
    return drawImage(img, x, y, Color.white, observer);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int x, int y, int width, int height,
      ImageObserver observer) {
    return drawImage(img, x, y, width, height, Color.white, observer);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int x, int y, Color bgcolor,
      ImageObserver observer) {
    int width = img.getWidth(null);
    int height = img.getHeight(null);
    return drawImage(img, x, y, width, height, bgcolor, observer);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int x, int y, int width, int height,
      Color bgcolor, ImageObserver observer) {
    return drawImage(img, x, y, x + width, y + height, 0, 0, width, height,
        bgcolor, observer);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
      int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
    return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, Color.white,
        observer);
  }

  /**
   * Draws an image.
   */
  public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
      int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
    Tools.unimp();
    return true;
  }

  /**
   * Disposes of all resources used by this EpsGraphics2D object.
   * If this is the only remaining EpsGraphics2D instance pointing at
   * a EpsDocument object, then the EpsDocument object shall become
   * eligible for garbage collection.
   */
  public void dispose() {
    xml = null;
  }

  /**
   * Finalizes the object.
   */
  public void finalize() {
    super.finalize();
  }

  /**
   * Returns the entire contents of the EPS document, complete with
   * headers and bounding box.  The returned String is suitable for
   * being written directly to disk as an EPS file.
   */
  public String toString() {
    StringWriter writer = new StringWriter();
    try {
      xml.write(writer);
      xml.flush();
      xml.close();
    } catch (IOException e) {
      throw new EpsException(e.toString());
    }
    return writer.toString();
  }

  /**
   * Returns true if the specified rectangular area might intersect the
   * current clipping area.
   */
  public boolean hitClip(int x, int y, int width, int height) {
    if (_clip == null) {
      return true;
    }
    Rectangle rect = new Rectangle(x, y, width, height);
    return hit(rect, _clip, true);
  }

  /**
   * Returns the bounding rectangle of the current clipping area.
   */
  public Rectangle getClipBounds(Rectangle r) {
    if (_clip == null) {
      return r;
    }
    Rectangle rect = getClipBounds();
    r.setLocation((int) rect.getX(), (int) rect.getY());
    r.setSize((int) rect.getWidth(), (int) rect.getHeight());
    return r;
  }

  private Color _color;

  private Color _backgroundColor;

  private Paint _paint;

  private Composite _composite;

  private BasicStroke _stroke;

  private Font _font;

  private Shape _clip;

  private AffineTransform _clipTransform;

  private AffineTransform _transform;

  private boolean _accurateTextMode;

  private int _colorDepth;

  private IPEDocument xml;

  private FRect bounds;

  private static FontRenderContext _fontRenderContext = new FontRenderContext(
      null, false, true);

  private String colorStr;

  private Color prevColor;

  private String colorString() {
    if (colorStr == null || prevColor == null || !prevColor.equals(_color)) {
      int r = _color.getRed();
      int g = _color.getGreen();
      int b = _color.getBlue();
      if (r == g && r == b) {
        colorStr = str(r / 255.0);
      } else {
        colorStr = str(r / 255.0) + str(g / 255.0) + str(b / 255.0);
      }
    }
    //    Streams.out.println("colorString for "+_color+" = "+colorStr);
    return colorStr;
  }

  private String penStr;

  private Stroke prevStroke;

  private String dashStr;

  private String penStr() {
    recalcPenStuff();
    return penStr;
  }

  private void recalcPenStuff() {
    if (penStr == null || !_stroke.equals(prevStroke)) {
      penStr = str(_stroke.getLineWidth());

      {
        dashStr = null;
        StringBuffer dashes = new StringBuffer();
        float[] dashArray = _stroke.getDashArray();
        if (dashArray != null) {
          dashes.append("[");
          for (int i = 0; i < dashArray.length; i++) {
            double da = dashArray[i];
            //            da = da *  4 * 1.15f;
            dashes.append(str(da));
          }
          dashes.append("]");
          dashStr = dashes.toString();
        }
      }

      prevStroke = _stroke;
    }
  }

  private String dashStr() {
    recalcPenStuff();
    return dashStr;
  }

  private void add(double d) {
    //  Tools.ASSERT(d >= -1e4 && d <= 1e4);
    xml.addContent(str(d));
  }

  private void add(String s) {
    xml.addContent(s);
    xml.addContent(" ");
  }

  public void cr() {
    if (false)
      xml.addContent("\n");
  }

  public static String str(double d) {
    String s = Double.toString(Math.round(d * 1000) / 1000.0);
    if (s.endsWith(".0"))
      s = s.substring(0, s.length() - 2);
    return s + " ";
  }
}
