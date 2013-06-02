package base;

import java.io.*;

/**
 * Utility class for streams.
 *
 */
public class Streams {

  public static boolean isApplet() {
    return fileAccess.isApplet();
  }

  public static Class classParam(Object p) {
    Class c = null;
    if (p != null) {
      if (p instanceof Class)
        c = (Class) p;
      else
        c = p.getClass();
    }
    return c;
  }

  /**
   * Get a buffered InputStream for reading from a file.  If running in
   * an applet context, uses file in AppletFileList.
   * If no path specified, and running in applet context, reads
   *  from Console Workspace #1, if it exists; if not, throws exception
   * @param path : path of file, or null to read from System.in.
   * @param alwaysInJAR : if true, assumes file is always in the JAR file,
   *   even if it's not running as an applet
   * @return OutputStream
   * @throws IOException
   */
  public static InputStream inputStream(String path, boolean alwaysInJAR)
      throws IOException {

    return getFileAccess().getInputStream(path, alwaysInJAR);
  }

  /**
   * Get a buffered InputStream for reading from a file.  If running in
   * an applet context, uses file in AppletFileList.
   * If no path specified, and running in applet context, reads
   * from Console's stdin text area, or from Console main text area
   * if no stdin exists.
   * @param path : path of file, or null to read from System.in.
   * @return OutputStream
   * @throws IOException
   */
  public static InputStream inputStream(String path) throws IOException {
    return inputStream(path, false);
  }

  /**
   * Get a buffered Reader for a file.
   * If running in an applet context, gets reader for file in
   * the AppletFileList.
   * If no path specified, and running in applet context, reads
   * from Console's stdin text area, or from Console main text area
   * if no stdin exists.
   *
   * @param path String : path of file, or null to read from System.in
   * @return Reader
   */
  public static Reader reader(String path, boolean alwaysInJAR)
      throws IOException {
    return new InputStreamReader(inputStream(path, alwaysInJAR));
  }

  /**
   * Get a buffered Reader for a file.
   * If running in an applet context, gets reader for file in
   * the AppletFileList.
   * If no path specified, and running in applet context, reads
   * from Console's stdin text area, or from Console main text area
   * if no stdin exists.
   *
   * @param path String : path of file, or null to read from System.in
   * @return Reader
   */
  public static Reader reader(String path) throws IOException {
    return reader(path, false);
  }

  /**
   * Get a buffered Reader for a file.
   * If running in an applet context, gets reader for file in
   * the AppletFileList.
   * If no path specified, and running in applet context, reads
   * from Console's stdin text area, or from Console main text area
   * if no stdin exists.
   *
   * @param path File : file to read, or null to read from System.in
   * @return Reader
   */
  public static Reader reader(File path) throws IOException {
    String str = null;
    if (path != null)
      str = path.getPath();
    return reader(str, false);
  }

  /**
   * Get a buffered OutputStream for writing to a file.  If running in
   * an applet context, gets writer for file in AppletFileList.
   * @param path : path of file
   * @return OutputStream
   * @throws IOException
   */
  public static OutputStream outputStream(String path) throws IOException {
    return getFileAccess().getOutputStream(path);
  }

  /**
   * Get a buffered Writer for writing to a file.
   * If running in an applet context, gets writer for file in AppletFileList.
   * @param path : path of file, or null to construct a
   *   non-closing writer for System.out
   * @return Writer
   * @throws IOException
   */
  public static Writer writer(String path) throws IOException {

    Writer w = null;
    if (path == null) {
      w = new NCOutputStreamWriter(outputStream(path));
    } else {
      w = new OutputStreamWriter(outputStream(path));
    }
    return w;
  }

  /**
   * Get an input stream to a data file, which may be stored in the class
   * folder or one of its subfolders.  This is how to access files in a jar.
   * @param path String : name of file
   * @return BufferedInputStream
   */
  public static BufferedInputStream openResource(String path)
      throws IOException {
    return openResource(mainClass, path);
  }

  /**
   * Set the class that defines where resources are located.  If left undefined,
   * will attempt to load data files from the current directory.
   * @param main Object to load resources from, or null
   */
  public static void loadResources(Object main) {
    if (mainClass == null) {
      mainClass = classParam(main);
    }
  }

  private static Class mainClass;

