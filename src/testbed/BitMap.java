package testbed;

import java.awt.*;
import java.awt.RenderingHints.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import testbed.*;
import base.*;

public class BitMap implements Renderable {

  public static void main(String[] args) {

    int n = (1 << 1) | (1 << 5) | (1 << 6);

    System.out.println("n= " + n);

    // set bit #3
    n |= (1 << 3);
    System.out.println("n= " + n);

    // clear bit #3
    n &= ~(1 << 3);
    System.out.println("n= " + n);

    // clear bit #3 again (no effect)
    n &= ~(1 << 3);
    System.out.println("n= " + n);

    if ((n & (1 << 7)) != 0)
      System.out.println("bit 7 is set");
    else
      System.out.println("bit 7 is not set");
  }

  /**
   * Determine how many bits are set in an integer
   * @param n integer
   * @return number of set bits, 0..32
   */
  public static int countBits(int n) {
    throw new UnsupportedOperationException("implement this");
  }

  private static final boolean db = false;

  private boolean construct(String descr, int width, int height,
      double pixelSize) {

    if (descr == null)
      descr = "";

    boolean constructed = false;

    //    Streams.out.println("BitMap, descr=\n"+descr+"\nprev=\n"+description);
    if (!descr.equals(this.description) || pixelSize != this.pixelSize
        || this.width != width || this.height != height) {

      if (db)
        Streams.out.println("constructing, width=" + width + " height="
            + height + " pixelSize=" + pixelSize);

      constructed = true;
      this.objMatrix = null;
      this.description = descr;
      this.pixelSize = pixelSize;
      this.width = width;
      this.height = height;
      if (this.imgGraph != null) {
        imgGraph.dispose();
        imgGraph = null;
      }

      this.img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      this.toGrid = AffineTransform.getScaleInstance(1 / pixelSize,
          1 / pixelSize);
      this.imgGraph = img.createGraphics();

      rect = new Rectangle2D.Double(0, 0, pixelSize, pixelSize);

      // imgGraph.setColor(new Color(20,0,0,0));

      // imgGraph.fillRect(0,0,width,height);
      clearTo(V.getBackgroundColor());
      //      imgGraph.setBackground(Color.white);
      //      if (db) {
      //        imgGraph.setBackground(new Color(0xf0, 0xf0, 0xf0));
      //      }
      imgGraph.clearRect(0, 0, width, height);

      imgGraph.setTransform(toGrid);

      imgGraph.setStroke(new BasicStroke(.1f));

    }
    return constructed;
  }

  public void clearTo(Color bgnd) {
    imgGraph.setBackground(bgnd);
    imgGraph.clearRect(0, 0, width, height);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PointSamples2");
    sb.append(" w=" + width);
    sb.append(" h=" + height);
    return sb.toString();
  }

  /**
   * Constructor
   * @param width width of bitmap, in pixels
   * @param height height of bitmap, in pixels
   * @param pixelSize size of each pixel, in world coordinates
   */
  public BitMap(String descr, int width, int height, double pixelSize) {
    construct(descr, width, height, pixelSize);
  }

  /**
   * Constructor
   * Sets dimensions, pixel size to match viewport on screen
   */
  public BitMap() {
    double pixelSize = 2.0;
    FPoint2 vSize = V.logicalSize();
    construct(null, (int) Math.round(vSize.x * pixelSize), (int) Math
        .round(vSize.y * pixelSize), pixelSize);
  }

  /**
   * Prepare bitmap for possible new dimensions.  If changed, clears bitmap
   * @param width
   * @param height
   * @param pixelSize
   * @return true if any of the parameters changed since last constructed
   */
  private boolean prepare(String descr, int width, int height, double pixelSize) {
    return construct(descr, width, height, pixelSize);
  }

  /**
   * Prepare bitmap for new pixel size
   * @param pixelSize
   * @return true if bitmap size changed (and has been cleared)
   */
  public boolean prepare(String descr, double pixelSize) {
    FPoint2 vSize = V.logicalSize();
    return prepare(descr, (int) Math.round(vSize.x / pixelSize), (int) Math
        .round(vSize.y / pixelSize), pixelSize);
  }

  /**
   * Augment bitmap with matrix of object references for each pixel
   */
  public void addObjectMatrix() {
    if (objMatrix == null) {
      objMatrix = new RowEntry[height];
      //      for (int i = 0; i < objMatrix.length; i++)
      //        objMatrix[i] = new RowEntry();
    }
  }

  private Rectangle2D.Double rect;

  /**
  * Plot a point
  * @param x
  * @param y
  */
  public void plot(double x, double y, Object sample) {
    rect.x = x;
    rect.y = y;
    imgGraph.fill(rect);
    if (sample != null && objMatrix != null) {
      IPoint2 cell = calcCell(x, y);
      horzLine(cell.x, cell.x, cell.y, sample);
    }

  }

