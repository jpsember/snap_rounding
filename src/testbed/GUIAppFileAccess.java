package testbed;

import java.io.*;
import base.*;

  class GUIAppFileAccess extends ApplicationFileAccess {
  public IFileChooser getFileChooser() {
    return new ApplicationFileChooser();
  }
  public DArray getFileList(String dir, String extension) {
    File f = new File(dir);
    if (!f.isDirectory()) {
      f = f.getParentFile();
    }
    DArray list = new DArray();

    String[] srcList = f.list(new PathFilter(extension));

    for (int i = 0; i < srcList.length; i++) {
      File f2 = new File(dir, srcList[i]);
      list.add(f2.toString());
    }
    return list;
  }

}
