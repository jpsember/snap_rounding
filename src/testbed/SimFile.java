package testbed;

import java.util.*;
import java.io.*;
import base.*;

/**
 * Class for applet-access to files.
 * They can be either
 *   [] read-only files within a JAR file (applet or application)
 *   [] read/write files on disk (non-applet)
 *   [] read/write files in memory (applet)
 */
  class SimFile {

  public static Comparator comparator() {
    return new ourComparator();
  }

  private ByteArrayOutputStream os;
  private int readCount;

  InputStream getInputStream() throws IOException {
    boolean db = false;
    if (db) System.out.println("SimFile:getInputStream, "+this.debug());

    InputStream out = null;

    switch (type()) {
      case DISK:
        out = new FileInputStream(file());
        break;
      case MEMORY:
        if (os == null && data != null) {
          readCount++;
          out = new myByteArrayInputStream(data);
        }
        break;
      case JAR:
        out = Streams.openResource(actual);
        break;
    }
    if (out == null) {
      throw new IOException("Can't read from file " + this);
    }
    return out;
  }

  OutputStream getOutputStream() throws IOException {

    OutputStream out = null;

    switch (type()) {
      case DISK:
        out = new FileOutputStream(file());
        break;
      case MEMORY: {
        if (os == null && readCount == 0) {
          os = new myByteArrayOutputStream();
          out = os;
        }
      }
      break;
    }
    if (out == null) {
      throw new IOException("Can't write to file " + this+" ("+debug()+")");
    }
    return out;
  }

  public String nameOnly() {
    return null;
  }

  public File file() {
    if (file == null) {
      file = new File(name);
    }
    return file;
  }

  private File file;
//private boolean inUse;

  public static final int
      JAR = 1
      , DISK = 0
      , MEMORY = 2
      , PROGRAM = 3
      ;

  /**
   * Constructor
   *
   * @param name : name of file on disk, or within jar file
   * @param hidden : if true, sets hidden flag (applicable to JAR files only)
   * @param type : DISK, JAR, MEMORY
   */
  public SimFile(String name, boolean hidden, int type) {
    this.actual = name;
    this.hidden = hidden;

switch (type) {
  case JAR:
      name = name.substring(name.lastIndexOf('/') + 1);
    break;
    case PROGRAM:
      // set name to the class, without the package; also,
      // convert to lower case.
      name = (name.substring(name.lastIndexOf('.') + 1)).toLowerCase();
      break;
}

    this.name = name;
    this.type = type;
  }

  private static final String[] typeStr = {"DSK", "JAR", "MEM", "PRG"};
  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    return name;
  }
public String actual() {
  return actual;
}

  public String debug() {
    StringBuilder sb = new StringBuilder();
    sb.append("SimFile[");
    if (hidden)
      sb.append("-");
   sb.append(typeStr[type]);
    sb.append(": name=" + name + " actual=" + actual);
    if (readCount > 0)
      sb.append(" rc:"+readCount);
    sb.append("]");
    return sb.toString();
  }

  private String name;
  private int type;

  public String name() {
    int i = name.lastIndexOf('/');
    return name.substring(i + 1);
  }

  public int type() {
    return type;
  }

  public boolean type(int c) {
    return type == c;
  }

  public String ext() {
    return Path.getExtension(name);
  }

//    /**
//     * Get a list of files within a JAR file
//     * @param jarfile : path of jar file
//     * @return DArray containing files
//     */
//    private static DArray extractFromJAR(String jarfile) {
//
//      DArray a = new DArray();
//      try {
//        ZipFile f = new ZipFile(jarfile);
//        Enumeration e = f.entries();
//        while (e.hasMoreElements()) {
//          ZipEntry ent = (ZipEntry) e.nextElement();
//          String name = ent.getName();
////          if (ext != null && !Path.getExtension(name).equals(ext))
////            continue;
////
//          a.add(new SimFile(name,true));
//        }
//      }
//      catch (IOException e) {
//        throw new Error(e.toString());
//      }
//  return a;
//  }

  private static class ourComparator
      implements Comparator {
    public int compare(Object a, Object b) {
      SimFile sa = (SimFile) a,
          sb = (SimFile) b;
      return sa.name().compareTo(sb.name());
    }

  }

  public boolean hidden() {
    return hidden;
  }

  private byte[] data;

  // the true location of the file; if it's in a JAR, we strip any
  // subdirectories from the name field
  private String actual;

  private boolean hidden;

  private class myByteArrayOutputStream
      extends ByteArrayOutputStream {
    public void close() throws IOException {
      super.close();
      if (os != null) {
        data = os.toByteArray();
        os = null;
      }
    }
  }

  private class myByteArrayInputStream
      extends ByteArrayInputStream {
    public myByteArrayInputStream(byte[] data) {
      super(data);
    }

    public void close() throws IOException {
      super.close();
      readCount--;
    }
  }

}
