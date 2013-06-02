package testbed;

import java.io.*;
import java.util.*;
import base.*;

/**
 * SimFile collection for applets
 * 
 * Format of appletdir.txt:
 * 
 * #comment
 * (-|!)*<file>
 *  - : hides file ! : file is a program (implies hidden)
 */
class AppletFileList {

  public Iterator iterator() {
    return list.iterator();
  }

  public SimFile[] getFiles() {
    DArray a = new DArray();
    Iterator it = iterator();
    while (it.hasNext()) {
      SimFile s = (SimFile) it.next();
      if (!s.hidden()) {
        a.add(s);
      }
    }
    return (SimFile[]) a.toArray(SimFile.class);

  }

  /**
   * Get a file chooser
   * 
   * @return FileChooser
   */
  public static IFileChooser getFileChooser() {
    return null;
  }

  /**
   * Construct an output stream to a SimFile with a particular name
   * 
   * @param name :
   *          name of file
   * @return OutputStream
   */
  public OutputStream getOutputStream(String name) throws IOException {
    // does file already exist in file list?
    SimFile sf = find(name);
    if (sf == null) {
      sf = new SimFile(name, false, SimFile.MEMORY);
      addFile(sf);
    }
    return sf.getOutputStream();
  }

  private void addFile(SimFile sf) {
    // System.out.println("AppletFileList: adding file "+sf.debug());
    list.add(sf);
  }

  /**
   * Construct an output stream to a SimFile with a particular name
   * 
   * @param name :
   *          name of file
   * @return OutputStream
   */
  public InputStream getInputStream(String name) throws IOException {
    boolean db = false;
    if (db) {
      System.out.println("getInputStream for " + name);
    }

    // does file already exist in file list?
    SimFile sf = find(name);
    if (sf == null) {
      throw new FileNotFoundException("File not found: " + name);
    }
    return sf.getInputStream();
  }

  public void displayDir() {
    final boolean db = false;

    Iterator it = iterator();
    while (it.hasNext()) {
      SimFile sf = (SimFile) it.next();

      if (db) {
        Streams.out.println(sf.debug());
      } else {
        if (sf.hidden()) {
          continue;
        }
        Streams.out.println(sf.name());
      }
    }
  }

  /**
   * Read and process 'appletdir.txt' from the JAR file. Processes each entry as
   * the name of a file that is assumed to be in the JAR. If the name starts
   * with '-', its hidden flag will be set. If it starts with '!', it will be
   * loaded into the stdin window (if one exists).
   */
  void processDirFile() throws IOException {
    final boolean db = false;

    if (db) {
      Streams.out.println("processDirFile");
    }

    InputStream is = null;
    try {
      is = Streams.openResource(TestBed.getAppContainer().getClass(),
          "appletdir.txt");
    } catch (FileNotFoundException e) {
      Streams.out.println("appletdir.txt not found");
    }

    if (is != null) {
      Reader r = null;
      try {
        r = new InputStreamReader(is);
        TextScanner sc = new TextScanner(r);
        while (true) {

          String n = sc.readLine();
          if (n == null) {
            break;
          }
          if (db)
            Streams.out.println(" read line " + n);

          boolean hidden = false;
          boolean program = false;
          String filename = null;

          TextScanner s = new TextScanner(n);
          if (db) {
            Streams.out.println(TextScanner.chomp(n));
          }
          if (s.peekChar() == '#') {
            continue;
          }
          while (true) {
            if (s.readWS()) {
              break;
            }

            char c = (char) s.peekChar();
            if (c == '-') {
              hidden = true;
              s.readChar();
              continue;
            }
            if (c == '!') {
              hidden = true;
              program = true;
              s.readChar();
              continue;
            }
            filename = s.readWordOrStr(true);
            if (db) {
              Streams.out.println(" filename=" + filename + " hidden=" + hidden
                  + " program=" + program);
            }
            break;
          }

          if (filename == null) {
            continue;
          }

          if (!program) {
            addFile(new SimFile(filename, hidden, SimFile.JAR));
            continue;
          }

          addFile(new SimFile(filename, true, SimFile.PROGRAM));
        }
      } finally {
        r.close();
      }
    }
  }

  public SimFile find(String name) {
    return find(name, false);
  }

  /**
   * Find file in list
   * 
   * @param name :
   *          name of file
   * @return SimFile, or null if not found
   */
  public SimFile find(String name, boolean ignoreCase) {

    final boolean db = false;

    SimFile out = null;
    SortedSet s = list.tailSet(new SimFile(name, false, SimFile.DISK));

    if (!s.isEmpty()) {

      SimFile e = (SimFile) s.first();
      if (db)
        Tools.showDialog("first file=" + Tools.d(e.name()) + "\nname="
            + Tools.d(name));

      if (ignoreCase) {
        if (e.name().equalsIgnoreCase(name)) {
          out = e;
        }
      } else {
        if (e.name().equals(name)) {
          out = e;
        }
      }
    }
    if (db)
      Tools.showDialog("returning " + out);
    return out;
  }

  public Collection collection() {
    return list;
  }

  private TreeSet list = new TreeSet(SimFile.comparator());

}
