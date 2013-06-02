package base;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public final class Tools {

  public static PrintWriter getPrintWriter(File f) {
    OutputStreamWriter w = null;
    try {
      w = new OutputStreamWriter(new FileOutputStream(f));
    } catch (java.io.FileNotFoundException e) {
      throw new RuntimeException(e.toString());
    }
    return new PrintWriter(w);
  }

  public static String stackTrace() {
    return stackTraceFmt(1);
  }

  public static String tr() {
    return stackTraceFmt(1);
  }
  public static String tr(int display) {
    return stackTrace(1, display);
  }

  public static String trc() {
    return stackTraceFmt(2);
  }

  private static String stackTraceFmt(int skip) {
    StringBuilder sb = new StringBuilder();
    sb.append(stackTrace(1 + skip, 1));
    sb.append(" : ");
    tab(sb, 24);
    return sb.toString();
  }

  /**
   * Construct a string describing a stack trace
   * 
   * @param skipCount : #
   *          stack frames to skip (actually skips 1 + skipCount, to skip the
   *          call to this method)
   * @param displayCount :
   *          maximum # stack frames to display
   * @return String; iff displayCount > 1, cr's inserted after every item
   */
  public static String stackTrace(int skipCount, int displayCount) {
    // skip 1 for call to this method...
    return stackTrace(1 + skipCount, displayCount, new Throwable());
  }

  /**
   * Construct string describing stack trace
   * 
   * @param skipCount : #
   *          stack frames to skip (actually skips 1 + skipCount, to skip the
   *          call to this method)
   * @param displayCount :
   *          maximum # stack frames to display
   * @param t :
   *          Throwable containing stack trace
   * @return String; iff displayCount > 1, cr's inserted after every item
   */
  public static String stackTrace(int skipCount, int displayCount, Throwable t) {
    final boolean db = false;

    StringBuilder sb = new StringBuilder();

    StackTraceElement[] elist = t.getStackTrace();

    if (db) {
      for (int j = 0; j < elist.length; j++) {
        StackTraceElement e = elist[j];
        sb.append(j >= skipCount && j < skipCount + displayCount ? "  " : "x ");
        String cn = e.getClassName();
        cn = cn.substring(cn.lastIndexOf('.') + 1);
        sb.append(cn);
        sb.append(".");
        sb.append(e.getMethodName());
        sb.append(":");
        sb.append(e.getLineNumber());
        sb.append("\n");

      }
      return sb.toString();
    }

    int s0 = skipCount;
    int s1 = s0 + displayCount;

    for (int i = s0; i < s1; i++) {
      if (i >= elist.length) {
        break;
      }
      StackTraceElement e = elist[i];
      String cn = e.getClassName();
      cn = cn.substring(cn.lastIndexOf('.') + 1);
      sb.append(cn);
      sb.append(".");
      sb.append(e.getMethodName());
      sb.append(":");
      sb.append(e.getLineNumber());
      if (displayCount > 1) {
        sb.append("\n");
      }
    }
    return sb.toString();
  }

  private Tools() {
  }

  /**
   * Makes the current thread sleep for a specified time. Ignores any
   * InterruptedExceptions that occur.
   * 
   * @param time
   *          time, in milliseconds, to sleep() for
   */
  public static void delay(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
    }
  }

  /**
   * Simple assertion mechanism, throws RuntimeException if flag is false
   * @param flag : flag to test 
   * @param message : if flag is false, throws RuntimeException including
   *   this message
   */
  public static void ASSERT(boolean flag, String message) {
    if (!flag) {
      throw new RuntimeException("ASSERTION FAILED " + Tools.stackTrace() + " "
          + message);
    }
  }

  /**
   * Simple assertion mechanism, throws RuntimeException if flag is false
   * @param flag : flag to test 
   */
  public static void ASSERT(boolean flag) {
    if (!flag) {
      throw new RuntimeException("ASSERTION FAILED " + Tools.stackTrace());
    }
  }

  public static boolean rndBool(Random r) {
    if (r == null)
      r = random();
    return r.nextInt() > 0;
  }

  public static void unimp() {
    warn("TODO", null, 1);
  }

  public static void unimp(String msg) {
    warn("TODO", msg, 1);
  }

  private static void warn(String type, String s, int skipCount) {
    String st = Tools.stackTrace(1 + skipCount, 1);
    StringBuilder sb = new StringBuilder();
    sb.append("*** ");
    if (type == null) {
      type = "WARNING";
    }
    sb.append(type);
    if (s != null && s.length() > 0) {
      sb.append(": ");
      sb.append(s);
    }
    sb.append(" (");
    sb.append(st);
    sb.append(")");
    String keyString = sb.toString();

    {
      Object wr = warningStrings.get(keyString);
      if (wr == null) {
        warningStrings.put(keyString, Boolean.TRUE);
        Streams.out.println(keyString);
      }
    }
    //    return true;
  }

  public static void warn(String s) {
    warn(null, s, 1);
    // return true;
    //    String st = Tools.stackTrace(1, 1);
    //    String keyString = "*** WARNING: " + s + " (" + st + ")";
    //    // if (TestBed.DEBUG)
    //    {
    //      Object w = warningStrings.get(keyString);
    //      if (w != null) {
    //        return false;
    //      }
    //      warningStrings.put(keyString, Boolean.TRUE);
    //      Streams.out.println(keyString);
    //    }
    //    return true;
  }

  public static String f(boolean b) {
    return b ? " T" : " F";
  }

  public static String fBits(int word, int nBits) {
    StringBuilder sb = new StringBuilder();

    for (int j = nBits - 1; j >= 0; j--) {
      if ((word & (1 << j)) != 0)
        sb.append('1');
      else
        sb.append('0');
    }
    sb.append(' ');
    return sb.toString();
  }

  /**
   * Format a string to be at least a certain size
   * 
   * @param s :
   *          string to format
   * @param length :
   *          minimum size to pad to; negative to insert leading spaces
   * @return blank-padded string
   */
  public static String f(String s, int length) {
    StringBuilder sb = new StringBuilder();
    sb.setLength(0);
    if (length >= 0) {
      sb.append(s);
      return tab(sb, length).toString();
    } else {
      tab(sb, (-length) - s.length());
      sb.append(s);
      return sb.toString();
    }
  }

  /**
   * Format a string for debug purposes
   * 
   * @param s
   *          String, may be null
   * @return String
   */
  public static String d(CharSequence s) {
    return d(s, 80, false);
  }

  public static String hashCode(Object obj) {
    int hc = 0;
    if (obj != null) {
      hc = obj.hashCode();
    }
    return "[" + Tools.f(MyMath.mod(hc, 10000), 4, true) + "]";
  }

  public static String d(Throwable t) {
    return t.getMessage() + "\n" + stackTrace(0, 15, t);
  }

  public static String d(Object obj) {
    String s = null;
    if (obj != null)
      s = obj.toString();
    return d(s);
  }

  public static String tv(Object obj) {
    if (obj == null)
      return "<null>";
    return "<type=" + obj.getClass().getName() + " value=" + d(obj.toString())
        + ">";
  }

  public static String d(Map m) {
    if (m == null)
      return m.toString();
    StringBuilder sb = new StringBuilder();
    sb.append(dashTitle(80, "Map (size=" + m.size() + ")", true));
    Iterator it = m.keySet().iterator();
    while (it.hasNext()) {
      Object k = it.next();
      sb.append(Tools.f(k.toString(), 50));
      sb.append(" -> ");
      Object v = m.get(k);
      String s = "";
      if (v != null)
        s = TextScanner.chomp(v.toString());

      sb.append(Tools.d(s));
      sb.append("\n");
    }
    sb.append(dashTitle(80, null, true));
    return sb.toString();
  }

  public static String d(Collection c) {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      //      sb.append(' ');
      String s = obj.toString();
      sb.append(s);
      Tools.addCr(sb);
    }
    sb.append("]\n");
    return sb.toString();
  }

  public static String d(char c) {
    StringBuilder sb = new StringBuilder();
    sb.append('\'');
    convert(c, sb);
    sb.append('\'');
    return sb.toString();
  }
  public static String dashTitle(int length, String title) {
    return dashTitle(length, title, false);
  }
  /**
   * Construct a string that is a dashed line with a centered title, i.e.
   *       ========== Title goes here ===========
   * @param length : length of title
   * @param title : title to insert, or null for line of dashes
   * @return string
   */
  public static String dashTitle(int length, String title, boolean addCR) {
    StringBuilder sb = new StringBuilder();
    if (title != null) {
      int c = (length - title.length() - 2) / 2;
      rep(sb, '-', c);
      sb.append(' ');
      sb.append(title);
      sb.append(' ');
    }
    rep(sb, '-', length - sb.length());
    if (addCR)
      sb.append('\n');
    return sb.toString();
  }
  /**
   * Append a character to a StringBuilder some number of times
   * 
   * @param sb :
   *          StringBuilder to modify
   * @param c :
   *          character to append
   * @param count : #
   *          times to append it
   * @return the StringBuilder
   */
  public static StringBuilder rep(StringBuilder sb, char c, int count) {
    while (count-- > 0)
      sb.append(c);
    return sb;
  }

  /**
   * Convert string to debug display
   * 
   * @param orig
   *          String
   * @param maxLen :
   *          maximum length of resulting string
   * @param pad :
   *          if true, pads with spaces after conversion
   * @return String in form [xxxxxx...xxx], with nonprintables converted to
   *         unicode or escape sequences, and ... inserted if length is greater
   *         than about the width of a line
   */
  public static String d(CharSequence orig, int maxLen, boolean pad) {
    if (maxLen < 8) {
      maxLen = 8;
    }

    StringBuilder sb = new StringBuilder();
    if (orig == null) {
      sb.append("<null>");
    } else {
      sb.append("[");
      convert(orig, sb);
      sb.append("]");
      if (sb.length() > maxLen) {
        sb.replace(maxLen - 7, sb.length() - 4, "...");
      }
    }
    if (pad) {
      Tools.tab(sb, maxLen);
    }

    return sb.toString();
  }

  public static String trimLength(String orig, int maxLen, boolean addDots) {
    return trimLength(orig, maxLen, addDots, false);
  }

  public static String trimLength(String orig, int maxLen, boolean addDots,
      boolean cropAtWordBoundary) {
    String ret = orig;
    if (orig.length() > maxLen) {
      StringBuilder sb = new StringBuilder(orig);
      if (addDots && maxLen < 3) {
        addDots = false;
      }
      if (addDots) {
        maxLen -= 3;
      }
      if (cropAtWordBoundary) {
        while (maxLen > 0) {
          //          Streams.out.println(" maxLen="+maxLen+", charAt="+orig.charAt(maxLen));
          if (orig.charAt(maxLen) <= ' ')
            break;
          maxLen--;
        }
      }

      sb.setLength(maxLen);
      if (addDots) {
        sb.append("...");
      }
      ret = sb.toString();
    }
    return ret;

  }

  private static void convert(char c, StringBuilder dest) {
    switch (c) {
    case '\n':
      dest.append("\\n");
      break;
    default:
      if (c >= ' ' && c < (char) 0x80) {
        dest.append(c);
      } else {
        dest.append("\\#");
        dest.append((int) c);
      }
      break;
    }
  }

  private static void convert(CharSequence orig, StringBuilder sb) {
    for (int i = 0; i < orig.length(); i++) {
      convert(orig.charAt(i), sb);
    }
  }

  private static StringBuilder sbw = new StringBuilder();

  public static String sp(int len) {
    if (len <= 0) {
      return "";
    }
    while (sbw.length() < len) {
      sbw.append(' ');
    }
    return sbw.substring(0, len);
  }

  /**
   * Format an int into a string
   * @param v   value
   * @param width   number of digits to display
   * @param spaceLeadZeros  if true, right-justifies string
   * @return String, with format siiii
   *   where s = sign (' ' or '-'), 
   *   if overflow, returns s********* of same size
   */
  public static String f(int v, int width, boolean spaceLeadZeros) {

    String s = Integer.toString(Math.abs(v));
    int pad = width - s.length();

    if (pad >= 0) {
      return f(s, -(width + 1));

      //      if (spaceLeadZeros) {
      //        for (int k = 0; k < pad; k++)
      //          sb.append(' ');
      //      }
      //      if (v < 0)
      //        sb.append('-');
      //      sb.append(s);
      //      while (sb.length() < width + 1)
      //        sb.append(' ');
    } else {
      StringBuilder sb = new StringBuilder();
      if (v < 0)
        sb.append('-');
      while (sb.length() < width + 1)
        sb.append('*');
      return sb.toString();
    }
  }

  /**
   * Format a double into a string, without scientific notation
   * @param v : value
   * @param iDig : number of integer digits to display
   * @param fDig : number of fractional digits to display
   * @return String, with format siiii.fff
   *   where s = sign (' ' or '-'), 
   *   . is present only if fDig > 0
   *   if overflow, returns s********* of same size
   */
  public static String f(double v, int iDig, int fDig) {

    StringBuilder sb = new StringBuilder();

    boolean neg = false;
    if (v < 0) {
      neg = true;
      v = -v;
    }

    int[] dig = new int[iDig + fDig];

    boolean overflow = false;

    // Determine which digits will be displayed.
    // Round last digit and propagate leftward.
    {
      double n = Math.pow(10, iDig);
      if (v >= n) {
        overflow = true;
      } else {
        double v2 = v;
        for (int i = 0; i < iDig + fDig; i++) {
          n /= 10.0;
          double d = Math.floor(v2 / n);
          dig[i] = (int) d;
          v2 -= d * n;
        }
        double d2 = Math.floor(v2 * 10 / n);
        if (d2 >= 5) {
          for (int k = dig.length - 1;; k--) {
            if (k < 0) {
              overflow = true;
              break;
            }
            if (++dig[k] == 10) {
              dig[k] = 0;
            } else
              break;
          }
        }
      }
    }

    if (overflow) {
      int nDig = iDig + fDig + 1;
      if (fDig != 0)
        nDig++;
      for (int k = 0; k < nDig; k++)
        sb.append("*");
    } else {

      sb.append(' ');
      int signPos = 0;
      boolean leadZero = false;
      for (int i = 0; i < iDig + fDig; i++) {
        int digit = dig[i]; //(int) d;
        if (!leadZero) {
          if (digit != 0 || i == iDig || (i == iDig - 1 && fDig == 0)) {
            leadZero = true;
            signPos = sb.length() - 1;
          }
        }
        if (i == iDig) {
          sb.append('.');
        }

        if (digit == 0 && !leadZero) {
          sb.append(' ');
        } else {
          sb.append((char) ('0' + digit));
        }
      }
      if (neg)
        sb.setCharAt(signPos, '-');
    }
    return sb.toString();
  }

  public static String f(double f) {
    return f(f, 5, 3);
  }

  public static String fa(double radians) {
    return f(radians * 180 / Math.PI, 3, 2);
  }

  public static String f(int f) {
    return f(f, 6, true);
  }

  public static String f(int val, int width) {
    return f(val, width, true);
  }

  /**
   * Add spaces to a StringBuilder until its length is at some value. Sort of a
   * 'tab' feature, useful for aligning output.
   * 
   * @param sb :
   *          StringBuilder to pad out
   * @param len :
   *          desired length of StringBuilder; if it is already past this point,
   *          nothing is added to it
   */
  public static StringBuilder tab(StringBuilder sb, int len) {
    sb.append(sp(len - sb.length()));
    return sb;
  }

  /**
   * Add a space to buffer if it doesn't already end with whitespace
   * @param sb
   * @return sb
   */
  public static StringBuilder addSp(StringBuilder sb) {
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) > ' ')
      sb.append(' ');
    return sb;
  }

  /**
   * Add a linefeed to buffer if it doesn't already end with one
   * @param sb
   * @return sb
   */
  public static StringBuilder addCr(StringBuilder sb) {
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n')
      sb.append('\n');
    return sb;
  }

  public static void main(String[] args) {
    String s = "Two powerful undersea earthquakes in the "
        + "Pacific triggered a widespread tsunami "
        + "warning on Thursday but the alert was later "
        + "cancelled and there appeared to be little damage.\n\n"
        + "With memories of last week's deadly series of quakes"
        + " and tsunamis in the region, however, residents of several "
        + "islands fled to higher ground and local authorities closed "
        + "schools and issued alerts to stay off beaches.";
    String f = insertLineFeeds(s, 12);
    Streams.out.println("[\n" + f + "]");
  }
  /**
   * Insert linefeeds in a string so no line exceeds a particular width
   * 
   * @param s :
   *          source string
   * @param width :
   *          maximum width of each line
   * @return String : string with linefeeds inserted
   */
  public static String insertLineFeeds(String s, int width) {

    final boolean db = false;
    StringBuilder dest = new StringBuilder();

    if (true) {
      if (db)
        Streams.out.println("insertLineFeeds: " + s + ", width=" + width);

      for (int i = 0; i < s.length(); i++) {

        // skip leading white space
        if (s.charAt(i) != '\n' && s.charAt(i) <= ' ')
          continue;

        // determine max # chars to fit on line
        int lastWS = 0;
        int adjust = 0;
        for (int j = 0;; j++) {
          char c = '\n';
          if (i + j < s.length())
            c = s.charAt(i + j);

          if (c <= ' ') {
            lastWS = j;
            if (c == '\n')
              break;
          }
          if (j == width) {
            if (lastWS == 0) {
              adjust = 1;
              lastWS = width;
            }
            break;
          }
        }

        for (int k = 0; k < lastWS; k++)
          dest.append(s.charAt(i + k));
        // dest.append("#" + lastWS);
        i += lastWS - adjust;
        dest.append('\n');
      }
    } else {

      // index of start of current line
      int lineStart = 0;

      for (int i = 0, j = 0; i < s.length(); i++, j++) {

        // set c to current char
        char c = s.charAt(i);

        if (lineStart == i && c == ' ') {
          lineStart++;
        }

        // if we've exceeded the maximum line width, scan back
        // to a space

        if (j - lineStart > width) {
          int lastSpace = j - 1;
          for (; lastSpace > 0; lastSpace--) {
            if (s.charAt(lastSpace) == ' ') {
              break;
            }
          }
          int len = lastSpace - lineStart;
          boolean removeSpace = (len > 0);
          if (!removeSpace) {
            len = width;
          }

          if (dest.length() > 0) {
            dest.append("!\n");
          }
          dest.append(s.substring(lineStart, lineStart + len));

          lineStart += len + (removeSpace ? 1 : 0);
        }
      }
    }
    return dest.toString();
  }
  /**
   * Read a text file.
   * 
   * @param f file to read
   * @return String containing file
   * @throws IOException  if problem
   */
  public static String read(File f) throws IOException {
    // Streams.out.println("reading file " + f);
    StringBuilder sb = new StringBuilder();

    BufferedReader in = new BufferedReader(new FileReader(f));
    String str;
    while ((str = in.readLine()) != null) {
      sb.append(str);
      sb.append("\n");
    }
    in.close();
    return sb.toString();
  }

  public static void write(File f, String s) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(f));
    out.write(s);
    out.close();
  }

  public static String hexDump(byte[] buffer) {
    return hexDump(buffer, true);
  }

  public static String strDump(byte[] buffer) {
    return hexDump(buffer, 0, buffer.length, false, false, true);
  }

  public static String hexDump(byte[] buffer, boolean multiline) {
    return hexDump(buffer, 0, buffer.length, multiline, true, multiline);
  }

  /**
   * Compare two strings
   * @param a
   * @param b
   * @return String containing differences, or null if they were equal
   */
  public static String compare(String a, String b) {
    StringBuilder sb = new StringBuilder();
    int diff = -1;
    {
      int i = 0;
      while (true) {
        if (i == a.length())
          break;
        if (i == b.length())
          break;
        if (a.charAt(i) != b.charAt(i)) {
          diff = i;
          break;
        }
        i++;
      }
      if (diff < 0 && (i < a.length() || i < b.length())) {
        diff = i;
      }
    }

    if (diff < 0)
      return null;

    for (int pass = 0; pass < 2; pass++) {
      sb.append("=================\n");
      String s = (pass == 0) ? a : b;
      int cc = 0;
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);

        sb.append(c);
        if (i == diff) {
          sb.append('\n');
          for (int j = 0; j < cc; j++)
            sb.append(' ');
          sb.append("^\n ");
          for (int j = 0; j < cc; j++)
            sb.append(' ');
        }

        cc++;
        if (c == '\n')
          cc = 0;
      }
    }
    sb.append("=================\n");
    return sb.toString();

  }

  /**
   * Compare two byte arrays.
   * 
   * @param b1
   * @param b2
   * @return null if equal, else string showing differences
   */
  public static String compare(byte[] b1, byte[] b2) {
    StringWriter sw = new StringWriter(5000);
    PrintWriter pw = new PrintWriter(sw);

    int rowSize = 32;
    int len = Math.max(b1.length, b2.length);
    len = (len + rowSize - 1) & ~(rowSize - 1);

    for (int i = 0; i < len; i += rowSize) {
      // If this row is the same in both, skip.
      int r1 = Math.min(b1.length - i, rowSize);
      int r2 = Math.min(b2.length - i, rowSize);
      if (r1 < 0)
        r1 = 0;
      if (r2 < 0)
        r2 = 0;
      boolean same = (r1 == r2);
      if (same) {
        for (int j = 0; j < r1; j++)
          if (b1[j + i] != b2[j + i]) {
            same = false;
            break;
          }
      }
      if (same)
        continue;

      for (int side = 0; side < 2; side++) {

        int rowSize1 = side == 0 ? r1 : r2;
        int rowSize2 = side == 0 ? r2 : r1;
        byte[] s1 = (side == 0) ? b1 : b2;
        byte[] s2 = (side == 0) ? b2 : b1;

        pw.print(TextScanner.toHex(i, 4));
        pw.print(": ");

        for (int pass = 0; pass < 2; pass++) {
          if (pass == 1) {
            pw.print("|");
          }

          for (int j = 0; j < rowSize; j++) {
            boolean spaces = true;
            if (j < rowSize1) {
              if (j >= rowSize2 || s1[i + j] != s2[i + j])
                spaces = false;
            }
            if (!spaces) {
              int v1 = s1[i + j];
              if (pass == 0) {
                pw.print(TextScanner.toHex(v1, 2));
              } else {
                int v = v1 & 0x7f;
                if (v < 0x20) {
                  v = '.';
                }
                pw.print((char) v);
              }
            } else {
              pw.print(pass == 0 ? "  " : " ");
            }
            if (pass == 0) {
              pw.print(' ');
              if ((j & 3) == 3) {
                pw.print(' ');
              }
            }
          }
        }
        pw.print('\n');
      }
      pw.print('\n');
    }
    pw.close();
    String s = sw.toString();
    if (s.length() == 0)
      s = null;
    return s;
  }

  public static String hexDump(byte[] buffer, boolean multiline,
      boolean withHex, boolean withASCII) {
    return hexDump(buffer, 0, buffer.length, multiline, withHex, withASCII);
  }

  public static void insertSpaces(StringBuilder sb, int position, int nSpaces) {
    sb.insert(position, sp(nSpaces));
  }

  /**
   * Hexdump a portion of a byte array to a string
   * 
   * @param buffer :
   *          byte array
   * @param offset :
   *          offset to first dumped byte
   * @param length :
   *          number of dumped bytes, or 0 for remaining buffer
   * @param multiline :
   *          true split output into sets of 32 bytes, with offset printed at
   *          start of each line, and linefeeds added to the end
   * @param withHex :
   *          true to display hex dump portion
   * @param withASCII :
   *          true to display ASCII dump portion
   * 
   * @return String
   * 
   */
  public static String hexDump(byte[] buffer, int offset, int length,
      boolean multiline, boolean withHex, boolean withASCII) {
    return hexDump(buffer, offset, length, multiline, withHex, withASCII, true);
  }

  /**
   * Hexdump a portion of a byte array to a string
   * 
   * @param buffer :
   *          byte array
   * @param offset :
   *          offset to first dumped byte
   * @param length :
   *          number of dumped bytes, or 0 for remaining buffer
   * @param multiline :
   *          true split output into sets of 32 bytes, with offset printed at
   *          start of each line, and linefeeds added to the end
   * @param withHex :
   *          true to display hex dump portion
   * @param withASCII :
   *          true to display ASCII dump portion
   * 
   * @return String
   */
  public static String hexDump(byte[] buffer, int offset, int length,
      boolean multiline, boolean withHex, boolean withASCII, boolean hideZeros) {

    StringWriter sw = new StringWriter(5000);
    PrintWriter pw = new PrintWriter(sw);

    if (length == 0) {
      length = buffer.length - offset;
    }
    Tools.ASSERT(offset <= buffer.length && offset + length <= buffer.length);

    int rowSize = length;
    if (multiline) {
      rowSize = 32;
      if (length < rowSize) {
        rowSize = Math.max(4, length);
      }
    }

    long len = length;
    int i = 0;
    while (i < len) {
      int rSize = rowSize;
      if (rSize + i > len) {
        rSize = (int) (len - i);
      }

      if (multiline) {
        pw.print(TextScanner.toHex(i + offset, 4));
        pw.print(": ");
      } else {
        pw.print('|');
      }
      for (int pass = 0; pass < 2; pass++) {
        if (pass == 0 && !withHex) {
          continue;
        }
        if (pass == 1 && !withASCII) {
          continue;
        }
        if (pass == 1 && withHex) {
          pw.print("|");
        }
        for (int j = 0; j < rowSize; j++) {
          if (j < rSize) {
            int val = buffer[i + offset + j];
            if (pass == 0) {
              if (hideZeros && val == 0) {
                pw.print("__");
              } else {
                pw.print(TextScanner.toHex(val, 2));
              }
            } else {
              int v = val & 0x7f;
              if (v < 0x20) {
                v = '.';
              }
              pw.print((char) v);
            }
          } else {
            pw.print(pass == 0 ? "  " : " ");
          }
          if (pass == 0) {
            pw.print(' ');
            if ((j & 3) == 3) {
              pw.print(' ');
            }
          }
        }
      }
      if (multiline) {
        pw.print('\n');
      } else {
        pw.print('|');
      }
      i += rSize;
    }
    pw.close();
    return sw.toString();
  }

  public static String st() {
    return stackTrace(1, 5);
  }

  public static void printWarnings() {
    Iterator it = warningStrings.keySet().iterator();
    while (it.hasNext())
      Streams.out.println(it.next());
  }

  private static HashMap warningStrings = new HashMap();

  public static String fh(int n) {
    return "$" + TextScanner.toHex(n, 8);
  }

  public static String fh4(int n) {
    return "$" + TextScanner.toHex(n, 4);
  }

  /**
   * Sleep for a number of milliseconds by calling Thread.sleep().
   * Ignore any InterruptedException that is thrown.
   * 
   * @param ms : delay in milliseconds
   */
  public static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Streams.report(e);
    }

  }

  public static String toString(Map map) {

    StringBuilder sb = new StringBuilder();
    if (map == null) {
      sb.append(map);
    } else {
      sb.append("Map:\n");
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        Object key = it.next();
        sb.append(" ");
        sb.append(Tools.f(Tools.tv(key), 18));

        // sb.append(" "+Tools.f(key.toString(),12));
        sb.append(" -> ");
        sb.append(" " + Tools.f(map.get(key).toString(), 70));
        sb.append("\n");
      }
    }

    return sb.toString();
  }

  public static Random rseed(long seed) {
    rnd = null;
    if (seed == 0)
      random();
    else
      rnd = new Random(seed);
    return rnd;
  }

  public static Random random() {
    if (rnd == null)
      rnd = new Random();
    return rnd;
  }

  public static int rnd(int i) {
    return random().nextInt(i);
  }

  private static Random rnd;

  public static String fa2(double ang) {
    return fa(MyMath.normalizeAnglePositive(ang));
  }

  public static void showDialog(String msg) {
    new MyDialog(msg);
  }
