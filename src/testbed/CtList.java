package testbed;

import base.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * List gadget
 */
//public 
class CtList
    extends Gadget implements ListSelectionListener {

  static final boolean db = false;

private myJList list;

  public void setRenderer(ListCellRenderer r) {
    clist().setCellRenderer(r);
  }

  private myJList clist() {
    return list;
  }

  /**
   * Get the value of the comboBox as an Integer containing
   * the id of the selected item
   * @return Object
   */
  public Object readValue() {
    return new Integer(selectedId());
  }

  /**
   * Set the value by changing the selected item
   * @param v : an Integer containing the id to select
   */
  public void writeValue(Object v) {
    int itemId = ( (Integer) v).intValue();
    int item = idToIndex(itemId);
    if (item < 0) {
      Tools.warn("writeValue to CtList, item is < 0");
      return;
    }

    Tools.ASSERT(item >= 0);
    if (db) {
      System.out.println("CtList writeValue id=" + itemId + " item=" +
                         item);
    }
    clist().setSelectedIndex(item);
  }

  /**
   * Get the id of the currently selected item
   * @return id
   */
  private int selectedId() {
    CtListItem j = (CtListItem) C.get(child0(clist().getSelectedIndex()));
    return j.getId();
  }

  // ------------------------------------------------------
  // ListSelectionListener interface
  // ------------------------------------------------------
  public void valueChanged(ListSelectionEvent e) {
    TestBed.procAction(
        new TBAction(TBAction.CTRLVALUE,
                     selectedId()));
  }

  /**
   * Constructor
   *
   * @param id : id of gadget
   * @param label : label for list, or null
   * @param toolTip : optional String
   * @param form : 0 for column, 1 for horz+wrap, 2 for vert+wrap
   * @param maxRows : number of rows to display
   */
  public CtList(int id, String label, String toolTip, int form, int maxRows) {
    dataType = DT_INT;
    setId(id);

    if (db) {
      System.out.println("CtList constructor, id=" + id + ", label=" +
                         label);
    }
    list = new myJList(form, maxRows);

    list.getSelectionModel().addListSelectionListener(this);

    JPanel cpanel = new JPanel(new GridBagLayout());

    GridBagConstraints gc = GC.gc(0, 0, 1, 1, 0, 0);
    if (label != null) {
      cpanel.add(new JLabel(label, SwingConstants.CENTER), gc);
    }
    int y = (label != null) ? 1 : 0;
    cpanel.add(list.scrollPane, GC.gc(0, y, 1, 1, 100, 100));
    if (toolTip != null) {
      cpanel.setToolTipText(toolTip);
    }
    setComponent(list.scrollPane);
  }

  public void addItem(int itemid, String field) {
    if (db) {
      System.out.println("CtList addItem id=" + itemid + " label=" + field);
    }
    CtListItem ci = new CtListItem(itemid);
    children.addInt(itemid);
    C.list.add( ci);
    clist().model.addElement(field);

    if (db) Streams.out.println("visRowCount is "+clist().getVisibleRowCount());
  }

  /**
   * Convert an item id to an item index
   * @param id : id of item
   * @return item index, or -1 if it's not in the box
   */
  public int idToIndex(int id) {
    for (int i = 0; i < nChildren0(); i++) {
      CtListItem j = (CtListItem) C.get(child0(i));
      if (j.getId() == id) {
        return i;
      }
    }
    return -1;
  }

//  // ------------------------------------------------------
//  // ActionListener interface
//  // ------------------------------------------------------
//  public void actionPerformed(ActionEvent e) {
//    Streams.out.println("CtList actionPerformed.... is this necessary?");
//    TestBed.app.appAction(
//        new TBAction(TBAction.CTRLVALUE,
//                     selectedId()));
//  }

  /**
   * Class to record info about list items
   */
  private static class CtListItem
      extends Gadget {
    public CtListItem(int id) {
     setId(id);
   }
   public boolean serialized() {
      return false;
   }
  }


  /**
   * Subclass of JList for CtList
   */
  private static class myJList
      extends JList {
//    /**
//     * Override key bindings for lists, so we ignore keyboard
//     * actions (up/down/etc.) that may be mapped to menu items
//     * instead.
//     *
//     * @param ks KeyStroke
//     * @param e KeyEvent
//     * @param condition int
//     * @param pressed boolean
//     * @return boolean
//     */
//    protected boolean processKeyBinding(javax.swing.KeyStroke ks,
//                                              java.awt.event.KeyEvent e,
//                                              int condition,
//                                              boolean pressed) {
//      return false;
//    }

    private static final int[] v = {
              JList.VERTICAL,
              JList.HORIZONTAL_WRAP,
              JList.VERTICAL_WRAP,
          };

    public myJList(int form, int maxRows) {

      setModel(model = new DefaultListModel());
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      if (CtList.db)
        Streams.out.println("setting visible row count to "+maxRows);

      setVisibleRowCount(maxRows);

      setLayoutOrientation(v[form]);
      scrollPane = new JScrollPane(
          this,
          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
          );
    }

    DefaultListModel model;
    JScrollPane scrollPane;
  }

}
