package base;

import java.io.FilenameFilter;

/**
 * File chooser interface which can be applied to both normal applications,
 * and applets running application simulation
 */
public interface IFileChooser {

  /**
   * Get name of file to open
   *
   * @param prompt prompt to display in file requester
   * @param path  previously selected filename
   * @param filter  if not null, PathFilter to apply to directory
   * @return  if not null, path of file to open
   */
  public String doOpen(String prompt, String path, FilenameFilter filter);

  /**
   * Get name of file to write
   *
   * @param prompt prompt to display in file requester
   * @param path   previously selected filename
   * @param filter   if not null, PathFilter to apply to directory
   * @return String   if not null, path of file to write
   */
  public String doWrite(String prompt, String path, FilenameFilter filter);
}
