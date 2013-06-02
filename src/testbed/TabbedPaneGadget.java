package testbed;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import base.*;

class TabbedPaneGadget extends Gadget {
  static final boolean db = false;

  /**
   * Read value.
   * Returns the current pane's identifier.
   * @return Object integer
   */
  public Object readValue() {

    int val = -1;

    JTabbedPane tp = getSet();
    int si = tp.getSelectedIndex();

    // get id of this pane
    int userId = ids.getInt(si);
    val = userId;
    //    val = ((userId & 0xffff) << 16) | si;
    if (db)
      Streams.out.println("readValue for selected pane=" + val);
    return new Integer(val);
  }

  /**
   * Write value: set selected tabbed pane
   * @param v integer; if >= ID_BASE, assumes it's an id of an item; otherwise,
   * assumes it's an index
   */
  public void writeValue(Object v) {
    if (db)
      Streams.out.println("StackPanel writeValue " + v);

    JTabbedPane tp = getSet();

    int val = ((Integer) v).intValue();
    Integer val2 = null;

    if (val >= TAB_ID_START) {
      val2 = (Integer) idToIndexMap.get(new Integer(val));
      if (val2 == null)
        Tools.warn("no value " + val2 + " for " + this.getId());
    } else {
      val2 = new Integer(val);
    }
    if (val2 != null) {
      if (val2.intValue() < 0 || val2.intValue() >= tp.getTabCount()) {
        Tools.warn("no such tab: " + val2 + " for " + this.getId());
      } else
        tp.setSelectedIndex(val2.intValue());
    }
  }

  /**
   * Constructor
   * @param vertical true if vertical stack
   * @param tabbed true if a tab panel
   * @param destContainer   if not null, existing component that will
   *  contain this stack's elements; it is assumed to use GridBagLayout
   */
  public TabbedPaneGadget(boolean vertical, int id) {
//    if (true) {
//      Tools.warn("vert");
//      vertical = false;
//    }
    
    this.dataType = DT_INT;
    this.setId(id);
    JTabbedPane tabPane = new JTabbedPane(vertical ? JTabbedPane.TOP
        : JTabbedPane.LEFT);
    tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
  //  Tools.warn("tab layout="+tabPane.getTabLayoutPolicy());
    tabPane.addChangeListener(this);
    setComponent(tabPane);
  }

  private JTabbedPane getSet() {
    return (JTabbedPane) this.getComponent();
  }

  /**
   * Add a tab to the panel
   * @param title title of tab
   * @param pnlId identifier to assign this tab; if < ID_BASE, uses the 
   *   tab index as its id
   * @param component
   */
  public void addTab(String title, int pnlId, Component component) {
    if (db)
      Streams.out.println("addTab, title=" + title + ", pnlId=" + pnlId);

    // Determine id of pane.  If it is < 1000, assume it's just an index
    if (pnlId < TAB_ID_START) {
      pnlId = ids.size();
    }

    titles.add(title);
    idToIndexMap.put(new Integer(pnlId), new Integer(ids.size()));
    ids.addInt(pnlId);

    getSet().add(title, component);
  }

  // ids of panels
  // maps user ids => pane index
  private Map idToIndexMap = new HashMap();

  // titles of items
  private DArray titles = new DArray();
  // ids of items
  private DArray ids = new DArray();
}
