package testbed;

import base.*;
import java.io.*;

/**
 */
class AppletFileAccess implements IFileAccess {
  public boolean isApplet() {
    return true;
  }
  /**
   * Get input stream from stdin TextArea, if one exists; otherwise,
   * return null
   * @return InputStreamC
   */
  private InputStream workspaceInputStream(int index) {
    InputStream out = null;
    return out;
  }

  /**
   * Get applet file list.  Should be called within a simulated application
   * context to get the list of files available to the applet.
   * @return AppletFileList
   */
  public AppletFileList appletFileList() {
    return afList;
  }

  /**
   * Construct a Console
   * @param wsFiles : if not null, includes workspace windows, with
   *  this an array of filenames containing initial contents of these windows
   */
  public AppletFileAccess() {

    final boolean db = false;

    if (db) {
      System.out.println("Constructing console");
    }

    try {
      afList.processDirFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Determine which workspace, if any, is associated with a filename.
   * @param path String
   * @param defaultWS : index of workspace to use if path is null
   * @return Integer : null if no association, else 0..n-1
   */
  private Integer workspace(String path, int defaultWS) {
    Integer out = null;
    if (path == null) {
      path = Integer.toString(1 + defaultWS);
    }
    return out;
  }

  // ---------------------------------------------------------------
  // StreamFromPath interface
  // ---------------------------------------------------------------
  public InputStream getInputStream(String path, boolean alwaysInJAR)
      throws IOException {

    boolean db = false;
    if (db) {
      System.out.println("getInputStream for " + path);
    }
    InputStream ret = null;

    Integer wnum = null;
    // If no console exists yet, don't perform test.
    wnum = workspace(path, 0);
    if (db) {
      System.out.println(" wnum is " + wnum);
    }

    if (wnum != null) {
      ret = workspaceInputStream(wnum.intValue());
    } else {
      ret = appletFileList().getInputStream(path);
    }
    return ret;
  }

  /**
   */
  public IFileChooser getFileChooser() {
    return new AppletFileChooser();
  }

  public DArray getFileList(String dir, String extension) {
    DArray list = new DArray();
    SimFile[] f = appletFileList().getFiles();
    for (int i = 0; i < f.length; i++) {
      SimFile s = f[i];
      if (s.hidden()) {
        continue;
      }
      if (Path.getExtension(s.name()).equals(extension)) {
        list.add(s.name());
      }
    }
    return list;
  }

  public OutputStream getOutputStream(String path) throws IOException {
    OutputStream r = null;
    if (path == null) {
      r = Streams.out;
    } else {
      r = appletFileList().getOutputStream(path);
    }
    return r;
  }

  private AppletFileList afList = new AppletFileList();
}
