package testbed;

import java.awt.*;
import javax.swing.*;
import base.*;

/**
 * Panel that contains a stack of components.
 */
class StackPanel {
  static final boolean db = false;

  /**
   * Constructor
   * @param vertical true if vertical stack
   * @param title if not null, a title is placed around this panel
   */
  public StackPanel(String title) {
    component = new JPanel();
    if (title != null)
      Gfx.addBorder(component, Gfx.BD_LINE_PAD, title, SwingConstants.LEFT);

    // create a gridbaglayout to hold the different columns
    component.setLayout(new GridBagLayout());

    // the first row is divided up into arbitrary number of columns,
    // and the vertical weight is zero.

    // the second row spans the panel, and has all the vertical weight; it
    // serves to push the panel contents upward as much as possible
    //    if (true) 
    {
      if (GC.USEGLUE)
        GC.addGlue(component, 0,1);
      //           {
      //        GC gc = GC.gc(0, 1, GC.REMAINDER, 1, 1, 1);
      //        //        gc.fill = GC.BOTH;
      //        component.add(GC.glue(), /*new MyGluePanel(),*/ gc);
      //      }
    }
  }

  /**
   * Get the swing component this panel represents 
   * @return
   */
  public Component getComponent() {
    return component;
  }

  /**
   * Start a new column to the right of the previous one
   */
  public void startNewColumn() {
    currentColumn = null;
  }

  private void createColumn() {
    currentColumn = new JPanel();
    currentColumn.setLayout(new GridBagLayout());

    {
      GC gc = GC.gc(ccNum, 0, 1, 1, 1, 0);
      component.add(currentColumn, gc);
    }
    ccNum++;
    ccRow = 0;

    // try adding a component at the bottom to push others up

    //    if (true) 
    if (GC.USEGLUE)
      GC.addGlue(currentColumn,0,100);
    //       {
    //      GC gc = GC.gc(0, 100, GC.REMAINDER, GC.REMAINDER, 1, 1);
    //        currentColumn.add(GC.glue(), gc);
    //    }
    //
    //    if (false) {
    //      GC gc = GC.gc(0, 200, 1, 1, 100, 100);
    //      gc.fill = GC.BOTH;
    //      currentColumn.add(Box.createVerticalGlue(), //new MyGlue(),
    //          gc);
    //    }
  }

  /**
   * Add a component to the panel
   * @param c Component
   */
  public void addItem(Component c) {
    // if no column exists, add one
    if (currentColumn == null)
      createColumn();

    GC gc = GC.gc(0, ccRow, 1, 1, 0,0);
    currentColumn.add(c, gc);
    ccRow++;
  }

  //  private static class MyGlue extends JPanel {
  //    public MyGlue(String text) {
  //      super(new FlowLayout());
  //      this.setBackground(Color.red);
  //      JComponent c = new JButton(text);
  //      this.add(c);
  //    }
  //  }

//  static class MyGluePanel extends JPanel {
//    public MyGluePanel() {
//      this(true);
//    }
//
//    public MyGluePanel(boolean stretchy) {
//      this.stretchy = stretchy;
//    }
//
//    private boolean stretchy;
//
//    public void paintComponent(Graphics g) {
//      if (true) {
//        Rectangle r = g.getClipBounds();
//        g.setColor(stretchy ? Color.gray : Color.green);
//        Gfx.fillRect(g, r);
//        if (r.width > 4 && r.height > 4) {
//          g.setColor(Color.darkGray);
//          Gfx.drawRect(g, r);
//        }
//
//        if (stretchy) {
//          if (glueFont == null) {
//            glueFont = new Font("Monospaced", Font.PLAIN, 12);
//          }
//          g.setFont(glueFont);
//          FontMetrics metrics = g.getFontMetrics();
//          String s = "GLUE";
//          java.awt.geom.Rectangle2D r2 = metrics.getStringBounds(s, g);
//          if (r.width > r2.getWidth() && r.height > r2.getHeight()) {
//            g.drawString(s, r.x + r.width / 2 - (int) (r2.getWidth() / 2), r.y
//                + r.height / 2 + (int) (r2.getHeight() / 2)
//                + metrics.getLeading());
//          }
//        }
//        return;
//      }
//      super.paintComponent(g);
//    }
//
//    private static Font glueFont;
//  }

  //
  // component of current column
  private JPanel currentColumn;
  // outermost component of this panel
  private JPanel component;
  // current column number, 0..n-1
  private int ccNum;
  // current row number
  private int ccRow;
}
