package testbed;

import base.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * ComboBox gadget.
 * 
 * Appears as either a JComboBox or a set of JRadioButtons.
 */
class CtComboBox extends Gadget implements ActionListener {

  private static boolean db = false;

  /**
   * Constructor
   * 
   * @param id : id of gadget.  The value of this gadget will be
   *  the id of the selected item
   * @param label : label for gadget (if not null, encloses gadget in
   *   frame with label)
   * @param asRadio : if true, appears as a radio button group
   */
  public CtComboBox(int id, String label, String toolTip, boolean asRadio) {
    setId(id);
    dataType = DT_INT;

    if (db) {
      System.out.println("CtComboBox constructor, id=" + id + " label=" + label
          + " asRadio=" + asRadio);
    }

    if (asRadio) {
      myRadioSet mbox = new myRadioSet(this, toolTip);
      cbox = mbox;
      mbox.addActionListener(this);

      setComponent(cbox.getComponent());
    } else {
      myComboBox mbox = new myComboBox(this, toolTip);
      cbox = mbox;
      mbox.addActionListener(this);

      JPanel cpanel = new JPanel(new GridBagLayout());
      GridBagConstraints gc = GC.gc(0, 0, 1, 1, 0, 0);
      if (label != null) {
        gc.insets.right = 5;
        cpanel.add(new JLabel(label, SwingConstants.RIGHT), gc);
      }
      int x = (label != null) ? 1 : 0;
      cpanel.add(mbox.getComponent(), GC.gc(x, 0, 1, 1, 0, 0));
      setComponent(cpanel);
    }
  }

  /**
   * Set renderer (for drop down box only)
   */
  public void setRenderer(ListCellRenderer r) {
    cbox.setRenderer(r);
  }

  /**
   * Add an item to the combo box
   * @param itemid
   * @param field
   */
  public void addItem(int itemid, String field) {
    if (db) {
      System.out
          .println("CtComboBox addField id=" + itemid + " label=" + field);
    }
    CtComboBoxItem ci = new CtComboBoxItem(itemid);
    ci.writeValue(field);
    children.addInt(itemid);
    C.list.add(ci);

    if (db)
      Streams.out.println("added child " + ci + ", id=" + itemid + " to "
          + this + ", nchildren now " + this.nChildren0());
    cbox.addItem(ci);
  }

