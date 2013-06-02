package testbed;

import base.*;
import java.io.*;

public class Tokenizer extends TextScanner implements IEditorScript {

  private static DFA dfa;
  private static DFA dfa() {
    if (dfa == null) {
      dfa = DFA.readFromSet(TestBed.class, "veditor.dfa");
    }
    return dfa;
  }

  /**
   * Return value of next integer, skipping non-integer tokens in between
   * @return int
   */
  public int extractInt() {
    while (true) {
      if (eof() || peek(T_INT)) {
        break;
      }
      read();
    }
    return readInt();
  }
  /**
   * Return value of next double, skipping non-numeric tokens in between
   * @return int
   */
  public double extractDouble() {
    while (true) {
      if (eof() || peek(T_DBL) || peek(T_INT)) {
        break;
      }
      read();
    }
    return  readDouble();
  }
  public IPoint2 extractIPoint2() {
    return new IPoint2(extractInt(), extractInt());
  }
  
  
  /**
   * Extract FPoint2, skipping non-double tokens in between
   * @return FPoint2
   */
  public FPoint2 extractFPoint2() {   
    return new FPoint2(extractDouble(), extractDouble());
  }

  public boolean extractBool() {
    while (true) {
      if (eof() || peek(T_BOOL))
        break;
      read();
    }
    return readBoolean();
  }

  /**
   * Constructor
   * @param str : String to tokenize
   * @param skipWS : true to skip whitespace tokens (T_WS)
   */
  public Tokenizer(String str, boolean skipWS) {
    this(new StringReader(str), str, skipWS);
  }

  /**
   * Constructor
   * @param r Reader
   * @param sourceDesc : description of reader
   * @param skipWS : true to skip whitespace tokens (T_WS)
   */
  public Tokenizer(Reader r, String sourceDesc, boolean skipWS) {
    super(r, sourceDesc, dfa(), skipWS ? T_WS : -1);
  }

  public Tokenizer(String path) {
    super(dfa(), T_WS);
    try {
      include(Streams.reader(path), path);
    } catch (IOException e) {
      throw new ScanException(e.toString());
    }
  }

  /**
   * Read a list of integers
   * @param len int
   * @return int[]
   */
  public int[] readInts(int len) {
    int[] a = new int[len];
    for (int i = 0; i < len; i++) {
      a[i] = readInt();
    }
    return a;
  }

  /**
   * Read list of FPoint2's
   * @param len int
   * @return FPoint2[]
   */
  public FPoint2[] readFPoint2s(int len) {
    FPoint2[] a = new FPoint2[len];
    for (int i = 0; i < len; i++) {
      a[i] = readFPoint2();
    }
    return a;
  }

  /**
   * Read a list of doubles
   * @param len int
   * @return double[]
   */
  public double[] readDoubles(int len) {
    double[] a = new double[len];
    for (int i = 0; i < len; i++) {
      a[i] = readDouble();
    }
    return a;
  }

  /**
   * Read next token if it matches a string
   * @param compareTo : string to compare with
   * @return true if next existed and it was an exact match
   */
  public boolean readIf(String compareTo) {
    boolean f = peek().text().equals(compareTo);
    if (f) {
      read();
    }
    return f;
  }

  /**
   * Read token and determine which of a set of strings it matches.
   * Throws an exception if it doesn't match.
   * @param values : array of strings it can match
   * @return index into array
   */
  public int readString(String[] values) {
    Token tk = read();
    String s = tk.text();
    int i = matchString(s, values);
    if (i < 0) {
      tk.exception("unrecognized");
    }
    return i;
  }

  /**
   * Match string to an item in a set of strings.
   * @param str : string
   * @param values : array of strings it can match
   * @return index into array, or -1 if it didn't match any
   */
  public static int matchString(String str, String[] values) {
    int out = -1;
    for (int i = 0; i < values.length; i++) {
      if (str.equals(values[i])) {
        out = i;
        break;
      }
    }
    return out;
  }

  //  /**
  //   * Read next token if it's a string
  //   * @return string, or null if it wasn't a string
  //   * @deprecated
  //   */
  //  public String readIfString() {
  //    String str = null;
  //    if (peek(T_LBL)) {
  //      str = removeQuotes(read().text());
  //    }
  //    return str;
  //  }

