package testbed;

import javax.swing.JTextArea;
import javax.swing.*;
import java.awt.GridBagConstraints;

class CtTextArea extends Gadget {
  public boolean serialized() {
    return false;
  }

  public int gcFill() {
    return GridBagConstraints.BOTH;
  }

  public void writeValue(Object v) {
    textArea.setText((String) v);
  }

  public Object readValue() {
    return textArea.getText();
  }

  /**
   * Constructor
   * @param id : id of gadget
   * @param title : String; if not empty, string to display in a border around the gadget
   * @param titleAlignment int how title is to be justified
   * @param value String initial contents of text area
   * @param rows int number of rows, or 0 to use available space
   * @param columns int number of columns, or 0 to use available space
   * @param withScroll boolean true if it should be embedded in a scroll pane
   * @param fixedWidth boolean true to use fixed-width font
   */
  public CtTextArea(int id, String title, int titleAlignment, String value,
      int rows, int columns, boolean fixedWidth) {
    setId(id);

    JTextArea cj = new JTextArea(value);

    cj.setEditable(false);

    cj.setWrapStyleWord(true);
    cj.setLineWrap(true);
    if (fixedWidth) {
      cj.setFont(TBFont.fixedWidthFont());
    }

    //    if (rows != 0 && columns != 0)
    //      cj.setSize(new java.awt.Dimension(columns*10,rows*14));
    //    if (false)
    {
      if (rows > 0) {
        cj.setRows(rows);
      }
      if (columns > 0) {
        cj.setColumns(columns);
      }
    }
    textArea = cj;

    JComponent c = null;
    {
      JScrollPane scrollPane = new JScrollPane(cj,
          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      c = scrollPane;
      //      if (title.length() != 0) {
      //      Gfx.addBorder(c, BD_LINE_PAD, title, titleAlignment);
      //    }
    }
    if (title.length() != 0) {
      Gfx.addBorder(c, Gfx.BD_LINE_PAD, title, titleAlignment);
    }
    setComponent(c);
  }

  private JTextArea textArea;
  //  static {
  //    Tools.warn ("make special gadget for text area, edit in popup dialog");
  //  }
  //  

}
