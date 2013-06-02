package testbed;

import javax.swing.JButton;
import javax.swing.Action;
import java.awt.GridBagConstraints;

/**
 * Button gadget
 */
class CtButton
    extends Gadget  {

  public int gcFill() {
     return GridBagConstraints.HORIZONTAL;
   }
   public boolean serialized() { return false; }

  public CtButton(int id, Action a) {
    this.dataType = DT_STRING;

    setId(id);
    b = new JButton(a);
    setComponent( b);
  }
  public void writeValue(Object v) {
    b.setText((String)v);
  }

  public Object readValue() {
    return b.getText();
  }
  private JButton b;
}


