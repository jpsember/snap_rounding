package testbed;

import javax.swing.*;
import java.awt.event.*;

/**
 * Text field gadget
 */
class CtTextFieldNew extends Gadget implements ActionListener {
  public Object readValue() {
    if (readOnly())
      return jText.getText();
    else
      return jButton.getText();
  }
  public void writeValue(Object v) {
    if (readOnly())
      jText.setText(v.toString());
    else
      jButton.setText(v.toString());
  }

  /**
   * Construct a CtTextField for a string value
   * @param id  
   * @param label if "!", field is read only 
   * @param value 
   * @param maxStrLen  maximum # characters for value (for sizing
   *  purposes only; no edit length limit is enforced)
   * @param fixedWidth ignored
   */
  public CtTextFieldNew(int id, String label, String value, int maxStrLen,
      boolean fixedWidth) {
    setId(id);
    dataType = DT_STRING;

    boolean readOnly = (label != null && label.startsWith("!"));

    c = new JPanel();
    if (readOnly) {
      label = label.substring(1);
      if (label.length() > 0)
        jLabel = new JLabel(label);
      else
        label = null;
      jText = new JTextField(maxStrLen);
      jText.setEditable(false);
    } else {
      jButton = new JButton();
      jButton.addActionListener(this);
    }
    writeValue(value);
    c.setOpaque(true);
    if (label != null) {
      jLabel = new JLabel(label);
      c.add(jLabel);
    }
    if (readOnly)
      c.add(jText);
    else
      c.add(jButton);
    setComponent(c);
  }
  public void actionPerformed(ActionEvent e) {
    String lbl = null;
    if (jLabel != null)
      lbl = jLabel.getText();

    String sOrig = jButton.getText();
    String s = (String) JOptionPane.showInputDialog(null, null, lbl,
        JOptionPane.PLAIN_MESSAGE, null, null, sOrig);

    //If a string was returned, say so.
    if (s != null && !s.equals(sOrig)) {
      jButton.setText(s);
      TestBed.procAction(new TBAction(TBAction.CTRLVALUE, getId()));
    }
  }
  private boolean readOnly() {
    return jButton == null;
  }
  private JPanel c;
  private JTextField jText;
  private JButton jButton;
  private JLabel jLabel;
}
