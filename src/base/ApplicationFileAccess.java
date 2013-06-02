package base;

import java.io.*;

public class ApplicationFileAccess implements IFileAccess {
  public boolean isApplet() {
    return false;
  }

  public IFileChooser getFileChooser() {
    throw new UnsupportedOperationException();
//    return new ApplicationFileChooser();
  }

  public DArray getFileList(String dir, String extension) {
    throw new UnsupportedOperationException();
  }

  public InputStream getInputStream(String path, boolean alwaysInJAR)
      throws IOException {
    InputStream ret;
    if (path == null) {
      if (in == null)
        in = new NonClosingSystemIn();
      ret = in;
    } else if (alwaysInJAR)
      ret = Streams.openResource(path);
    else
      ret = new BufferedInputStream(new FileInputStream(path));
    return ret;
  }

  public OutputStream getOutputStream(String path) throws IOException {
    
    OutputStream r = null;
    // if path undefined, use system.out
    if (path == null) {
      r = Streams.out;
    } else {
      OutputStream os  = new FileOutputStream(path);
      r = new BufferedOutputStream(os);
    }
    return r;
  }

  private static class NonClosingSystemIn extends BufferedInputStream {
    public NonClosingSystemIn() {
      super(System.in);
    }

    public void close() {
    }
  }

  // InputStream to use instead of System.in
  private static InputStream in;
}
