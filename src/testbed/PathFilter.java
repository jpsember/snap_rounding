package testbed;

import java.io.*;
import base.*;

  public class PathFilter extends javax.swing.filechooser.FileFilter implements
    FilenameFilter {
  private final boolean db = false;

  /**
   * Constructor
   * @param f : FilenameFilter to use 
   */
  public PathFilter(FilenameFilter f) {
    if (db)
      Streams.out.println("PathFilter construct for FilenameFilter " + f + "\n"
          + Tools.st());
    this.filter = f;
  }

  /**
   * Constructor
   * @param extensions : space-separated list of extensions to allow
   */
  public PathFilter(String extensions) {
    if (db)
      Streams.out.println("PathFilter construct for extensions= " + extensions
          + "\n" + Tools.st());
    TextScanner sc = new TextScanner(extensions);
    while (true) {
      String w = sc.readWord();
      if (w == null)
        break;
      ext.add(w);
    }
    if (db)
      Streams.out.println(" extensions now " + ext);

  }

  //  public PathFilter(FilenameFilter f) {
  //    this.filter = f;
  //  }

  /**
  * Accept file?
  * @param dir File, or null
  * @param name String
  * @return boolean
  */
  public boolean accept(File dir, String name) {
    if (filter != null)
      return filter.accept(dir, name);

    boolean flag = Streams.isApplet();
    if (!flag) {
      File f = new File(name);

      if (f.isDirectory()) {
        flag = true;
      } else
        flag = accept(name);
    }
    return flag;
  }

  public boolean accept(File file) {
    if (file.isDirectory()) return true;
    return accept(file.getPath());
  }

  private boolean accept(String path) {
    String e = Path.getExtension(path);
    for (int i = 0; i < ext.size(); i++)
      if (e.equals(ext.getString(i))) {
        return true;
      }
    return false;
  }

  public String getDescription() {
    if (db)
      Streams.out.println("getDescription, ext=" + ext);
    String ret = null;
    if (ext.size() > 0) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < ext.size(); i++) {
        if (i > 0)
          sb.append(", ");
        sb.append("*.");
        sb.append(ext.get(i));
      }
      ret = sb.toString();
    } else
      ret = "*** override PathFilter: getDescription ***";
    if (db)
      Streams.out.println(" returning description: " + ret);

    return ret;
  }

  protected DArray ext = new DArray();

  private FilenameFilter filter;

  /**
   * Construct a PathFilter from a FilenameFilter.
   * If FilenameFilter is already a PathFilter, just returns it.
   * @param filter : FilenameFilter
   * @return PathFilter
   */
  public static PathFilter construct(FilenameFilter filter) {
    PathFilter ret = null;

    if (filter instanceof PathFilter)
      ret = (PathFilter) filter;
    else
      ret = new PathFilter(filter);
    return ret;
  }
}
