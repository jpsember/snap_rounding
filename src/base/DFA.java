package base;

import static base.Tools.*;
import java.io.*;
import java.util.*;

/**
 * Deterministic Finite State Automaton
 */
public class DFA {

  //  /**
  //   * Constructor
  //   * @param path : path to read DFA from
  //   * @deprecated
  //   */
  //  public DFA(String path) {
  //    try {
  //      InputStream s = new FileInputStream(path);
  //      read(s);
  //      s.close();
  //    } catch (IOException e) {
  //      throw new RuntimeException(e);
  //    }
  //  }

  /**
   * Construct a DFA.
   * @param s : InputStream, a binary xxx.dfa file
   */
  public DFA(InputStream s) {
    try {
      read(s);
    } catch (IOException e) {
      ScanException.toss(e);
    }
  }

  /**
   * Read DFA from file.
   * Throws IOExceptions as ScanExceptions.
   * @param owner : owner of file, for resource loader; can be null
   * @param path : name of file, if owner defined; else, filesystem path
   */
  public DFA(Class owner, String path) {

    try {
      InputStream s = Streams.openResource(owner, path);
      read(s);
      s.close();
    } catch (IOException e) {
      ScanException.toss(e);
    }

  }

  /**
   * Read a DFA, if it is not already read, using default DFA map
   * @param owner : owner of DFA, for using class loader to locate it
   * @param path : path of DFA
   * @return DFA
   */
  public static DFA readFromSet(Object owner, String path) {
    return readFromSet(null, owner, path);
  }

  /**
   * Read a DFA, if it is not already read
   * @param map : map to store DFA's within
   * @param owner : owner of DFA, for using class loader to locate it
   * @param path : path of DFA
   * @return DFA
   */
  public static DFA readFromSet(Map map, Object owner, String path) {
    Class c = Streams.classParam(owner);

    String key = path;
    if (c != null) {
      key = c.getName() + key;
    }

    if (map == null) {
      if (defaultDFAMap == null) {
        defaultDFAMap = new HashMap();
      }
      map = defaultDFAMap;
    }

    DFA dfa = (DFA) map.get(key);
    if (dfa == null) {
      dfa = new DFA(c, path);
      map.put(key, dfa);
    }
    return dfa;
  }

  /**
   * Attempt to recognize a token from a string.  If recognized,
   * the current state of the DFA is left at the appropriate final
   * state; if not, the state will be -1.
   * Does not affect position of reader.
   *
   * @param r : reader
   * @param t : token containing context; id and text fields will be modified
   *   if a token is recognized; otherwise, returns T_UNKNOWN or T_EOF with
   *   empty text
   */
  public void recognize(PushbackReader r, Token t) {

    final boolean db = false;
    if (db)
      Streams.out.println("DFA recognize:");

    StringBuilder sb = new StringBuilder();

    try {

      // keep track of the length of the longest token found, and
      // the final state it's associated with
      int maxLengthFound = 0;
      int bestFinalState = -1;
      int bestFinalCode = -1;

      int state = startState();

      while (true) {
        // read character from the reader; exit if eof
        int ch = r.read();
        if (db)
          Streams.out.println(" char read= " + ch + " (" + (char) ch + ")");

        if (ch < 0) {
          break;
        }

        // add character to buffer for later pushing back
        char c = (char) ch;
        sb.append(c);

        // if this is not a legal character for a token, no match.
        if (c > Token.T_ASCII_END)
          break;

        int newState = getTransitionState(state, c);
        // if there's no transition on this symbol from the state, no match.
        if (newState < 0)
          break;

        state = newState;

        // if state has token code, we've reached a final state.
        int token = getState(state).getTerminalCode();

        if (token >= 0) {
          maxLengthFound = sb.length();
          bestFinalCode = token;
          bestFinalState = newState;

          if (db)
            Streams.out.println(" found new final state: " + token);

        }
      }
      state = bestFinalState;

      // push back all characters we read, since location tracking
      // has probably been disabled.
      for (int i = sb.length() - 1; i >= 0; i--)
        r.unread(sb.charAt(i));

      if (bestFinalCode >= 0) {
        t.setId(bestFinalCode);
        t.setText(sb.substring(0, maxLengthFound));
      } else if (sb.length() == 0) {
        t.setText("");
        t.setId(Token.T_EOF);
      } else {
        t.setText(sb.substring(0, 1));
        t.setId(sb.charAt(0));
      }
      if (db)
        Streams.out.println(" read token: " + t);
    } catch (IOException e) {
      throw new ScanException(e.toString());
    }
  }

