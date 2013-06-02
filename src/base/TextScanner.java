package base;

import java.io.*;

public class TextScanner {

  public static int parseInt(String s) {
    int out = 0;
    try {
      out = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      ScanException.toss(e);
    }
    return out;
  }

  public void exception(String msg) {
    last().exception(msg);
  }

  public static double parseDouble(String s) {
    double out = 0;
    try {
      out = Double.parseDouble(s);
    } catch (NumberFormatException e) {
      ScanException.toss(e);
    }
    return out;
  }

  //public static final int
  //      T_WS = 128
  //      ,T_WORD = 129
  //      ;

  private static Reader sysInReader;
  private static Writer sysOutWriter;

  public static Reader getSystemInReader() {
    if (sysInReader == null) {
      sysInReader = new StdInReader();
    }
    return sysInReader;
  }

  public static Writer getSystemOutWriter() {
    if (sysOutWriter == null) {
      sysOutWriter = new StdOutWriter();
    }
    return sysOutWriter;
  }

  public String tokenName(Token t) {
    return DFA.tokenName(dfa, t.id());
    //(dfa == null) ? DFA.defaultTokenName(t.id()) : dfa.tokenName(t.id());
  }

  public static String convert(String orig) {
    return convert(orig, false, (char) 0);
  }

  public static String chomp(String s) {
    while (s.endsWith("\n")) {
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  public static String chomp(StringBuilder s) {
    return chomp(s.toString());
  }

  /**
   * Convert a character to escaped form, append to buffer
   * @param c : char to convert
   * @param delim : if >= 0, delimeter of string that must be escaped
   * @param sb : destination StringBuilder; null to create one
   * @return StringBuilder
   */
  public static StringBuilder convert(char c, char delim, StringBuilder sb) {
    if (sb == null) {
      sb = new StringBuilder();
    }
    switch (c) {
    case '\n':
      sb.append("\\n");
      break;
    default:
      if (c != delim && c >= ' ' && c < (char) 0x80) {
        sb.append(c);
      } else {
        sb.append("\\u");
        sb.append(toHex(c, 4));
      }
      break;
    }
    return sb;
  }

  public static boolean isHexDigit(char c) {
    return hexValue(c) >= 0;
  }

  /**
   * Convert a character to its escaped form
   * @param c char
   * @return String
   */
  public static String convert(char c) {
    return convert(c, (char) 0, null).toString();
  }

  /**
   * Convert string to debug display
   * @param orig String
   * @return String in form [xxxxxx...xxx], with nonprintables converted to
   * unicode or escape sequences, and ... inserted if length is greater than
   * about the width of a line
   */
  public static String debug(String orig) {
    return debug(orig, 75, false);
  }

  /**
   * Convert string to debug display
   * @param orig String
   * @param maxLen : maximum length of resulting string
   * @param pad : if true, pads with spaces after conversion
   * @return String in form [xxxxxx...xxx], with nonprintables converted to
   * unicode or escape sequences, and ... inserted if length is greater than
   * about the width of a line
   */
  public static String debug(String orig, int maxLen, boolean pad) {
    if (maxLen < 8) {
      maxLen = 8;
    }

    StringBuilder sb = new StringBuilder();
    if (orig == null) {
      sb.append("<null>");
    } else {
      sb.append("[");
      convert(orig, false, (char) 0, sb);
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

  private static void convert(String orig, boolean toUnicode, char delim,
      StringBuilder sb) {
    final int NORM = 1, ESC = 3, ERR = 2, ENCODE = 0;

    int state = toUnicode ? NORM : ENCODE;
    for (int i = 0; state != ERR; i++) {
      if (i == orig.length()) {
        if (state > NORM) {
          state = ERR;
        }
        break;
      }
      char c = orig.charAt(i);
      switch (state) {
      case NORM:
        {
          if (c == '\\') {
            state = ESC;
            break;
          }
          sb.append(c);
        }
        break;
      case ENCODE:
        {
          convert(c, delim, sb);
        }
        break;

      case ESC:
        {
          state = NORM;
          switch (c) {
          default:
            state = ERR;
            break;
          case 'u':
            if (i + 5 > orig.length()) {
              state = ERR;
              break;
            }
            sb.append((char) TextScanner.parseHex(orig, i + 1, 4));
            i += 4;
            break;
          case 'n':
            sb.append('\n');
            break;
          case '\'':
          case '"':
            sb.append(c);
            break;
          }
        }
        break;
      }
    }
    if (state > NORM) {
      sb.append(" *** conversion problem");
    }
  }

  /**
   * Convert string to/from embedded format
   * @param orig String
   * @param toUnicode : if true, converts escape sequences to unicode;
   *   false, converts unicode to escape sequences
   * @param delim : if not 0, then if converting FROM unicode, surrounds
   *  output with this character; if converting TO unicode, removes this
   *  character from ends if it exists (it must exist at both ends)
   * @return String
   */
  public static String convert(String orig, boolean toUnicode, char delim) {
    StringBuilder sb = new StringBuilder();
    if (delim != 0) {
      if (!toUnicode) {
        sb.append(delim);
        convert(orig, toUnicode, delim, sb);
        sb.append(delim);
      } else {
        orig = removeQuotes(orig, delim);
        //      if (orig.length() >= 2
        //      && orig.charAt(0) == delim
        //      && orig.charAt(orig.length()-1) == delim) {
        //    orig = orig.substring(1,orig.length()-1);
        //      }
        convert(orig, toUnicode, (char) 0, sb);
      }
    } else {
      convert(orig, toUnicode, delim, sb);
    }
    return sb.toString();
  }

  //  /**
  //   * Convert string to/from embedded format
  //   * @param orig String
  //   * @param toUnicode : if true, converts escape sequences to unicode;
  //   *   false, converts unicode to escape sequences
  //   * @return String
  //   */
  //  private static String convert(String orig, boolean toUnicode) {
  //    return convert(orig, toUnicode, (char) 0);
  //  }

  /**
   * Remove quotes from a string if they exist.
   * Note that no special treatment is given to the last character if it
   * has been escaped:  'xxxx\' will return xxxx\
   * 
   * @param s : String
   * @param delim : quote character to look for
   * @return original string, or original string with first and last characters
   *   removed if they equal delim
   */
  public static String removeQuotes(String s, char delim) {
    do {
      if (s.length() < 2) {
        break;
      }
      if (delim != s.charAt(0) || delim != s.charAt(s.length() - 1)) {
        break;
      }
      s = s.substring(1, s.length() - 1);
    } while (false);
    return s;
  }

  /**
   * Remove quotes from a string.
   * @param s : String
  * @return original string, or original string with first and last characters
   *   removed if they are the same and ' or "
    */
  public static String removeQuotes(String s) {
    do {
      if (s.length() < 2) {
        break;
      }
      char c = s.charAt(0);
      if (!(c == '"' || c == '\'')) {
        break;
      }
      s = removeQuotes(s, c);
    } while (false);
    return s;
  }

  public void setSkipType(int t) {
    this.skipType = t;
  }

  /**
   * Construct a scanner for reading, with optional DFA
   * @param reader : Reader
   * @param sourceDescription : String describing reader, or null; i.e. filename
   * @param dfa : DFA to use, or null
   * @param skipType : id of tokens to skip, or -1
   */
  public TextScanner(Reader reader, String sourceDescription, DFA dfa,
      int skipType) {
    this(dfa, skipType);
    include(reader, sourceDescription);
  }

  public TextScanner(DFA dfa, int skipType) {
    this.dfa = dfa;
    this.skipType = skipType;
  }

  public TextScanner(Reader reader) {
    
     
    include(reader, null);
  }

  /**
   * Construct a scanner for a string, with no DFA
   * @param str String
   */
  public TextScanner(String str) {
    include(new StringReader(str), str);
  }

  public void pushTrace(boolean t) {
    traceFlags.pushBoolean(tracing);
    setTrace(t);
  }

  public boolean trace() {
    return tracing;
  }

  public void popTrace() {
    setTrace(traceFlags.popBoolean());
  }

  public void setTrace(boolean t) {
    tracing = t;
  }

  private DArray traceFlags = new DArray();

  /**
   * Read a whitespace-delimited token from a reader.
   * @param r Reader
   * @return String; empty if no tokens remain
   */
  public static String readToken(Reader r) throws IOException {
    StringBuilder sb = new StringBuilder();
    while (true) {
      int c = r.read();
      if (c == -1) {
        break;
      }
      if (c <= ' ') {
        if (sb.length() == 0) {
          continue;
        }
        break;
      }
      sb.append((char) c);
    }
    return sb.toString();
  }

  /**
   * Read a line, trimming any crs.
   * @return String, or null if no characters remain in reader
   */
  public String readLine() {
    StringBuilder sb = new StringBuilder();
    while (true) {
      int c = readChar();
      if (c == -1) {
        if (sb.length() == 0) {
          return null;
        }
        break;
      }
      if (c == '\n') {
        break;
      }
      sb.append((char) c);
    }
    return sb.toString();
  }

  private void traceMsg(String s) {
    System.out.println(" (Scanner: " + s + ")");
    //    inf.update();
  }

  //  Inf inf = new Inf("scanner", 200);

  //  public void include(Reader r, String description) {
  //    include(r,description);
  //
  //    if (tracing) {
  //      traceMsg("include " + description);
  //    }
  //    if (ri != null) {
  //      readerStack.push(ri);
  //    }
  //    ri = new MyReader(r, description, dfa, 1024);
  //  }

  /**
   * Include reader
   * @param r  : Reader
   * @param description : for display purposes, to tell user what
   *   file produced an error
   * @param toFront : where in queue to place reader: true if reader should be inserted in front
   *  of existing ones; false to append to end of existing ones
   *
   */
  public void include(Reader r, String description, boolean toFront) {
    include(r, description, toFront, 0, 0);
  }

  /**
   * Include reader
   * @param r  : Reader
   * @param description : for display purposes, to tell user what
   *   file produced an error
   * @param toFront : where in queue to place reader: true if reader should be inserted in front
   *  of existing ones; false to append to end of existing ones
   *
   */
  public void include(Reader r, String description, boolean toFront,
      int startLine, int startCol) {
     
    if (tracing) {
      traceMsg("include " + description);
    }
    MyReader riNew = new MyReader(r, description, dfa, startLine, startCol);

    if (toFront) {
      if (ri != null) {
        readerQueue.push(ri, true);
      }
      startReader(riNew);
    } else {
      readerQueue.push(riNew);
    }
  }

  private void startReader(MyReader r) {
    ri = r;
    if (echo) {
      echoNewReader(ri.description);
    }
  }

  public void include(Reader r, String description) {
    include(r, description, true);
  }

  //  private static String[] tokenize(String s, char delimiter,
  //                                   boolean skipEmptyTokens) {
  //    final boolean p = false;
  //
  //    if (p) {
  //      System.out.println("tokenize [" + s + "]");
  //    }
  //    DArray strs = new DArray();
  //    StringBuilder sb = new StringBuilder();
  //    {
  //      boolean escaped = false;
  //      for (int i = 0; i <= s.length(); i++) {
  //        char c = delimiter;
  //        if (i < s.length()) {
  //          c = s.charAt(i);
  //        }
  //        else {
  //          Tools.ASSERT(!escaped);
  //        }
  //
  //        if (!escaped) {
  //          if (c == '\\') {
  //            escaped = true;
  //            continue;
  //          }
  //          if (c == delimiter) {
  //            if (!skipEmptyTokens || sb.length() > 0) {
  //              strs.add(sb.toString());
  //              if (p) {
  //                System.out.println(" added [" + strs.last() + "]");
  //              }
  //            }
  //            sb.setLength(0);
  //            continue;
  //          }
  //        }
  //        sb.append(c);
  //        escaped = false;
  //      }
  //      return strs.toStringArray();
  //    }
  //  }

  /**
   * Return the first n characters of a string
   * @param s String
   * @return First n characters of string, or characters before first
   *   linefeed, whichever is first; "..." added if necessary
   */
  public static String strStart(String s) {
    if (s == null) {
      return "<null string!>";
    }

    int n = 40;

    int i = s.indexOf('\n');
    if (i < 0) {
      i = s.length();
    }

    boolean partial = false;
    if (i > n) {
      i = n - 3;
      partial = true;
    }

    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(s.substring(0, i));
    if (partial) {
      sb.append("...");
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * Get last token read
   * @return Token, or null if none read yet
   */
  public Token last() {
    return last;
  }

  private Token last = Token.eofToken();

  /**
   * Read token.  Returns eof token if end of input.
   * @return Token
   */
  public Token read() {
    return read(false);
  }

  public String getPosition() {
    Token t = last();
    if (t == null) {
      t = Token.eofToken();
    }
    return t.display();
  }

  public Token read(boolean mustExist) {
    return read(mustExist, false);
  }

  public boolean closed() {
    return readerQueue == null;
  }

  public void finalize() throws Throwable {
    close();
    super.finalize();
  }

  public void close() {
    try {
      if (!closed()) {
        if (ri != null) {
          ri.close();
          ri = null;
        }
        while (!readerQueue.isEmpty()) {
          Reader r = (MyReader) readerQueue.pop();
          r.close();
        }
      }
    } catch (IOException e) {
      throw new ScanException(e.getMessage());
    }
  }

  /**
   * Read token.  Returns eof token if end of input, unless mustExist is true,
   *  in which case it throws an exception.
   * @return Token
   */
  private Token read(boolean mustExist, boolean ignoreQueue) {
    Token t = null;

    if (ignoreQueue || tokenQueue.isEmpty()) {

      while (true) {

        // if reader is null, pop reader from queue if it isn't empty;
        // otherwise, return eof

        if (ri == null || ri.closed) {
          if (readerQueue.isEmpty()) {
            t = Token.eofToken();
            break;
          }

          startReader((MyReader) readerQueue.pop());
          continue;
        }

        // construct a token recording current read information
        t = ri.token();
        ri.setLocationTrackingActive(true);

        if (dfa != null) {
          ri.setLocationTrackingActive(false);
          dfa.recognize(ri, t);
          ri.setLocationTrackingActive(true);
        }

        // If token was unknown, consume a single character;
        // otherwise, read token text
        if (t.unknown()) {
          int ch = ri.read();
          if (ch < 0) {
            t = Token.eofToken();
          } else {
            t = new Token(t.source(), t.context(), t.line(), t.column(), ""
                + (char) ch, ch, dfa );
          }
        } else {
          String s = t.text();
          for (int i = 0; i < s.length(); i++) {
            ri.read();
          }
        }

        // if eof token, close reader and loop
        if (t.eof()) {
          try {
            ri.close();
          } catch (IOException e) {
            t.exception(e.toString());
          }
          //          ri.closed = true;
          continue;
        }

        if (skipType >= 0 && t.id(skipType)) {
          if (echo) {
            echo(t);
          }
          continue;
        }
        break;
      }
      if (tracing) {
        trace("Read: " + Tools.f(t.idStr(), 10) + " " + t.text());
      }
      if (echo) {
        echo(t);
      }
    } else {
      t = (Token) tokenQueue.pop();
    }

    if (mustExist && t.eof()) {
      throw new ScanException("Missing token");
    }
    last = t;
    return t;
  }

  public void setEcho(boolean f) {
    this.echo = f;
  }

  public boolean echo() {
    return this.echo;
  }

  private boolean echo;
  private StringBuilder echoBuffer = new StringBuilder();

  private void echoNewReader(String desc) {
    echo(Token.eofToken());
    if (desc == null) {
      desc = "(unknown)";
    }
    Streams.out.println("Reading: " + desc);
  }

  private void echo(Token t) {
    //    Streams.out.println("echo token "+Tools.d(t.text())+" echoBuffLen="+echoBuffer.length()
    //        +" = "+Tools.d(echoBuffer.toString())+" echoLineStartToken="+Tools.d(echoLineStartToken));
    String str = t.text();
    if (t.eof()) {
      if (echoBuffer.length() > 0) {
        echoBuffer.append('\n');
      }
    } else {
      //      if (echoBuffer.length() == 0) {
      //        echoLineStartToken = t;
      //      }
      echoBuffer.append(str);
    }

    if (echoBuffer.length() > 0) {
      //      for (int dispLine = 1 + echoLineStartToken.line(); ; dispLine++) {

      while (true) {
        int crPos = echoBuffer.indexOf("\n");
        if (crPos < 0) {
          break;
        }
        String s = echoBuffer.substring(0, crPos);
        Streams.out.print(Tools.f(1 + ri.echoLineNumber++, 4));
        Streams.out.print(": ");
        Streams.out.println(s);
        echoBuffer.delete(0, crPos + 1);
      }
    }
    //    if (t.eof())
    //      echoLineNumber = 0;
  }

  public void trace(String s) {
    if (tracing) {
      Streams.out.println(s);
    }
  }

  /**
   * Peek at next token.  Returns eof token if at end.
   * @return Token
   */
  public Token peek() {
    return peekAt(0);
  }

  /**
   * Determine if at end of input
   * @return boolean
   */
  public boolean eof() {
    return peek().eof();
  }

  public boolean peek(int id) {
    return peek().id(id);
  }

  /**
   * Peek ahead at a token.  Returns eof token if past end.
   * @param offset : 0...n
   * @return Token
   */
  public Token peekAt(int offset) {
    while (offset >= queued()) {
      if (queued() > 0 && peekAt(queued() - 1).eof()) {
        break;
      }
      Token t = read(false, true);
      tokenQueue.push(t);
    }
    return (Token) tokenQueue.peek (Math.min(offset, queued() - 1), true);
  }

  private int queued() {
    return tokenQueue.size();
  }

  public static String scanSource(Reader src) {
    TextScanner scan = new TextScanner(src);
    StringBuilder sb = new StringBuilder();
    while (true) {
      int ch = scan.readChar();
      if (ch < 0) {
        break;
      }
      sb.append((char) ch);
    }
    return sb.toString();
  }

  /**
   * Extract tokens from a source.
   * @param src : Reader to extract from
   * @param srcDescription : description of source (e.g. filename)
   * @param dfa : DFA to use, or null
   * @param tokenIds : if not null, token ids stored here
   * @param tokenNames : if not null, token names stored here
   * @param skipType : if >= 0, token id to skip
   * @return DArray of Tokens
   */
  public static DArray scanSource(Reader src, String srcDescription, DFA dfa,
      DArray tokenIds, DArray tokenNames, int skipType) {
    DArray tokens = new DArray();
    TextScanner scan = new TextScanner(src, srcDescription, dfa, skipType);
    while (true) {
      Token t = scan.read();
      if (t.eof()) {
        break;
      }
      tokens.add(t);
      if (tokenIds != null) {
        tokenIds.addInt(t.id());
      }
      if (tokenNames != null) {
        tokenNames.add(scan.tokenName(t));
      }
    }
    return tokens;
  }

  // Character-based functions

  /**
   * Read character.  If eof, returns -1.
   * @return char
   */
  public int readChar() {
    return readChar(false);
  }

  public Token readTokenIf(int id) {
    Token tk = peek();
    if (tk.id(id)) {
      return read();
    }
    return null;
  }

  /**
   * Read character.  If eof, returns -1, unless mustExist is true, in which
   *  case it throws an exception.
   * @param mustExist boolean
   * @return char
   */
  public int readChar(boolean mustExist) {
    Token t = read(mustExist);
    int c = t.eof() ? -1 : t.text().charAt(0);
    return c;
  }

  /**
   * Peek at next character.  If eof, returns -1.
   * @return char
   */
  public int peekChar() {
    return peekAtChar(0);
  }

  /**
   * Determine if next characters match a string; only makes sense
   * for character scanning.
   * @param s String
   * @return boolean
   */
  public boolean peek(String s) {
    for (int i = 0; i < s.length(); i++) {
      if (peekAtChar(i) != s.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  public boolean readCharIf(char c) {
    boolean out = peekChar() == c;
    if (out) {
      readChar();
    }
    return out;
  }

  /**
   * Peek at nth character.  If eof, returns -1.
   * @param offset : # characters to look ahead (0=first)
   * @return char
   */
  public int peekAtChar(int offset) {
    int c = -1;
    Token t = peekAt(offset);
    if (!t.eof()) {
      c = t.text().charAt(0);
    }
    return c;
  }

  public boolean readExpChar(char c) {
    return readExpChar(c, false);
  }

  public boolean readExpChar(char c, boolean mustExist) {
    boolean res = false;
    if (readCharIf(c)) {
      res = true;
    } else {
      if (mustExist) {
        throw new ScanException("Expected '" + c + "'");
      }
    }
    return res;
  }

  /**
   * Skip any whitespace, excluding linefeeds
   * @param isLFWS : if true, treats LF as whitespace
   * @return true if eof
   */
  public boolean readWS(boolean isLFWS) {
    while (true) {
      if (eof())
        break;
      int j = peekChar();
      if (!isLFWS && j == '\n')
        break;
      if (j > ' ')
        break;
      read();
    }
    return eof();
  }

  /**
   * Skip any whitespace in input.
   * @return true if eof
   */
  public boolean readWS() {
    return readWS(true);
  }
  //    while (
  //        !eof()
  //        && isWS( (char) peekChar())) {
  //      read();
  //    }
  //    return eof();
  //  }

  /**
   * Utility function: determine if a character is whitespace
   * @param c char
   * @return boolean
   */
  private static boolean isWS(char c, boolean isCRWS) {
    return (c == '\n' ? isCRWS : c <= ' ');
  }

  /**
   * Read past n tokens (or characters); attempt to read past eof throws exception.
   * @param count int
   */
  public void skip(int count) {
    while (count > 0) {
      read(true);
      count--;
    }
  }

  private static final boolean db = false;

  /**
   * Read word, if one exists
   * @param mustExist boolean : if true, and no word exists, throws exception
   * @param parseString boolean : if true, treats single or double quotes
   *   as delimeters for a string
   * @return String
   */
  public String readWord(boolean mustExist, boolean parseString, boolean isCRWS) {

    if (db) {
      System.out.println("readWord mustEx=" + mustExist + " parseStr="
          + parseString);
    }

    final int S_TESTSTARTSTRING = 0, S_NOTSTRING = 1, S_ESCAPED = 2, S_INSTRING = 3, S_DONE = 4;

    StringBuilder sb = new StringBuilder();
    readWS(isCRWS);

    int delim = -1;
    int state = parseString ? S_TESTSTARTSTRING : S_NOTSTRING;

    wt: while (true) {
      int ch = peekChar();
      if (!isCRWS && ch == '\n')
        break;
      readChar();

      //      int ch = readChar();
      if (db) {
        System.out.println(" ch=" + ch + " (" + (char) ch + ") state=" + state
            + " sb=[" + sb.toString() + "]");
      }
      if (ch < 0) {
        if (state == S_INSTRING || state == S_ESCAPED) {
          throw new ScanException("Unclosed string");
        }
        break;
      }

      char c = (char) ch;
      switch (state) {

      case S_TESTSTARTSTRING:
        if (c == '\'' || c == '"') {
          delim = c;
          state = S_INSTRING;
        } else {
          state = S_NOTSTRING;
        }
        break;

      case S_INSTRING:
        if (c == '\\') {
          state = S_ESCAPED;
        }
        if (c == delim) {
          state = S_DONE;
        }
        break;

      case S_NOTSTRING:
      case S_DONE:
        if (isWS(c, isCRWS)) {
          break wt;
        }
        if (state == S_DONE) {
          throw new ScanException("Problem in string");
        }
        break;

      case S_ESCAPED:
        state = S_INSTRING;
        break;
      }
      sb.append(c);
    }

    readWS(isCRWS);
    String out = sb.toString();
    if (db) {
      System.out.println(" parsed [" + out + "]");
    }
    if (out.length() == 0) {
      out = null;
      if (mustExist) {
        throw new ScanException("Missing word");
      }
    }
    return out;
  }

  public String readWord(boolean mustExist) {
    return readWord(mustExist, false, true);
  }

  //  public String readWord(boolean mustExist, boolean isLFWS) {
  //    
  //  }

  //  /**
  //   * Read a whitespace-delimited word or quoted string.  If eof, returns null.
  //   * @return String
  //   */
  //  public String readWordOrStr() {
  //    return readWord(false, true);
  //  }
  /**
   * Read a whitespace-delimited word or quoted string.
   * If eof, returns null.
   * @param removeQuotes : if true, removes quotes from string
   * @return String
   */
  public String readWordOrStr(boolean removeQuotes) {
    String s = readWord(false, true, true);
    if (removeQuotes && s != null) {
      s = removeQuotes(s);
    }
    return s;
  }

  /**
   * Read a whitespace-delimited word.  If eof, returns null.
   * Skips any leading and trailing whitespace.
   * @return String
   */
  public String readWord() {
    return readWord(false);
  }

  public int readInt() {
    int ret;
    String w = readWord();
    try {
      ret = Integer.parseInt(w);
    } catch (NumberFormatException e) {
      throw new ScanException("not an integer: " + w);
    }
    return ret;
  }

  /**
   * Convert a hex digit to an integer.
   * @param c : char '0..9', 'a..f', 'A...F'
   * @return int 0..15
   */
  public static int parseHex(char c) {
    int val = hexValue(c);
    if (val < 0) {
      throw new ScanException("parseHex problem: " + c);
    }
    return val;
  }

  private static int hexValue(char c) {

    c = Character.toUpperCase(c);
    int val = -1;
    if (c >= '0' && c <= '9') {
      val = c - '0';
    } else if (c >= 'A' && c <= 'F') {
      val = c - 'A' + 10;
    }
    return val;
  }
  public static void main(String[] args) {
    int[] v = { 0xf0000000, 0x80000000, 0x7fffffff, 0x0000ffff, };
    for (int i = 0; i < v.length; i++) {

      Streams.out.println("toHex " + toHex(null, v[i], 8) + " = "
          + toHex(null, v[i]));

    }
  }
  public static StringBuilder toHex(StringBuilder sb, int val) {

    int nDig = 8;
    while (nDig > 1) {
      if (((((long) val) >> ((nDig - 1) << 2)) & 0xf) != 0)
        break;
      nDig--;
    }
    return toHex(sb, val, nDig);
  }
  //  
  //    if (sb == null)
  //      sb = new StringBuilder();
  //
  //    int digits = 8;
  //    while (digits > 1 && (val & (0xff << (digits << 2))) == 0) {
  //      digits--;
  //    }
  //
  //    //    int shift = (digits - 1) << 2;
  //    while (digits-- > 0) {
  //      int shift = digits << 2;
  //      int v = (val >> shift) & 0xf;
  //      char c;
  //      if (v < 10) {
  //        c = (char) ('0' + v);
  //      } else {
  //        c = (char) ('a' + (v - 10));
  //      }
  //      sb.append(c);
  //    }
  //    return sb;
  //  }

  /**
   * Convert an integer to a hex string
   * @param val : value
   * @param digits : number of digits to produce (number of nybbles,
   *   starting from the lowest 4 bits, of the value to examine)
   * @return String
   */
  public static String toHex(int val, int digits) {
    return toHex(null, val, digits).toString();

    //    StringBuilder sb = new StringBuilder();
    //
    //    int shift = (digits - 1) << 2;
    //    while (digits-- > 0) {
    //      shift = digits << 2;
    //      int v = (val >> shift) & 0xf;
    //      char c;
    //      if (v < 10) {
    //        c = (char) ('0' + v);
    //      }
    //      else {
    //        c = (char) ('A' + (v - 10));
    //      }
    //      sb.append(c);
    //    }
    //    return sb.toString();
  }

  /**
   * Convert a string of hex digits to an integer
   * @param s String
   * @param offset : index of first digit in string
   * @param length : number of digits
   * @return int
   */
  public static int parseHex(CharSequence s, int offset, int length) {
    int val = 0;
    for (int i = 0; i < length; i++) {
      val <<= 4;
      int n = parseHex(s.charAt(offset + i));
      val |= n;
    }
    return val;
  }

  /**
   * Convert a string of hex digits to an integer
   * @param s String
   * @return int
   */
  public static int parseHex(CharSequence s) {
    return parseHex(s, false);
    //    return parseHex(s, 0, s.length());
  }

  public static int parseHex(CharSequence s, boolean skipDollarSign) {
    if (s.length() > 0 && s.charAt(0) == '$')
      s = s.subSequence(1, s.length());
    return parseHex(s, 0, s.length());
  }

  public static boolean isIdentifierStart(char c) {
    c = Character.toUpperCase(c);
    return c == '_' || (c >= 'A' && c <= 'Z');
  }

  public static boolean isIdentifierMiddle(char c) {
    c = Character.toUpperCase(c);
    return c == '_' || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
  }

  /**
   * Split a string into substrings at line break positions
   * @param str String
   * @param lineWidth : maximum number of characters per row
   * @param lst : substrings are stored here
   * @return int : length of longest substring
   */
  public static int splitString(String str, int lineWidth, DArray lst) {

    int stringStart = 0;
    int lastSpace = -1;

    int cursor = 0;
    int maxStrLen = 0;

    lst.clear();
    while (true) {
      char ch = cursor < str.length() ? str.charAt(cursor) : '\n';
      if (ch <= ' ') {
        lastSpace = cursor;
      }

      // If linefeed, or at maximum width,
      // output a substring from the start of the string to
      // the last space printed (exclusive), or to the maximum
      // width (if no last space exists)

      if (ch == '\n') {
        String ns = str.substring(stringStart, cursor);
        lst.add(ns);
        maxStrLen = Math.max(maxStrLen, ns.length());
        cursor++;
        stringStart = cursor;
      } else if (cursor + 1 - stringStart > lineWidth) {
        if (lastSpace > stringStart) {

          // Consume spaces preceding last space
          int ls = lastSpace - 1;
          while (ls >= stringStart && str.charAt(ls) == ' ') {
            ls--;
          }

          String ns = str.substring(stringStart, ls + 1);
          lst.add(ns);
          maxStrLen = Math.max(maxStrLen, ns.length());

          // Consume spaces following last space
          while (lastSpace + 1 < str.length()
              && str.charAt(lastSpace + 1) == ' ') {
            lastSpace++;
          }
          stringStart = lastSpace + 1;
          cursor = Math.max(stringStart, cursor);
        } else {
          String ns = str.substring(stringStart, cursor);
          lst.add(ns);
          maxStrLen = Math.max(maxStrLen, ns.length());
          stringStart = cursor;
        }
      } else {
        cursor++;
      }
      if (cursor > str.length()) {
        break;
      }
    }
    return maxStrLen;
  }

  public DFA getDFA() {
    return dfa;
  }

  private DQueue readerQueue = new DQueue();
  //  private DArray readerStack = new DArray();
  private DFA dfa;
  private MyReader ri;
  private DQueue tokenQueue = new DQueue();

  // maximum length of any one file (for multiple-line comments)
  //  private static final int MAX_FILE_LENGTH = 65536;
  private static final boolean ALWAYSTRACE = false;
  private boolean tracing = ALWAYSTRACE;
  static {
  if (ALWAYSTRACE)  Tools.warn("always tracing");
  }
  
  private int skipType = -1;

  /**
   * Class to store information about location within reader
   */
  private static class MyReader extends DynamicPushbackReader {
    public MyReader(Reader r, String desc, DFA dfa, int startLine, int startCol) {
      super(r);

      description = desc;
      this.dfa = dfa;
      this.line = startLine;
      this.column = startCol;
    }

    public void close() throws IOException {
      if (!closed) {
        super.close();
        closed = true;
      }
    }

    public int echoLineNumber;
    public boolean closed;

    /**
     * Set whether location tracking is active.
     * If inactive, no line, column, or context information is maintained.
     * @param f boolean
     */
    public void setLocationTrackingActive(boolean f) {
      disableLocationTracking = !f;
    }

    private boolean disableLocationTracking;

    /**
     * Get current line of text from reader.
     * @return String
     */
    public String currentLineOfText() {

      try {
        if (currentLineOfText == null && !disableLocationTracking) {
          // mark current place in reader, then build string of characters up to
          // cr (or eof)
          setLocationTrackingActive(false);

          StringBuilder sb = new StringBuilder();
          while (true) {
            int c = super.read();
            if (c < 0) {
              break;
            }
            sb.append((char) c);
            if (c == '\n') {
              break;
            }
          }

          currentLineOfText = sb.toString();
          unread(currentLineOfText.toCharArray());
          setLocationTrackingActive(true);
        }
      } catch (IOException e) {
        throw new ScanException(e.getMessage());
      }
      return currentLineOfText;
    }

    public int read() {
      try {
        int ch = super.read();
        if (ch >= 0) {
          char c = (char) ch;
          if (!disableLocationTracking) {
            if (c == '\n') {
              currentLineOfText = null;
              line++;
              column = 0;
            } else {
              column++;
            }
          }
        }
        return ch;
      } catch (IOException e) {
        throw new ScanException(e.getMessage());
      }
    }

    /**
     * Construct a token which describes current read position
     * @return Token
     */
    public Token token() {
      Token t = new Token(description, currentLineOfText(), line, column, "",
          Token.T_ASCII, dfa);
      //      lineAtStartOfToken = line;
      return t;
    }

    private String currentLineOfText;
    String description;
    // line number, 0...n
    int line;
    // column, 0...n
    int column;
    DFA dfa;
    //    int lineAtStartOfToken;
  }

  public boolean readIf(int id) {
    Token tk = peek();
    if (tk.id(id)) {
      read();
      return true;
    }
    return false;
  }

  public Token read(int id) {
    Token tk = read();
    if (!tk.id(id)) {
      throw new ScanException("Bad or missing token", tk);
    }
    return tk;
  }

  /**
   * Class for reading from System.in; close() commands don't close
   * the reader.
   */
  private static class StdInReader extends InputStreamReader {
    public StdInReader() {
      super(System.in);
    }

    public void close() {
    }
  }

  /**
   * Class for writing to System.out; close() commands won't close
   * the writer (but will flush)
   */
  private static class StdOutWriter extends OutputStreamWriter {
    public StdOutWriter() {
      super(System.out);
    }

    public void close() throws IOException {
      flush();
    }
  }

  /**
   * Convert value to hex, store in StringBuilder
   * @param sb where to store result, or null
   * @param value0 value to convert
   * @param digits number of hex digits to output
   * @return result
   */
  public static StringBuilder toHex(StringBuilder sb, int value0, int digits) {
    if (sb == null)
      sb = new StringBuilder();

    long value = value0;

    int shift = (digits - 1) << 2;
    while (digits-- > 0) {
      shift = digits << 2;
      int v = (int) ((value >> shift)) & 0xf;
      char c;
      if (v < 10) {
        c = (char) ('0' + v);
      } else {
        c = (char) ('a' + (v - 10));
      }
      sb.append(c);
    }
    return sb;

  }

  //  /**
  //   * @deprecated : use addSp
  //   * @param sb
  //   */
  //  public static void ensureWhitespace(StringBuilder sb) {
  //    int k = sb.length();
  //    if (k == 0 || sb.charAt(k - 1) != ' ')
  //      sb.append(' ');
  //  }

  public double readDouble() {
    String s = readWord(true);
    return parseDouble(s);
  }

}
