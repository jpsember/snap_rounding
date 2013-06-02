package base;

import java.io.*;

/**
 * Non-closing PrintStream for use with OutputStreams that are derived
 * from System.out
 */
public class NCPrintStream
    extends PrintStream {
  public NCPrintStream(OutputStream s) {
    super(s);
  }

  public NCPrintStream(OutputStream s, boolean autoFlush) {
    super(s, autoFlush);
  }

  public void close() {
    flush();
  }
}
