package testbed;

import java.awt.*;
import javax.swing.*;
import base.*;

class GC extends GridBagConstraints {
  private static GC gc = new GC();

  /**
   * We add empty 'glue' panels to force components upward when
   * there is free space vertically.
   */
  public static final boolean USEGLUE = true;

  public static GC gc(int gridX, int gridY, int gridWidth, int gridHeight,
      int weightX, int weightY) {
    gc.gridx = gridX;
    gc.gridy = gridY;
    gc.gridwidth = gridWidth;
    gc.gridheight = gridHeight;
    gc.weightx = weightX;
    gc.weighty = weightY;
    gc.anchor = GridBagConstraints.CENTER;
    gc.fill = GridBagConstraints.BOTH;
    return gc;
  }

  /**
   * Get a 'glue' component that when added as the last row of
   * a GridBagLayout, compresses the other items upward
   * 
   * @return component
   */
  private static Component glue() {
    return new MyGluePanel();
  }

  private static class MyGluePanel extends JPanel {
    public MyGluePanel() {
      if (false) {
        setBackground(MyColor.cDARKGREEN);
        Tools.warn("using debug color");
      }
      this.setPreferredSize(new Dimension(1, 1));
      this.setMinimumSize(new Dimension(1, 1));
    }
  }

  /**
   * Add a 'glue' component to the last row or column of a GridBagLayout
   * to compress other items as much as possible
   * @param Container container to add glue to; must use a GridBagLayout
   * @param gridX 
   * @param gridY grid positions for start of glue
   */

  public static void addGlue(Container component, int gridX, int gridY) {
    GC gc = gc(gridX, gridY, GC.REMAINDER, GC.REMAINDER, 1, 1);
    component.add(glue(), gc);
  }
}