  // ------------------------------------------------------
  // ActionListener interface
  // ------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    int selId = cbox.getSelectedId();
    if (db)
      Streams.out.println("CtComboBox actionPerformed: selected=" + selId);
    TestBed.procAction(new TBAction(TBAction.CTRLVALUE, selId));
  }

  /**
   * Get the value of the comboBox as an Integer containing
   * the id of the selected item, or -1 if no items are selected
   * @return Object
   */
  public Object readValue() {
    Object ret = new Integer(cbox.getSelectedId());
    if (db)
      Streams.out.println("CtComboBox " + getId() + " readValue, returning "
          + ret);
    return ret;
  }

  /**
   * Set the value by changing the selected item
   * @param v : an Integer containing the id to select
   */
  public void writeValue(Object v) {
    int itemId = ((Integer) v).intValue();
    if (db)
      Streams.out.println("CtComboBox " + getId() + " writeValue " + v
          + ", setting selected id " + itemId);
    cbox.setSelectedId(itemId);
  }

  private IComboBox cbox;

  /**
   * Child items
   */
  private static class CtComboBoxItem extends Gadget {
    public boolean serialized() {
      return false;
    }

    /**
    * Get string describing object
    * @return String
    */
    public String toString() {
      return readValue().toString();
    }

    public CtComboBoxItem(int id) {
      setId(id);
      dataType = DT_STRING;
    }

    private AbstractButton button;

    //
    public void setButton(AbstractButton b) {
      this.button = b;
    }
  }

  private abstract static class IComboBox {

    public IComboBox(CtComboBox parent) {
      this.parent = parent;
    }

    protected CtComboBox parent;

    public void setRenderer(ListCellRenderer r) {
      throw new UnsupportedOperationException();
    }

    public abstract void addItem(CtComboBoxItem item);

    public abstract Component getComponent();

    public int idToIndex(int id) {
      int ret = -1;

      for (int i = 0; i < parent.nChildren0(); i++) {
        if (parent.child0(i) == id) {
          ret = i;
          break;
        }
      }
      return ret;
    }

//    public int indexToId(int index) {
//      int ret = -1;
//      if (index >= 0 && index < parent.nChildren0())
//        ret = parent.child0(index);
//      return ret;
//    }

    public abstract void setSelectedId(int id);

    public abstract int getSelectedId();
  }

  private static class myComboBox extends IComboBox {
    public myComboBox(CtComboBox parent, String toolTip) {
      super(parent);
      c = new JComboBox();
      if (toolTip != null)
        c.setToolTipText(toolTip);
    }

    private JComboBox c;

    public void addActionListener(ActionListener listener) {
      c.addActionListener(listener);
    }

    public void addItem(CtComboBoxItem item) {
      c.addItem(item);
    }

    public Component getComponent() {
      return c;
    }

    public int getSelectedId() {
      int ret = -1;
      CtComboBoxItem item = (CtComboBoxItem) c.getSelectedItem();
      if (item != null)
        ret = item.getId();
      return ret;
    }

    public void setRenderer(ListCellRenderer r) {
      c.setRenderer(r);
    }

    public void setSelectedId(int id) {
      if (db)
        Streams.out.println("setSelectedId=" + id + ", idToIndex="
            + idToIndex(id));

      c.setSelectedIndex(idToIndex(id));
    }
  }

  private static class myRadioSet extends IComboBox {

    private CtComboBox cbox;

    public myRadioSet(CtComboBox cbox, String toolTip) {
      super(cbox);
      this.cbox = cbox;
      this.bgroup = new ButtonGroup();

      this.selectedId = -1;
      this.toolTip = toolTip;
      cpanel = new JPanel(new GridBagLayout());

    }

    private String toolTip;

    private ActionListener listener;

    public void addActionListener(ActionListener listener) {
      this.listener = listener;
    }

    public Component getComponent() {
      return cpanel;
    }

    private JPanel cpanel;

    public void addItem(CtComboBoxItem item) {
      MyRadioButton b = new MyRadioButton(this, item);
      item.setButton(b);
      if (toolTip != null)
        b.setToolTipText(toolTip);

      bgroup.add(b);
      GridBagConstraints gc = GC.gc(0, parent.nChildren0() - 1, 1, 1, 0, 0);
      cpanel.add(b, gc);
    }

    void processSelection(int id) {
      if (db)
        Streams.out.println("myRadioSet.processSelection=" + id);
      if (selectedId != id)
        setSelectedId(id);
      //      if (listener != null)
      //        listener.actionPerformed(new ActionEvent(cbox, cbox.getId(),
      //            "radio button selected"));
    }

    public int getSelectedId() {
      return selectedId;
    }

    public void setSelectedId(int id) {
      if (db)
        Streams.out.println("myRadioSet.setSelectedId=" + id);

      CtComboBoxItem item = (CtComboBoxItem) C.get(id);

      if (item != null && item.button != null) {
        if (db)
          Streams.out.println(" calling setb with " + item.getId());
        item.button.setSelected(true);
      }
      this.selectedId = id;
      if (listener != null)
        listener.actionPerformed(new ActionEvent(cbox, cbox.getId(),
            "radio button selected"));

    }

    private ButtonGroup bgroup;

    private int selectedId;

  }

  private static class MyRadioButton extends JRadioButton implements
      ActionListener {
    private myRadioSet radioSet;

    private int id;

    public MyRadioButton(myRadioSet rs, CtComboBoxItem item) {
      super(item.toString());
      this.radioSet = rs;
      this.id = item.getId();
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      radioSet.processSelection(id);
    }
  }

}