  /**
   * Determine what state, if any, is reached by transitioning from a
   * state on a symbol
   * @param stateI : initial state
   * @param symbol : symbol to transition on
   * @return new state, or -1 if no transition exists on this symbol
   */
  private int getTransitionState(int stateI, char symbol) {
    verifyValidState(stateI);
    int stateD = -1;
    do {
      if (stateI >= nStates()) {
        break;
      }

      stateD = getState(stateI).getTransitionState(symbol);
    } while (false);
    return stateD;
  }

  private void verifyValidState(int s) {
    if (s < 0 || s > Character.MAX_VALUE) {
      throw new IllegalArgumentException("Invalid state: " + s);
    }
  }

  /**
   * Determine the start state
   * @return int
   */
  private int startState() {
    return startState;
  }

  /**
   * Determine if a state is a final state
   * @param state int
   * @return boolean
   */
  private boolean isFinalState(int state) {
    return getState(state).finalFlag();
  }

  /**
   * Find all the states that contain a transition to a terminal code,
   * and store that code with the state.
   */
  private void storeTerminalFlags() {
    for (int i = 0; i < nStates(); i++) {
      getState(i).setTerminalCode();
    }
  }

  /**
   * Read DFA from a source
   * @param s InputStream
   * @throws IOException
   */
  private void read(InputStream s) throws IOException {

    DataInputStream in = new DataInputStream(s);

    int v = in.readUnsignedShort();
    if (v != VERSION)
      throw new IOException("Bad version in DFA");
    int sCnt = in.readUnsignedShort();
    int sState = in.readUnsignedShort();
    int tokenNameCount = in.readUnsignedShort();
    for (int i = 0; i < sCnt; i++) {
      addState(i);
      getState(i).read(in, v);
    }
    setStartState(sState);

    nSymbols = tokenNameCount;
    symbols = new String[nSymbols];
    for (int i = 0; i < tokenNameCount; i++) {
      String n = in.readUTF();
      symbols[i] = n;
      tokenNameMap.add(n, new Integer(i + Token.T_USER));
      if (false) {
        Tools.warn("dumping debug");
        Streams.out.println("added " + n + ": " + (i + Token.T_USER));
      }
    }

    storeTerminalFlags();
  }
  private String[] symbols;

  public int nSymbols2() {
    return nSymbols;
  }
  public String symbol2(int id) {
    int index = id - Token.T_USER;
    if (index < 0 || id >= nSymbols)
      throw new IllegalArgumentException();
    return symbols[index];
  }
  /**
   * Set start state
   * @param s int
   */
  private void setStartState(int s) {
    verifyValidState(s);

    // If this state is a final state, that's a problem!
    // We don't want to recognize zero-length tokens.
    if (isFinalState(s))
      throw new IllegalArgumentException("Start state cannot be a final state");
    startState = s;
  }

  /**
   * Add a state if it doesn't already exist
   * @param s int
   */
  private void addState(int s) {
    verifyValidState(s);

    // add new states if necessary between end of state
    // array and this state

    while (s >= nStates()) {
      states.add(constructState());
    }
  }

  private DFAState constructState() {
    return new DFAState();
  }

  /**
   * Get the id of a token
   * @param s : name of token
   * @return id of token, or -1 if not found
   */
  public int tokenId(String s) {
    int out = -1;
    Integer ival = (Integer) tokenNameMap.getValue(s);
    if (ival != null)
      out = ival.intValue();
    return out;
  }

  /**
   * Get the name of a token from a DFA
   * @param dfa : DFA, or null to get name of default token
   * @param type : id of token
   * @return name of token, or T_UNKNOWN if it is of unknown type
   */
  public static String tokenName(DFA dfa, int type) {
    String n = null;
    if (dfa != null)
      n = dfa.tokenName(type);
    else
      n = defaultTokenName(type);
    return n;
  }

  /**
   * Get the name of a token from the DFA
   * @param type : id of token
   * @return name of token, or T_UNKNOWN if it is of unknown type
   */
  private String tokenName(int type) {

    final boolean db = false;

    String n = null;

    int t = type - Token.T_USER;

    if (db)
      Streams.out.println("tokenName type=" + type + " t=" + t
          + " tokenNameMap.size=" + tokenNameMap.size());

    if (t >= 0 && t < tokenNameMap.size()) {
      Object obj = tokenNameMap.getKey(t); //)getValue(t);
      if (db)
        Streams.out.println("got value from map=" + Tools.tv(obj));

      n = obj.toString();
      //      n = (String) tokenNameMap.getValue(t);
    } else
      n = defaultTokenName(type);
    return n;
  }
  /**
   * @param type
   * @return
   */
  private static String defaultTokenName(int type) {
    String n = null;
    switch (type) {
    case Token.T_EOF:
      n = "T_EOF";
      break;
    default:
      n = "T_UNKNOWN:" + type;
      break;
    }
    return n;
  }

