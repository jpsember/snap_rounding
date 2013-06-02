package testbed;

import java.awt.*;
import javax.swing.*;
import base.*;
import java.io.*;
  class WorkTextArea
    extends JTextArea {

  private int maxBufferSize;

  public WorkTextArea(int maxBufferSize) {
    this.maxBufferSize = maxBufferSize;
    setTextAreaStyle(this);
  }

  public int length() {
    return getDocument().getLength();
  }

  public void cls() {
    setText("");
  }

  public void ensureCursorVisible() {
    // Make sure the last line is always visible
    setCaretPosition(length());
  }

  public void trim() {
    if (maxBufferSize >= 0) {
      // Keep the text area down to a certain character size
      int maxExcess = 5000;
      int excess = length() - maxBufferSize;
      if (excess >= maxExcess) {
        replaceRange("", 0, excess);
      }
    }
  }

  /**
   * Set appearance for a JTextArea to our console style
   * @param t JTextArea
   */
  public static void setTextAreaStyle(JTextArea t) {
    t.setFont(monoFont());
    t.setBackground(new Color(230, 230, 230));
  }

  private static Font monoFont;
  static Font monoFont() {
    if (monoFont == null) {
      monoFont = new Font("Monospaced", Font.PLAIN, 16);
    }
    return monoFont;
  }

  /**
   * Get a non-closing PrintStream for this text area.
   * @return PrintStream
   */
  public PrintStream printStream() {
    if (ps == null)
      ps =  new NCPrintStream(new TextAreaOutputStream(this));
  return ps;
}
  private PrintStream ps;
}
