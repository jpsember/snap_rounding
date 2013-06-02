package testbed;

import javax.swing.*;

class CtCheckBox extends Gadget {

  /**
   * Constructor
   * @param gl GadgetList
   * @param id int
   * @param label String
   * @param value boolean
   * @param inMenu boolean
   * @param toolTip String
   * @param accel KeyStroke
   */
  public CtCheckBox(int id, String label, boolean value, boolean inMenu,
      String toolTip, KeyStroke accel) {
    setId(id);
    this.dataType = DT_BOOL;
    Action a = Gadget.createAction(id, label, toolTip, accel);

    if (inMenu) {
      setComponent(new JCheckBoxMenuItem());
    } else {
      setComponent(new JCheckBox());
    }
    button().setSelected(value);
    button().setAction(a);
  }

  public void writeValue(Object v) {
    button().setSelected(((Boolean) v).booleanValue());
  }

  public Object readValue() {
    return new Boolean(button().isSelected());
  }

  private AbstractButton button() {
    return (AbstractButton) getComponent();
  }

  //  private static class myCheckBoxMenuItem
  //    extends JCheckBoxMenuItem {
  //
  //  public myCheckBoxMenuItem(Action action, boolean value) {
  //    super(action);
  //    setSelected(value);
  //  }
  //
  ////  private int id;
  //// public Component getComponent() {return this;}
  //// public int getId() {return id;}
  //// public void setId(int id) {this.id = id;}
  //
  //}
  //
  //private static class myCheckBox
  //    extends JCheckBox {
  //
  ////  public void writeValue(Object v) {
  ////    setSelected( ( (Boolean) v).booleanValue());
  ////  }
  ////
  ////  public Object readValue() {
  ////    return new Boolean(isSelected());
  ////  }
  ////
  //  public myCheckBox( Action action, boolean value) {
  //    super(action);
  //    setSelected(value);
  //  }
  //
  ////  private int id;
  //// public Component getComponent() {return this;}
  //// public int getId() {return id;}
  //// public void setId(int id) {this.id = id;}
  //}
}
