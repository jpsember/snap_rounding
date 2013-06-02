package testbed;

import java.io.*;
import javax.swing.*;
import base.*;

class ApplicationFileChooser implements IFileChooser {
  /**
   * Get name of file to open
   * 
   * @param path :
   *          previously selected filename
   * @param filter :
   *          if not null, FileFilter to apply to directory
   * @return String : if not null, path of file to open
   */
  public String doOpen(String prompt, String path, FilenameFilter filter) {

    String out = null;

    JFileChooser chooser = new JFileChooser();
    if (filter != null)
      chooser.setFileFilter(PathFilter.construct(filter)); //new PathFilter(filter));

    if (prompt != null)
      chooser.setDialogTitle(prompt);
    if (path != null)
      chooser.setSelectedFile(new File(path));

    int returnVal = chooser.showOpenDialog(TestBed.getAppContainer());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      out = chooser.getSelectedFile().getPath();
    }

    return out;
  }

  /**
   * Get name of file to write
   * 
   * @param path :
   *          previously selected filename
   * @param filter :
   *          if not null, FileFilter to apply to directory
   * @return String : if not null, path of file to write
   */
  public String doWrite(String prompt, String path, FilenameFilter filter) {
    String out = null;

    JFileChooser chooser = new JFileChooser();

    //    Tools.warn("trying this");
    if (filter != null) {
      chooser.setFileFilter(PathFilter.construct(filter));
      //      chooser.setFileFilter(PathFilter.construct(filter));
      //      
      //      
      //      if (filter instanceof PathFilter)
      //        chooser.setFileFilter((PathFilter) filter);
      //      else
      //        chooser.setFileFilter(new PathFilter(filter));
    }
    if (prompt != null)
      chooser.setDialogTitle(prompt);
    if (path != null)
      chooser.setSelectedFile(new File(path));
    int returnVal = chooser.showSaveDialog(TestBed.getAppContainer());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      out = chooser.getSelectedFile().getPath();
    }
    return out;
  }

}
