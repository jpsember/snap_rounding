package testbed;

import java.io.*;
import base.*;

/**
 * Keeps track of file path, name, extension, and modified flags
 */
public class FileStats {

  /**
   * Add filepath as hidden gadget, so it is stored in the 
   * configuration file
   * @param pathId id to store filepath as
   */
  public void persistPath(int pathId) {
    this.pathId = pathId;
    C.sHide();
    C.sTextField(pathId, null, null, 200, false, null);
  }
  public boolean modified() {
    return modified;
  }
  public void setModified(boolean f) {
    modified = f;
  }

  public String getPath() {
    String filePath = C.vs(pathId);

    if (filePath.length() == 0) {
      filePath = lastPathOnly;
      if (filePath != null && filePath.length() != 0) {
        File file = new File(filePath);
        if (!file.isDirectory()) {
          filePath = file.getParent();
        }
        //        Streams.out.println("got path only from lastPathOnly:" + lastPathOnly
        //            + " = " + filePath);
      } else
        filePath = null;
    }
    lastPathOnly = filePath;
    return filePath;
  }
  private boolean modified;
  private int pathId;
  public void setPath(String f) {
    if (f == null)
      f = "";
    C.sets(pathId, f);
  }
  private String lastPathOnly;
}
