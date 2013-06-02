package testbed;

import base.*;
import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

/**
 * Base class for control gadgets
 */
abstract class Gadget implements Globals, ChangeListener,
    PropertyChangeListener {

  public void stateChanged(ChangeEvent changeEvent) {
    TestBed.procAction(new TBAction(TBAction.CTRLVALUE, getId()));
  }

  // ------------------------------------------------------
  // PropertyChangeListener interface
  // ------------------------------------------------------
  public void propertyChange(PropertyChangeEvent e) {
    TestBed.procAction(new TBAction(TBAction.CTRLVALUE, getId()));
  }

  public static final int DT_STRING = 0, DT_INT = 1, DT_DOUBLE = 2,
      DT_BOOL = 3;

  /**
   * Determine if data type for gadget matches a value
   * @param t : value to match
   * @return boolean
   */
  public boolean dataType(int t) {
    return dataType == t;
  }

  /**
   * Get number of children of this gadget
   * @return int
   */
  public int nChildren0() {
    return children.size();
  }

  /**
   * Find index of a child
   */
  /**
   * Get a child
   * @param i : index of child
   * @return id of child
   */
  public int child0(int i) {
    return children.getInt(i);
  }

  /**
   * Create an action for the gadget
   * @param id : id of gadget
   * @param label : label for gadget
   * @param l : who will be listening for these actions
   * @param toolTip : if not null, tooltip text to display with gadget
   * @param accel : if not null, KeyStroke to trigger action
   * @return Action
   */
  public static Action createAction(int id, String label, String toolTip,
      KeyStroke accel) {

    final boolean db = false;

    // create an action for the visible control
    Action action = new GadgetAction(id, label, toolTip, accel);

    //    // if accelerator defined, associate it with the
    //    // application/applet frame.
    //
    //    if (accel != null) {
    //      GadgetAction osAction = new GadgetAction(id, label,
    //                                  l, toolTip, accel);
    //      osAction.setAssociated(action);
    //
    //      JComponent f = TestBed.topLevelContainer();
    //
    //      InputMap imap = f.getInputMap(JComponent.
    //                                    WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    //      String key = "" + id;
    //      if (db) {
    //        System.out.println(" adding to imap: " + accel);
    //      }
    //      imap.put(accel, key);
    //      ActionMap amap = f.getActionMap();
    //      if (db) {
    //        System.out.println(" adding to amap: " + key);
    //      }
    //      amap.put(key, osAction);
    //    }

    if (db) {
      System.out.println("created action for gadget " + id);
    }
    return action;
  }

  /**
   * Get the ID assigned to this component
   * @return int
   */
  public int getId() {
    return this.id;
  }

  /**
   * Get the ID assigned to this component
   * @return int
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Get the value stored in this component
   * @return Object : a String, Double, Integer, etc.
   */
  public Object readValue() {
    return value;
  }

  /**
   * Store a new value to this component
   * @param v : a String, Double, Integer, etc.
   */
  public void writeValue(Object v) {
    this.value = v;
  }

  /**
   * Get Swing component associated with gadget
   * @return Component
   */
  public Component getComponent() {
    return this.component;
  }

  public void setComponent(Component c) {
    this.component = c;
  }

//  /**
//   * Set renderer for gadget.
//   * Override to support lists and whatnot.
//   * @param r ListCellRenderer
//   */
//  public void setRenderer(ListCellRenderer r) {
//    //    Tools.ASSERT(false, "setRenderer not supported by " + this);
//  }

  /**
   * Get GridBagConstraints fill parameter for this gadget
   * @return int
   */
  public int gcFill() {
    return GridBagConstraints.HORIZONTAL;
  }

  /**
   * Determine if this gadget has a value that needs to be serialized.
   * The default implementation returns true if gadget has a value defined.
   *
   * @return boolean
   */
  public boolean serialized() {
    Object v = readValue();
    return v != null;
  }

  private static class GadgetAction extends MyAction {

    private static final String GADGETID = "GADGETID";

    /**
     * Constructor
     * @param id : id of gadget associated with action
     * @param name : name of action (i.e., menu item label)
     * @param toolTip : tooltip message
     * @param accel : keystroke to use as accelerator
     */
    public GadgetAction(int id, String name, String toolTip, KeyStroke accel) {

      super(name);
      putValue(GADGETID, new Integer(id));
      setAccelerator(accel);
      if (toolTip != null) {
        setTooltipText(toolTip, true);
      }
    }

    public int id() {
      return ((Integer) getValue(GADGETID)).intValue();
    }

    public String toString() {
      return "GadgetAction id=" + id() + "\n" + super.toString();
    }

    public void actionPerformed(ActionEvent e) {
      // send application a CTRLVALUE action with the gadget id.
      TestBed.procAction(new TBAction(TBAction.CTRLVALUE, id()));
    }

  }

  private Component component;
  private int id;
  private Object value;

  // type of data (DT_xxx)
  protected int dataType;
  // list of child controls
  protected DArray children = new DArray();

}
