package base;

public class Token {
  public static final int T_EOF = -1,
      T_ASCII = 0 // to 127
      // range of legal ASCII values for terminal symbols in
      // regular expressions:
      , T_ASCII_END = 127, T_USER_OLD = 128, T_USER = 256,
      T_USER_END = (1 << 15) - 1;

  public static Token eofToken() {
    return eofToken;
  }

  /**
   * Get a string describing token, suitable for debugging.
   * @return String
   */
  public String debug() {
    StringBuilder sb = new StringBuilder("Token ");
    sb.append("id=" + id);
    Tools.tab(sb, 13);
    sb.append(DFA.tokenName(dfa, id()));
    //    sb.append(dfa == null ? DFA.defaultTokenName(id()) : dfa.tokenName(id()));
    Tools.tab(sb, 35);
    sb.append("text=");
    sb.append(TextScanner.debug(text()));
    return sb.toString();
  }

  public void exception(String descr) {
    StringBuilder sb = new StringBuilder("*** ");
    if (descr != null) {
      sb.append(descr + "\n");
    }
    sb.append(display());
    sb.append("\n");
    //
    //    sb.append(source + " line " + line + " column " + column + "\n");
    //    sb.append(text);
    //    sb.append('\n');
    //    for (int i = 0; i < column; i++) {
    //      sb.append(' ');
    //    }
    //    sb.append('^');
    //    sb.append("\n");
    throw new ScanException(sb.toString());
  }

  public int line() {
    return line;
  }

  public int column() {
    return column;
  }

  public String source() {
    return source;
  }

  public String context() {
    return context;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setText(String t) {
    this.text = t;
  }

  public Token(int id) {
    this(id, null);
  }
  public Token(int id, String text) {
    this.id = id;
    this.text = text;
  }

  public Token(String source, String context, int line, int column,
      String text, int id, DFA dfa) {
    this.source = source;
    //    System.out.println("Token, setting context to "+context);
    this.context = context;
    this.line = line;
    this.column = column;
    this.text = text;
    this.id = id;
    this.dfa = dfa;
  }

  public Token(Token src) {
    this(src.source, src.context, src.line, src.column, src.text, src.id,
        src.dfa);
    //    this.node = src.node;
  }

  public String text() {
    return text;
  }

  public boolean unknown() {
    return (id >= T_ASCII && id <= T_ASCII_END);
  }

  public boolean id(int t) {
    return id == t;
  }

  public int id() {
    return id;
  }

  public void setNode(int node) {
    if (this.node >= 0)
      throw new IllegalStateException();
    this.node = node;
  }
  public int node() {
    return this.node;
  }

  public String idStr() {
    return DFA.tokenName(dfa, id);
    //    
    //    String s = null;
    //    if (dfa != null) {
    //      s = DFA.tokenName(dfa, id); //dfa.tokenName(id);
    //    } else {
    //      s = "" + id;
    //    }
    //    return s;
  }

  public boolean eof() {
    return id == T_EOF;
  }

  public String display() {
    StringBuilder sb = new StringBuilder();
    if (source == null)
      sb.append("(unknown source)");
    else
      sb.append(source);
    sb.append(", line " + (1 + line) + ", column " + (1 + column));

    if (context != null) {
      sb.append(":\n");
      boolean db = false;
      if (db)
        sb.append(">>(context)");
      String ct = context;
      int i = 0;
      while (true) {
        int amt = Math.min(80, ct.length() - i);
        boolean done = (i + amt == ct.length());
        sb.append(ct.substring(i, i + amt));
        sb.append("\n");
        if (column >= i && column < i + amt) {
          Tools.tab(sb, sb.length() + (column - i));
          sb.append("^ ");
          if (!done)
            sb.append("\n");
        }
        i += amt;
        if (done)
          break;
      }
      if (db)
        sb.append("(context)<<");

    }
    return sb.toString();
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString(boolean full) {
    if (full) {
      return display() + debug(); //"Id="+Tools.f(id)+" Text=<"+text+">\n";
    } else {
      String ret = text;
      //ret = "<"+node+">"+ret;
      return ret;
    }
  }

  public String toString() {
    return toString(false);
  }

  public void setContext(String c) {
    this.context = c;
  }

  private static Token eofToken = new Token(null, null, 0, 0, "", T_EOF, null);

  // line of current file (0..n)
  private int line;

  // column of current file (0..n)
  private int column;

  // text of token
  private String text;

  // id of token (T_xxx)
  private int id;

  // description of source (i.e. filename)
  private String source;

  // context of token (i.e. current line)
  private String context;

  // if not null, the DFA that produced this token
  private DFA dfa;

  // node within parse tree, or -1
  private int node = -1;
}
