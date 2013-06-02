package testbed;

import java.io.*;

/**
 * OutputStream for printing to a TextArea.
 */
class TextAreaOutputStream
    extends OutputStream {

  /**
   * Create a new stream that will write buffered output to a TextArea.
   */
  TextAreaOutputStream(WorkTextArea textArea) {
    this.textArea = textArea;
    reset();
  }

  private void reset() {
    buffer.setLength(0);
  }

  public void write(int b) throws IOException {
    char charCast = (char) b;
    if (b == 13) {
      return;
    }

    buffer.append(charCast);

    // if charCast is a CR, or buffer has lots of characters, flush.
    if (charCast == '\n' || buffer.length() > 300) {
      flush();
    }
  }

  public void flush() {
    // add the current buffer to the textArea
    addStringToTextArea(buffer.toString());
    reset();
  }

  private final void addStringToTextArea(String string) {
    textArea.append(string);
    textArea.trim();
    textArea.ensureCursorVisible();
//    ensureCursorVisible();
  }

  private WorkTextArea textArea;
  private StringBuilder buffer = new StringBuilder();
}