  /**
   * Get an input stream to a data file, which is stored in the
   * class folder (or one of its subfolders)
   * @param path String : path to file
   * @return BufferedInputStream
   * @throws IOException
   */
  public static BufferedInputStream openResource(Class c, String path)
      throws IOException {

    BufferedInputStream out = null;
    if (c == null) {
      out = new BufferedInputStream(new FileInputStream(path));
    } else {
      InputStream is = c.getResourceAsStream(path);
      if (is == null) {
        throw new FileNotFoundException("openResource failed: " + path);
      }
      out = new BufferedInputStream(is);
    }
    return out;
  }

  /**
   * Get a reader. which is stored in the
   * class folder (or one of its subfolders)
   * @param owner : owner of file (for locating it)
   * @param path String : path to file
   * @return Reader
   * @throws IOException
   */
  public static Reader readResource(Class owner, String path)
      throws IOException {
    return new InputStreamReader(openResource(owner, path));
  }

  /**
   * Read a string from a reader, then close the reader
   * @param r reader
   * @return string read
   * @throws IOException
   */
  public static String readStringFrom(Reader r) throws IOException {
    StringBuilder sb = new StringBuilder();
    char[] buff = new char[100];

    while (true) {
      int cnt = r.read(buff);
      if (cnt < 0)
        break;
      sb.append(buff, 0, cnt);
    }
    r.close();
    return sb.toString();
  }

  static void redirectOutput(PrintStream newOutput) {
    out = newOutput;
  }

  public static void report(Throwable e) {
    out.println(e.getMessage());
    out.println(Tools.stackTrace(0, 20, e));
  }

  public static DArray getFileList(String dir, String extension) {
    return getFileAccess().getFileList(dir, extension);
  }

  public static void setFileAccess(IFileAccess f) {
    Streams.fileAccess = f;
  }

  public static IFileAccess getFileAccess() {
    return fileAccess;
  }

  /**
   * Get a FileChooser appropriate to the applet/application context.
   * @return FileChooser
   */
  public static IFileChooser fileChooser() {
    return getFileAccess().getFileChooser();
  }

  /**
   * @return home directory (from "user.home" property)
   */
  public static File homeDirectory() {
    if (homeDir == null) {
      setHomeDirectory(new File(System.getProperty("user.home")));
    }
    return homeDir;
  }

  /**
   * Get the root directory
   * 
   * @return root directory
   */
  public static File rootDirectory() {
    if (rootDirectory == null) {
      setRootDirectory(homeDirectory());
    }

    return rootDirectory;
  }

  /**
   * Write text file if it has changed.
   * @param filename  name of file, relative to root directory
   * @param content  new contents of file
   * @throws IOException 
   */
  public static boolean writeIfChanged(String filename, String content)
      throws IOException {
    final boolean db = false;

    if (db)
      Streams.out.println("Streams.writeIfChanged filename:" + filename
          + " content:" + Tools.d(content));

    boolean changed = false;

    File path = new File(Streams.rootDirectory(), filename);
    String txOld = null;

    try {
      if (db)
        Streams.out.println(" attempting to read old from: " + path.getPath());

      txOld = Streams.readTextFile(path.getPath());
    } catch (FileNotFoundException e) {
      if (db)
        Streams.out.println("old file didn't exist");
    }
    if (txOld == null || !content.equals(txOld)) {
      changed = true;
      if (db) {
        Streams.out.println("writing new file;\n" //
            + "old=" + Tools.d(txOld, 80, false) + "\n" //
            + "new=" + Tools.d(content, 80, false));
      }
      Streams.writeTextFile(path, content);
    }
    return changed;
  }

  /**
   * Define the home directory for the server.
   * We use this as the base for files that may not be visible to the client,
   * for example, backup files, example photos, calendar scripts.
   * 
   * @param dir : home directory; if null, reads it from user.home system property
   */
  public static void setHomeDirectory(File dir) {
    homeDir = dir;
    // in case it's null, read it back so it is set to user.home
    homeDirectory();
  }

  /**
   * The root directory for the application
   * 
   * "Since there's no telling which directory the servlet engine was launched
   * from, you should always use absolute pathnames from inside a servlet.
   * Usually I pass in the root directory in an InitParameter, and create File
   * objects relative to that directory."
   */
  private static File rootDirectory;

  private static File homeDir;

