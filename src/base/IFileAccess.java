package base;
import java.io.*;

/**
 * This interface allows paths to be given as input and
 * produce Input/Output streams, and is used for Applet application emulation.
 */
public interface IFileAccess {
  public boolean isApplet();
  /**
   * Get an InputStream to read a file 
   * @param path : path of file
   * @return InputStream
   * @throws IOException if problem
   */
  public InputStream getInputStream(String path, boolean alwaysInJAR) throws IOException;
  /**
   * Get an OutputStream to write a file 
   * @param path : path of file
   * @return OutputStream
   * @throws IOException if problem
   */
  public OutputStream getOutputStream(String path) throws IOException;
  
  /**
   * Get a list of files within a directory that have a particular extension
   * @param dir : directory
   * @param extension 
   * @return DArray of files
   */
  public DArray getFileList(String dir, String extension);
  
  /**
   * Get a FileChooser
   * @return FileChooser
   */
  public IFileChooser getFileChooser();
  
// use casting to get this
//  /**
//   * Get applet file list. Gets the list of files available to the applet.
//   * @return AppletFileList
//   */
//  public AppletFileList appletFileList();
}