  /**
   * Read next token as a string, remove any quotes
   * @return String
   */
  public String readString() {
    String str = removeQuotes(read(T_LBL).text());
    return str;
  }

  /**
   * Read next token as integer value
   * @return int
   */
  public int readInt() {
    return parseInt(read(T_INT).text());
  }

  /**
   * Read next token as double value
   * @return double
   */
  public double readDouble() {

    double val = -1;
    try {
//      this.readWS();
      Token t = read();
      if (t.id(T_INT) || t.id(T_DBL)) {
        val = Double.parseDouble(t.text());
      } else {
        t.exception("expected a double");
      }
    } catch (NumberFormatException e) {
      throw new ScanException(e.getMessage());
    }
    return val;
  }

  /**
   * Read T_BOOL
   * @return boolean
   */
  public boolean readBoolean() {
    return read(T_BOOL).text().equals("T");
  }

  /**
   * Read FPoint2
   * @return FPoint2
   */
  public FPoint2 readFPoint2() {
    FPoint2 pt = new FPoint2();
    try {
      pt.x = readDouble();
      pt.y = readDouble();
    } catch (NumberFormatException e) {
      throw new ScanException(e.getMessage());
    }
    return pt;
  }

  //  /**
  //   * Determine if the next token is an integer (T_INT)
  //   * @return true if so
  //   * @deprecated
  //   */
  //  public boolean nextIsInt() {
  //    return peek(T_INT);
  //  }
  //
  /**
   * Determine if the next token is a double
   * @return true next is T_INT or T_DBL
   */
  public boolean nextIsDouble() {
    return peek(T_INT) || peek(T_DBL);
  }

  //  /**
  //   * Determine if the next token is a double
  //   * @deprecated
  //   * @return true next is T_LBL
  //   */
  //  public boolean nextIsLabel() {
  //    return peek(T_LBL);
  //  }

  /**
   * Read next token, ensure that it matches a value
   * @param str  string to match with
   */
  public void readString(String str) {
    Token t = read();
    if (!str.equals(t.text())) {
      t.exception("expected " + str);
    }
  }

  public void readPastString(String str) {
    while (true) {
      Token t = read();
      if (t.eof())
        t.exception("expected " + str);
      if (t.text().equals(str))
        break;
    }

  }

  /**
   * Read next token if it's an integer
   * @param val : value to return if next token is not an integer
   * @return value read
   */
  public int readIfInt(int val) {
    if (peek(T_INT)) { //nextIsInt()) {
      val = readInt();
    }
    return val;
  }

//  /**
//   * @deprecated
//   * @param val
//   * @return
//   */
//  public double readIfDouble(double val) {
//    if (nextIsDouble()) {
//      val = readDouble();
//    }
//    return val;
//  }
  /**
   * Read next token if it's a double or an int
   * @param val   value to return if it's a double or int
   * @return value read, or val if next token wasn't a double
   */
  public double readIf(double val) {
    if (nextIsDouble()) {
      val = readDouble();
    }
    return val;
  }

  //  /**
  //   * Read next token if it's a boolean (T or F)
  //   * @return Boolean : value read, or null if next wasn't boolean
  //   * @deprecated
  //   */
  //  public Boolean readIfBool() {
  //    Boolean out = null;
  //    if (peek(T_BOOL)) {
  //      out = new Boolean(readBoolean());
  //    }
  //    return out;
  //  }

//  /**
//   * Read next token if it's a boolean (T or F)
//   * @param val : value to return if next token is not an integer
//   * @return value read
//   * @deprecated
//   */
//  public boolean readIfBool(boolean val) {
//    if (peek(T_BOOL))
//      val = readBoolean();
//    //
//    //    Boolean b = readIfBool();
//    //    if (b != null) {
//    //      val = b.booleanValue();
//    //    }
//    return val;
//  }

  public boolean readIf(boolean defaultValue) {
    if (peek(T_BOOL))
      defaultValue = readBoolean();
    //
    //  Boolean b = readIfBool();
    //  if (b != null) {
    //    val = b.booleanValue();
    //  }
    return defaultValue;
  }

}