  private DFAState getState(int n) {
    return (DFAState) states.get(n);
  }

  private int nStates() {
    return states.size();
  }

  // dynamic array of states
  private DArray states = new DArray();

  // start state of the DFA
  private int startState = -1;

  private ArrayMap tokenNameMap = new ArrayMap();
  private int nSymbols;

  private static Map defaultDFAMap;

  //magic number for file version
  private static final int VERSION = 0x9994;

  private static class DFAState {

    public static final int MAX_TERMINAL_CODE = Token.T_USER_END,
        F_TERMINALCODE = MAX_TERMINAL_CODE, F_FINAL = 1 << 15;

    /**
     * Make state a final state.
     */
//    public void setFinalFlag() {
//      setFlag(F_FINAL);
//    }

//    public void setFinalFlag(int code) {
//      int curr = finalCode();
//      if (curr < 0 || curr > code) {
//        setTerminalCode(code);
//        setFinalFlag();
//      }
//    }

    public void setTerminalCode(int n) {
      if (n > MAX_TERMINAL_CODE) {
        throw new IllegalArgumentException("Terminal code too large");
      }

      flags = (char) ((flags & ~F_TERMINALCODE) | n);
    }

//    /*	Determine the final state code associated with this state
//     < code, or -1 if none
//     */
//    public int finalCode() {
//      return (flags & F_TERMINALCODE) - 1;
//    }

    public void setTerminalCode() {
      setTerminalCode(1 + findTokenID());

    }

    public int getTerminalCode() {
      return (flags & F_TERMINALCODE) - 1;
    }

    public boolean finalFlag() {
      return flag(F_FINAL);
    }

//    public void setFlag(int f) {
//      flags |= f;
//    }

    public boolean flag(int f) {
      return (flags & f) != 0;
    }

    /**
     * Find insertion position of transition
     * @param symbol : symbol for transition
     * @return position where symbol is to be inserted / replace existing one
     */
    private int findSymbol(int symbol) {
      int min = 0, max = nTrans() - 1;

      while (true) {
        if (min > max) {
          break;
        }
        int test = (min + max) >> 1;
        int tSym = transSymbol(test);
        if (tSym == symbol) {
          min = test;
          break;
        }
        if (tSym > symbol) {
          max = test - 1;
        } else {
          min = test + 1;
        }
      }
      return min;
    }

    private char transSymbol(int index) {
      return trans.charAt(index << 1);
    }

    private int nTrans() {
      int out = 0;
      if (trans != null) {
        out = trans.length() >> 1;
      }
      return out;
    }

    /**
     * Get state to move to on a symbol
     * @param symbol
     * @return destination state, or -1 if no transition exists
     */
    public int getTransitionState(char symbol) {
      int out = -1;
      int insPos = findSymbol(symbol);
      if (symbolMatches(symbol, insPos)) {
        out = trans.charAt((insPos << 1) + 1);
      }
      return out;
    }

    public DFAState() {
    }

    private boolean symbolMatches(char symbol, int index) {
      return (index < nTrans()) && transSymbol(index) == symbol;
    }

    /*	Read state from file
     */
    void read(DataInputStream r, int version) throws IOException {
      //    reset();
      String s = r.readUTF();
      flags = s.charAt(0);
      char sCount = s.charAt(1);
      if (sCount > 0) {
        trans = new StringBuilder(s.substring(2));
      }

    }

//    /*	Read state from file
//     */
//    public void read(DataInputStream r) throws IOException {
//      read(r, DFA.VERSION);
//    }

    /**
     * Determine which token id, if any, is associated with this state
     * @return id of token, or -1
     */
    private int findTokenID() {
      int out = -1;
      int insPos = findSymbol(Token.T_USER);
      if (insPos < nTrans()) {
        out = transSymbol(insPos);
      }
      return out;
    }

    // F_xxx
    private char flags;

    // State transitions.
    // Each transition is stored as a <symbol, state> pair.
    private StringBuilder trans = new StringBuilder();
  }
}