  /**
   * Plot a point
   * @param pt
   */
  public void plot(FPoint2 pt, Object sample) {
    plot(pt.x, pt.y, sample);
  }

  private IPoint2 calcCell(double x, double y) {
    IPoint2 ret2 = new IPoint2((int) Math.round(x / pixelSize), (int) Math
        .round(y / pixelSize));

    //    if (ret.x != ret2.x || ret.y != ret2.y)
    //      System.out.println("calcCell "+pt+" yielded\n "+ret+"\n "+ret2);
    return ret2;
  }

  private IPoint2 calcCell(FPoint2 pt) {
    return calcCell(pt.x, pt.y);
  }
  //  
  //    //    IPoint2 ret =  toMatrix(pt, null);
  //    IPoint2 ret2 = new IPoint2((int) Math.round(pt.x / pixelSize), (int) Math
  //        .round(pt.y / pixelSize));
  //
  //    //    if (ret.x != ret2.x || ret.y != ret2.y)
  //    //      System.out.println("calcCell "+pt+" yielded\n "+ret+"\n "+ret2);
  //    return ret2;
  //  }

  public Object findObjectAt(FPoint2 pt) {

    final boolean db = false;

    Object ret = null;

    if (objMatrix != null) {
      IPoint2 ipt = calcCell(pt);
      if (db)
        Streams.out.println("findObjectAt " + pt + " cell=" + ipt);
      if (ipt.y >= 0 && ipt.y < objMatrix.length) {
        RowEntry e0 = objMatrix[ipt.y];
        while (e0 != null) {
          if (db)
            Streams.out.println(" entry= " + e0);

          if (e0.x > ipt.x)
            break;
          if (e0.x + e0.width <= ipt.x) {
            e0 = e0.next;
            continue;
          }
          ret = e0.object;
          break;
        }
      }
      if (db)
        Streams.out.println(" found " + ret);
    }
    return ret;

  }

  //  private static boolean segsOverlap(int x0, int len0, int x1, int len1) {
  //    return !(x0 >= x1 + len1 || x1 >= x0 + len0);
  //  }
  //
  private void horzLine(int x0, int x1, int y, Object obj) {

    //    System.out.println("horzLine "+x0+" ... "+x1+" at "+y+"  ti="+ti);
    //   boolean garg = !testMode || (ti++ == 9);
    //  //  if (!garg) return;
    //    
    //    

    final boolean db = false; //testMode && garg;

    if (!(y >= 0 && y < objMatrix.length))
      return;

    // if any pixels are new, replace all
    boolean newPix = false;
    {
      int t0 = x0;
      //   do {
      RowEntry e0 = objMatrix[y];

      while (true) {
        // no pixels at all?
        if (e0 == null) {
          newPix = true;
          break;
        }
        // existing segs start after new seg?
        if (e0.x > t0) {
          newPix = true;
          break;
        }
        // does existing seg end after new seg?
        if (e0.endX() >= x1 + 1) {
          newPix = false;
          break;
        }
        t0 = e0.endX();
        e0 = e0.next;
      }

    }
    if (!newPix)
      return;

    if (db) {
      System.out.println("\n hl:" + y + " " + x0 + " to " + x1);

      showLine(y);
    }

    // construct entry for new segment in row
    RowEntry eNew = new RowEntry(x0, x1 + 1 - x0, obj);
    int eNewEnd = eNew.endX();

    // ePrev is existing segment before new one, or null
    // if new segment is first in row
    RowEntry ePrev = null;

    // eNext is first segment following new one, or null
    RowEntry eNext = null;

    RowEntry e0 = objMatrix[y];

    while (true) {
      // if no more old entries exist, done
      if (e0 == null)
        break;

      int e0End = e0.endX();

      // if old entry is completely before start of new one,
      // retain it
      if (e0End <= x0) {
        if (ePrev == null) {
          ePrev = e0;
        } else {
          ePrev.next = e0;
          ePrev = e0;
        }
        e0 = e0.next;
        continue;
      }

      // if old entry is partially before new one, trim its size
      int trimLeft = x0 - e0.x;
      if (trimLeft > 0) {
        e0.width = trimLeft;
        continue;
      }

      // old entry starts at or after new one.

      // if old entry ends before end of this one, remove it
      if (e0End <= eNewEnd) {
        RowEntry e1 = e0.next;
        e0 = e1;
        continue;
      }

      // if old entry starts after new one, we've found insertion point
      if (e0.x >= x1 + 1) {
        eNext = e0;
        break;
      }

      // old entry starts before end of new one; trim size

      int adjust = x1 + 1 - e0.x;

      e0.width -= adjust;
      e0.x += adjust;
      continue;

    }

    if (ePrev == null) {
      objMatrix[y] = eNew;
    } else
      ePrev.next = eNew;

    if (eNext != null)
      eNew.next = eNext;
    if (db) {
      System.out.println("after modifying:");
      showLine(y);
    }

  }

