package testbed;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import base.*;

  class Workspace
    implements ActionListener {

  private JPanel container;
  private static final String[] buts = {
      "Open",
      "Save",
  };
  private static final int
      CB_READFILE = 0
      , CB_WRITEFILE = 1
      ;

  public JTextArea textArea() {
    return textArea;
  }

  public JComponent component() {
    return container;
  }

  public PrintStream printStream() {
    // clear text area, since we are about to write to this workspace
    // also clear the title, since old contents are gone
    setTitle(null);
    textArea.cls();
    PrintStream ps = new NCPrintStream(new TextAreaOutputStream(textArea));
    return ps;
  }

  public Workspace(int index, JFrame frame) {
    this.frame = frame;
    id = index;
    container = new JPanel(new GridBagLayout());
    textArea = new WorkTextArea( -1);
    JScrollPane sp = new JScrollPane(textArea);

    if (index >= 0) {
      title = new JLabel("", JLabel.CENTER);
      setTitle(null);
      container.add(title, SwingTools.setGBC(0, 0, 1, 1, 0, 0));
    }
    container.add(sp, SwingTools.setGBC(0, 1, 1, 1, 100, 100));
    JPanel butPanel = new JPanel();

    //Lay out the buttons from left to right.
    butPanel.setLayout(new BoxLayout(butPanel, BoxLayout.LINE_AXIS));
    butPanel.add(Box.createHorizontalGlue());
    for (int j = 0; j < buts.length; j++) {
      String lbl = buts[j];
      JButton b = new JButton(lbl);
      b.addActionListener(this);
      b.setActionCommand("" + j);
      butPanel.add(b);
    }
    container.add(butPanel, SwingTools.setGBC(0, 2, 1, 1, 0, 0));
  }

public void fixTitle() {
  titleFixed = true;
}
private boolean titleFixed;

  public void setTitle(String s) {
if (titleFixed) return;

    StringBuilder sb = new StringBuilder();


    if (s == null) {
      s = "";
    }
    String lbl = "";
    if (id >= 0) {
      lbl = Integer.toString(1 + id);
      sb.append(lbl);
    }
    if (s.length() > 0) {
      if (sb.length() > 0)
        sb.append(": ");
      sb.append(s);
    }

    if (title != null)
      title.setText(sb.toString());
    if (frame != null)
      frame.setTitle(sb.toString());
  }

private JFrame frame;
  private int id;
  private JLabel title;
  private WorkTextArea textArea;
  private String lastReadFile;

  public void readFromFile(String path) {
    String s = null;
    try {
     s = Streams.readTextFile(path);
    } catch (IOException e) {
      s = "Unable to read file from path '"+TextScanner.debug(path)+"':\n"+
          Tools.stackTrace(0,30,e);
    }

    setTitle(path);
    textArea.setText(s);
    textArea.setCaretPosition(0);
  }

  /**
   * Process user selecting a console button
   * @param actionEvent ActionEvent
   */
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      switch (Integer.parseInt(actionEvent.getActionCommand())) {
        case CB_READFILE: {
          IFileChooser fc = Streams.fileChooser();
          String s = fc.doOpen("Open file:", lastReadFile, null);
          if (s == null) {
            break;
          }
          textArea.setText(Streams.readTextFile(s));
          lastReadFile = s;
          setTitle(s);
        }
        break;
        case CB_WRITEFILE: {
          IFileChooser fc = Streams.fileChooser();
          String s = fc.doWrite("Save to file:", lastReadFile, null);
          if (s == null) {
            break;
          }
          try {
            Writer wr = Streams.writer(s);
            wr.write(textArea.getText());
            wr.close();
          }
          catch (IOException e) {
            throw new RuntimeException(e);
          }
          lastReadFile = s;
          setTitle(s);
        }
        break;
      }
    }
    catch (Throwable e) {
      Streams.out.println(e.toString());
    }
  }
}
