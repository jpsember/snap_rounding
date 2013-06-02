package base;

import java.io.*;

/**
 * A PushbackReader that has a dynamic pushback buffer.
 */
class DynamicPushbackReader extends PushbackReader {
  public DynamicPushbackReader(Reader r) {
    super(r);
  }

  public int read(char[] charArray, int off, int len) throws IOException {
    int i;
    for (i = 0; i < len; i++) {
      int c = read();
      if (c < 0) {
        if (i == 0) {
          i = -1;
        }
        break;
      }
      charArray[off + i] = (char) c;
    }

    return i;
  }

  public void close() throws IOException {
    super.close();
    queue = null;
  }

  public int read() throws IOException {
    int out = -1;

    if (!queue.isEmpty()) {
      out = queue.popInt();
      //
      //    if (stack.length() > 0) {
      //      int newLen = stack.length()-1;
      //
      //      out = stack.charAt(newLen);
      //      stack.setLength(newLen);
    } else
      out = super.read();
    return out;
  }

  public void unread(char[] cbuff, int off, int len) {
    for (int i = off + len - 1; i >= off; i--)
      queue.push (cbuff[i], true);
    //      stack.append(cbuff[i]);
  }

  public void unread(char[] cbuff) {
    unread(cbuff, 0, cbuff.length);
  }

  public void unread(int c) {
    if (c >= 0)
      queue.push (c, true);
    //      stack.append((char)c);
  }

  public int peek(int offset) throws IOException {
    while (queue.size() <= offset) {
      int ch = super.read();
      if (ch < 0)
        return -1;
      queue.push (ch, false);
    }
    return ((Integer) queue.peek (offset, true)).intValue();
  }

  private DQueue queue = new DQueue();
  //  private StringBuilder stack = new StringBuilder();
}