  /**
   * Define the root directory (ROOT) for the servlet. All data files are stored
   * within the tree of directories rooted at this directory. Creates a
   * directory ROOT/_temp_ for storing temporary files.
   * For instance, we store the password .png files in this directory.
   * @param rootDir : root directory
   */
  public static void setRootDirectory(File rootDir) {
    rootDirectory = rootDir;
  }

  /**
   * Create a directory on the client, optionally delete files of a certain age.
   * @param path   path of directory, relative to rootDirectory()
   * @param staleDelay   if >= 0, certain files within the temporary
   *          directory older than staleDelay milliseconds will be deleted
   * @param tempExt : only files within directory that end with this string
   *          will be candidates for deletion
   * @throws ServletException if problem creating or preparing directory
   */
  public static File createDirectory(String path, long staleDelay,
      String tempExt) throws IOException {
    if (false) {
      staleDelay = 1000L * 120;
      Tools.warn("setting short staleDelay");
    }

    final boolean db = false;

    File newDir = new File(rootDirectory(), path);

    if (!newDir.exists()) {
      if (db)
        Streams.out.println("attempting to make directory " + newDir);

      if (!newDir.mkdir())
        throw new IOException("failed to make " + newDir);
    }

    if (staleDelay >= 0) {
      long timeCutoff = System.currentTimeMillis() - staleDelay;

      File[] lst = newDir.listFiles();

      if (db)
        Streams.out
            .println("examining files in " + newDir + " for flushing...");

      for (int i = 0; i < lst.length; i++) {
        File f = lst[i];
        if (!f.isFile() || f.isHidden() || !f.getName().endsWith(tempExt))
          continue;

        if (f.lastModified() < timeCutoff) {
          if (db)
            Streams.out.println(" deleting " + f);
          if (!f.delete())
            throw new IOException("unable to delete: " + f);
        }
      }
    }
    return newDir;
  }

  static void ensureApplet() {
    if (!isApplet()) {
      throw new RuntimeException("Not an applet");
    }
  }

  public static void copyFile(File source, File dest, boolean overwriteExisting)
      throws IOException {
    InputStream in = new BufferedInputStream(new FileInputStream(source));
    copyFile(in, dest, overwriteExisting);
  }

  /**
   * Copy a file.
   * @param inp InputStream containing file
   * @param dest : file to write
   */
  public static void copyFile(InputStream inp, File dest,
      boolean overwriteExisting) throws IOException {

    if (!overwriteExisting && dest.exists()) {
      throw new IOException("Cannot overwrite " + dest.getAbsolutePath());
    }

    OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    final int BUFF_SIZE = 4096;
    byte[] buff = new byte[BUFF_SIZE];
    while (true) {
      int len = inp.read(buff);
      if (len < 0) {
        break;
      }
      out.write(buff, 0, len);
    }
    out.close();
  }

  public static void write(byte[] bytes, File dest, boolean overwriteExisting)
      throws IOException {
    copyFile(new ByteArrayInputStream(bytes), dest, overwriteExisting);
  }

  public static byte[] readBinaryFile(String path) throws IOException {
    FileInputStream r = new FileInputStream(path);
    ByteArrayOutputStream w = new ByteArrayOutputStream();

    byte[] b = new byte[2000];
    while (true) {
      int len = r.read(b);
      if (len < 0)
        break;
      w.write(b, 0, len);
    }
    b = w.toByteArray();
    r.close();
    return b;
  }

  /**
   * Read a file into a string
   * @param path String
   * @return String
   */
  public static String readTextFile(String path) throws IOException {
    return readTextFile(path, false);
  }

  /**
   * Read a file into a string
   * @param file : File to read, or null to read from System.in
   * @return String
   */
  public static String readTextFile(File file) throws IOException {
    String str = null;
    if (file != null)
      str = file.toString();
    return readTextFile(str, false);
  }

