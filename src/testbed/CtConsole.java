package testbed;

import base.*;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionListener;

class CtConsole extends Gadget implements ActionListener {

  public void processZAction() {
    TestBed.procAction(new TBAction(TBAction.COMMAND, inputStr()));
  }

  public int gcFill() {
    return GridBagConstraints.BOTH;
  }

  public String toString() {
    return "CtConsole";
  }

  public void cls() {
    //Tools.warn("cls() disabled\n");
  }

  public void actionPerformed(ActionEvent e) {

    // insert edit text to console's text area
    String s = inputTextField.getText();
    inputTextField.setText("");

    Streams.out.println(s);
    inputStr = s;

    processZAction();
  }

  String inputStr() {
    return inputStr;
  }

  public CtConsole(int id, int nRows, int nCols, boolean withInputLine) {
    setId(id);
    Font f = TBFont.fixedWidthFont();

    textArea2 = new WorkTextArea(50000);
    if (false) {
      textArea2.setLineWrap(true);
      textArea2.setWrapStyleWord(true);
    }

    textArea2.setRows(nRows);
    textArea2.setColumns(nCols);
    textArea2.setEditable(false);
    scrollPane = new JScrollPane(textArea2,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    //  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );

    JPanel panel = new JPanel(new GridBagLayout());
    GC gc = GC.gc(0, 0, 1, 1, 100, 100);
    panel.add(scrollPane, gc);
    if (withInputLine) {
      inputTextField = new JTextField(nCols);
      inputTextField.setFont(f);
      gc = GC.gc(0, 1, 1, 1, 100, 0);
      panel.add(inputTextField, gc);
      inputTextField.addActionListener(this);
    }
    setComponent(panel);
  }

  public void redirectSystemOutput() {
    // construct a non-closing writer to replace Streams.out.
    C.setConsole(textArea2);
  }

  // the single-line input text field
  private JTextField inputTextField;
  // the scroll pane that contains the text area
  private JScrollPane scrollPane;
  // the large text area
  private WorkTextArea textArea2;
  // last line of text entered by user
  private String inputStr;
}