  private void showLine(int y) {
    System.out.println("y=" + y + ": ");
    RowEntry ent = objMatrix[y];
    while (ent != null) {
      System.out.print(" " + ent);
      ent = ent.next;
    }
    System.out.println();
  }

  public void plotCircle(FPoint2 origin, double radius, Object obj) {

    imgGraph.draw(new Arc2D.Double(origin.x - radius, origin.y - radius,
        2 * radius, 2 * radius, 0, 360, Arc2D.CHORD));
    plotDiscSamples(origin, radius, obj);

  }
  private void plotDiscSamples(FPoint2 origin, double radius, Object obj) {
    if (objMatrix != null) {

      int sRadius = (int) Math.round(radius / pixelSize);
      IPoint2 cell = calcCell(origin);
      int sx = cell.x, sy = cell.y;

      for (int pass = 0; pass < 2; pass++) {

        int f = 1 - sRadius;
        int ddF_x = 1;
        int ddF_y = -2 * sRadius;
        int x = 0;
        int y = sRadius;

        while (x < y) {
          if (f >= 0) {
            if (pass == 0) {
              horzLine(sx - x, sx + x - 1, sy + y - 1, obj);
              horzLine(sx - x, sx + x - 1, sy - y, obj);
            }
            // draw horizontal line at current y
            y--;
            ddF_y += 2;
            f += ddF_y;
          }

          x++;
          ddF_x += 2;
          f += ddF_x;

          if (pass == 1) {
            horzLine(sx - y, sx + y - 1, sy + x - 1, obj);
            horzLine(sx - y, sx + y - 1, sy - x, obj);
          }
        }

      }
    }
  }

  public void plotDisc(FPoint2 origin, double radius, Object obj) {

    imgGraph.fill(new Arc2D.Double(origin.x - radius, origin.y - radius,
        2 * radius, 2 * radius, 0, 360, Arc2D.CHORD));
    plotDiscSamples(origin, radius, obj);
  }

  /**
   * Set rendering hints
   * @param g : Graphics2D 
   * @param hints : array of 2n objects, keys and values
   * @param save : if true, saves and returns current values
   * @return if save, returns array suitable for restoring old values 
   */
  private static Object[] setRenderingHints(Graphics2D g, Object[] hints,
      boolean save) {
    Object[] ret = null;
    if (save) {
      ret = new Object[hints.length];
    }
    for (int i = 0; i < hints.length; i += 2) {
      Key k = (Key) hints[i];
      if (save) {
        ret[i] = k;
        ret[i + 1] = g.getRenderingHint(k);
      }
      g.setRenderingHint(k, hints[i + 1]);
    }
    return ret;
  }

  private static Object[] renderHints = { //
  RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED,
      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF,
      //  RenderingHints.KEY_ALPHA_INTERPOLATION,  RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED,
      RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_OFF,

  };

  public void render() {
    render(null, -1, -1);
  }

  /**
  * Set color for subsequent plotting.  Equivalent to
  *  bitmap.getGraphics().setColor(c)
  * @param c color
  */
  public void setColor(Color c) {
    imgGraph.setColor(c);
  }

  public void render(Color c, int stroke, int markType) {
    Graphics2D g = V.get2DGraphics();
    Object[] save = setRenderingHints(g, renderHints, true);

    AffineTransform tfm = AffineTransform
        .getScaleInstance(pixelSize, pixelSize);
    g.drawImage(img, tfm, null);
    setRenderingHints(g, save, false);
  }

  /**
   * Get graphics context
   * @return Graphics2D
   */
  public Graphics2D getGraphics() {
    return imgGraph;
  }

  //  public void renderDebug() {
  //    if (objMatrix == null)
  //      return;
  //
  //    FRect r = new FRect();
  //
  //    for (int i = 0; i < objMatrix.length; i++) {
  //      r.y = i * pixelSize;
  //      r.height = pixelSize;
  //      RowEntry ent = objMatrix[i];
  //      while (ent != null) {
  //        r.x = ent.x * pixelSize;
  //        r.width = ent.endX() * pixelSize - r.x;
  //        if (ent.object == null)
  //          continue;
  //        ent.object.plotDebug(r);
  //        ent = ent.next;
  //
  //      }
  //    }
  //
  //  }

  private static class RowEntry {
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("x:");
      sb.append(x);
      sb.append("..");
      sb.append((x + width) - 1);
      sb.append("<");
      sb.append(object.hashCode());
      sb.append(">");
      return sb.toString();
    }
    public int endX() {
      return x + width;
    }
//    public RowEntry() {
//    }
    public RowEntry(int x, int width, Object obj) {
      this.x = x;
      this.width = width;
      this.object = obj;
    }
    int x;
    int width;
    Object object;
    RowEntry next;
  }
  private RowEntry[] objMatrix;

  // private Object[] objMatrix;
  private BufferedImage img;
  private int width, height;
  private double pixelSize;
  private String description;
  private AffineTransform toGrid;
  private Graphics2D imgGraph;

}