public static void pr(Object obj) {
  Streams.out.println(obj);
}

  private static class MyDialog extends JDialog implements ActionListener {
    private JPanel msg;
    private int counter;

    private void msg(String s) {

      //      DArray lst = new DArray();
      //      TextScanner.splitString(s, 865, lst);
      //
      //      for (int i = 0; i < lst.size(); i++) {
      if (counter != 0) {
        Dimension d = new Dimension(5, 5);
        msg.add(new Box.Filler(d, d, d));
      }
      counter++;
      msg.add(new JLabel(s)); //lst.getString(i)));
      //      }
    }

    public MyDialog(String m) {
      super((Frame) null, "Debugging message", true);
      msg = new JPanel();
      msg.setBorder(new EmptyBorder(10, 10, 10, 10));
      msg.setLayout(new BoxLayout(msg, BoxLayout.Y_AXIS));

      msg(m);

      getContentPane().add(msg);
      JPanel buttonPane = new JPanel();
      JButton button = new JButton("OK");
      buttonPane.add(button);
      button.addActionListener(this);
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      pack();
      setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
      setVisible(false);
      dispose();
    }
  }
  public static boolean dbWarn() {
    warn(null, "debug output active", 1);
    return true;
  }

  public static boolean dbWarn(boolean b) {
    if (b) {
      warn(null, "debug output active", 1);
    }
    return b;
  }

}
