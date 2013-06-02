package testbed;

import javax.swing.*;
import java.awt.*;
import base.*;
import javax.swing.border.*;

  class Gfx {
 // private static final boolean db = false;
  // border types
  public static final int
      // small padding, no line
      BD_PAD = 0
      // line + small padding
      , BD_LINE_PAD = 1
      ;

  /**
   * Add a border of a particular style to a component
   * @param c : JComponent to add border to
   * @param borderType : type of border, BD_x
   * @param label : if not null, string to display at top of border
   * @param labelAlignment : if label defined, this determines
   *   its horizontal alignment
   *   
   */
  public static void addBorder(JComponent c, int borderType, String label,
                               int labelAlignment) {
    final Border[] b = {
        // #0: empty, spacing of 2
        BorderFactory.createEmptyBorder(2, 2, 2, 2),
        // #1: lowered etched, interior spacing of 2
        BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)
            , BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ),
        // BorderFactory.createLineBorder(Color.red,3),
    };

    Tools.ASSERT(! (label != null && borderType < 0));

    if (label != null) {
      TitledBorder t = BorderFactory.createTitledBorder(
          b[borderType], label);

      int tbAlign = 0;
      switch (labelAlignment) {
        default:
          tbAlign = TitledBorder.LEFT;
          break;
        case SwingConstants.CENTER:
          tbAlign = TitledBorder.CENTER;
          break;
        case SwingConstants.RIGHT:
          tbAlign = TitledBorder.RIGHT;
          break;
      }
      t.setTitleJustification(tbAlign);
      c.setBorder(t);
    }
    else {
      if (borderType >= 0) {
        c.setBorder(b[borderType]);
      }
    }
  }


//  public static void init(Component c) {
//
//    if (db) {
//      Streams.out.println("gfx.init with component:\n" + Tools.d(c.toString()));
//    }
//
//    component = c;
//    tracker = new MediaTracker(component);
//
//    if (c instanceof Applet) {
//      Gfx.applet = (Applet) c;
//      if (db) Streams.out.println(" applet detected.");
//    }
//  }
//
//  private static void ensureInitialized() {
//    if (tracker == null) {
//      throw new IllegalStateException("gfx.init() wasn't called");
//    }
//  }
//
//  public static boolean isApplet() {
//    ensureInitialized();
//    return applet != null;
//  }
//
//  public static Image waitForImage(Image image) {
//    if (db) {
//      Streams.out.println("waitForImage " + image);
//    }
//    ensureInitialized();
//    boolean errFlag = false;
//    tracker.addImage(image, 0);
//    try {
//      tracker.waitForAll();
//    }
//    catch (InterruptedException e) {
//      errFlag = true;
//    }
//    errFlag |= tracker.isErrorAny();
//    if (errFlag) {
//      throw new RuntimeException("Error waiting for image");
//    }
//    return image;
//  }
//
//  /**
//   * Load an image that is a resource for a particular class
//   * @param owner Class
//   * @param name String
//   * @return Image
//   */
//  public static Image loadImage(Class owner, String name) {
//    name = Path.addExtension(name, "gif");
//    if (db) {
//      Streams.out.println("loadImage, owner=" + owner + ", name=" +
//                          Tools.d(name));
//    }
//    Image image = null;
//
//    if (owner == null) {
//      if (isApplet()) {
//        owner = applet.getClass();
//      }
//      else {
//        owner = component.getClass();
//      }
//    }
//
//    URL url = owner.getResource(name);
//    if (db) {
//      Streams.out.println(" url=" + url);
//    }
//    if (url != null) {
//      if (isApplet()) {
//        image = applet.getImage(url);
//      }
//      else {
//        image = Toolkit.getDefaultToolkit().getImage(url);
//      }
//    }
//
//    if (image == null) {
//      throw new RuntimeException("unable to load image: " + name);
//    }
//    return image;
//  }

public static void fillRect(Graphics g, Rectangle r) {
g.fillRect(r.x,r.y,r.width,r.height);
}

//public static void drawRect(Graphics g, Rectangle r) {
//  g.drawRect(r.x,r.y,r.width,r.height);
//}

  public static void showBounds(Graphics g) {
 g.setColor(Color.red);
 drawRect(g, g.getClipBounds());
  }

public static void drawRect(Graphics g, Rectangle r) {
  g.drawRect(r.x,r.y, r.width-1, r.height-1);
}

///**
// * Make a component have a fixed size.
// * @param c JComponent
// * @param size : the fixed size within any insets
// * @param insets : if not null, the insets containing the fixed size
// */
//public static void setFixedSize(JComponent c, Dimension size) {
//  Insets insets = c.getInsets();
////  if (insets != null)
//  {
//    size = new Dimension(size.width + insets.left + insets.right,
//                         size.height + insets.top + insets.bottom);
//  }
//
//  c.setPreferredSize(size);
//  c.setMinimumSize(size);
//  c.setMaximumSize(size);
//}
//
//
//public static Color textColor() {
//  return textColor;
//}
//public static Color bgndColor() {
//  return bgndColor;
//}
//public static Font textFont() {
//  return textFont;
//}
//public static Font textFontSmall() {
//  return textFontSmall;
//}
//public static Font textFontLarge() {
//  return textFontLarge;
//}
//
//public static Font getFont(int index) {
//  Font f = null;
//  switch (index) {
//    default:
//      f = textFont();break;
//      case 0:
//        f = textFontSmall(); break;
//        case 2:
//          f = textFontLarge(); break;
//  }
//  return f;
//
//}
//
//private static final Color bgndColor = new Color(20, 20, 120),
//    textColor = new Color(80, 80, 200);
//private static final Font textFont = new Font("Dialog", Font.PLAIN, 16);
//private static final Font textFontSmall = new Font("Dialog", Font.PLAIN, 12);
//private static final Font textFontLarge = new Font("Dialog", Font.PLAIN, 20);
//
//
//  private static Component component;
//  private static Applet applet;
//  private static MediaTracker tracker;
}