  public static String readTextFile(String path, boolean withinJAR)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    Reader r = reader(path, withinJAR);
    while (true) {
      int c = r.read();
      if (c < 0) {
        break;
      }

      sb.append((char) c);
    }
    r.close();
    return sb.toString();
  }

  public static PrintStream printStream(String path) throws IOException {
    OutputStream out = outputStream(path);
    return (path == null) ? new NCPrintStream(out) : new PrintStream(out);
  }

  public static void writeTextFile(File file, String content)
      throws IOException {
    final boolean db = false; //Tools.dbWarn();
    if (db)
      Streams.out.println("writeTextFile file=" + file + "\n  content="
          + Tools.d(content));

    BufferedWriter w = new BufferedWriter(new FileWriter(file));
    w.write(content);
    w.close();
  }

  /**
   * Read string from reader, then close it
   * @param s reader
   * @return string read
   * @throws IOException
   */
  public static String readTextFile(Reader s) throws IOException {
    StringBuilder sb = new StringBuilder();
    while (true) {
      int c = s.read();
      if (c < 0)
        break;
      sb.append((char) c);
    }
    s.close();
    return sb.toString();
  }

  /**
   * Move a file to a temporary directory under a different name
   * @param original : file to be moved
   * @param backupDirectory : backup directory; if null, creates one
   *   named "_tmp_" in original file's directory; if no backup directory
   *   exists, creates one
   * @param maxBackups : if > 0, deletes older copies of backup file
   * @return moved file
   * @throws IOException 
   */
  public static File moveFileToTemp(File original, File backupDirectory,
      int maxBackups) throws IOException {

    final boolean db = false;

    if (db)
      Streams.out.println("Streams.moveFileToTemp original=" + original
          + " backupDir=" + backupDirectory + " maxBackups=" + maxBackups);

    if (backupDirectory == null) {
      backupDirectory = new File(original.getParent(), "_tmp_");
      if (db)
        Streams.out.println(" created backupDirectory " + backupDirectory);

    }
    if (!backupDirectory.isDirectory()) {
      if (!backupDirectory.mkdirs())
        throw new IOException("unable to make backup dir: " + backupDirectory);
    }

    // Get name of backup file
    String nameOnly = original.getName();
    if (db)
      Streams.out.println(" nameOnly=" + nameOnly);

    File backupFile = null;
    if (maxBackups <= 0)
      maxBackups = Integer.MAX_VALUE;

    File oldestBackup = null;

    int number = 0;
    for (; number < maxBackups; number++) {
      backupFile = new File(backupDirectory, nameOnly + "_" + number + ".txt");
      if (db)
        Streams.out.println(" seeing if backupFile exists: " + backupFile);

      if (!backupFile.exists())
        break;

      if (db)
        Streams.out.println(" it does...");

      if (oldestBackup == null
          || oldestBackup.lastModified() > backupFile.lastModified()) {
        oldestBackup = backupFile;
        if (db)
          Streams.out.println("  setting as oldest backup");

      }
      backupFile = null;
    }
    if (backupFile == null) {
      if (db)
        Streams.out.println(" using oldest backup as backupFile");

      backupFile = oldestBackup;
    }
    if (db)
      Streams.out.println(" backupFile=" + backupFile);

    if (backupFile.exists()) {
      delete(backupFile);
    }
    if (db)
      Streams.out.println(" renaming original to backup");

    renameTo(original, backupFile);
    return backupFile;
  }

  public static void renameTo(File src, File dest) throws IOException {
    if (!src.renameTo(dest))
      throw new IOException("failed to rename " + src + " to " + dest);

  }

  public static void delete(File f) throws IOException {
    if (!f.delete())
      throw new IOException("failed to delete " + f);

  }

  /**
   * Non-closing OutputStream for use with OutputStreams that are derived
   * from System.out
   * 
   */
  private static class NCOutputStreamWriter extends OutputStreamWriter {
    public NCOutputStreamWriter(OutputStream s) {
      super(s);
    }

    public void close() throws IOException {
      flush();
    }

  }

  private static class NonClosingSystemOut extends PrintStream {
    public NonClosingSystemOut() {
      super(System.out, true);
    }

    public void close() {
      flush();
    }
  }

  /**
   * Initialize Streams.out.  This is done when the class is loaded,
   * but can be done explicitly later on if necessary (provided to
   * avoid tomcat problems)
   */
  public static void initPrintStream() {
    if (out != null)
      out.close();
    out = new NonClosingSystemOut();
  }

  // PrintStream to use instead of System.out
  public static PrintStream out;

  static {
    initPrintStream();
  }

  private static BufferedReader consoleInput;

  /**
   * Read command from console; for testing purposes
   * @return command 
   */
  public static String getCommand() {
    try {
      if (consoleInput == null)
        consoleInput = new BufferedReader(Streams.reader((String) null));
      Streams.out.print(">");
      String str;
      str = consoleInput.readLine();
      return str;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // StreamFromPath object, to make applet/application functionality
  // transparent
  private static IFileAccess fileAccess = new ApplicationFileAccess();
}
