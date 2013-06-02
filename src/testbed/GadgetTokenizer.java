package testbed;

 import static base.Tools.*;
import base.*;
import javax.swing.SwingConstants;
import java.io.*;

/**
 * Tokenizer for TestBed menus & gadgets.
 */
  class GadgetTokenizer
    extends TextScanner implements IScript {

  public GadgetTokenizer(String s) {
    super(new StringReader(s), null, scriptDFA(), T_WS);
  }

  public int readIfInt(int def) {
    if (peek().id(T_INT)) {
      def = readInt();
    }
    return def;
  }

  public double readDouble() {
    double out = 0;
    if (peek().id(T_INT)) {
      out = readInt();
    }
    else {
      out = Double.parseDouble(read(T_DBL).text());
    }
    return out;
  }

  public int readInt(Token t) {
    return parseInt(t.text());
  }

  public String readLabel() {
    return readLabel(read(T_LBL));
  }

  public int readInt() {
    return readInt(read(T_INT));
  }

  public boolean readBoolean() {
    return read(T_BOOL).text().charAt(0) == 'T';
  }

  public String readIfLabel() {
    String s = null;
    if (peek().id(T_LBL)) {
      s = readLabel();
    }
    return s;
  }

  /**
   * Determine the alignment status for a label.  If the first
   * character of a label (after the opening quote) is <,|,>, the
   * alignment is set to LEFT,CENTER,RIGHT.
   * @param lbl : String containing label
   * @param def : default alignment to return, if first char is other
   * @return alignment
   */
  private static int labelAlignment(char c, int def) {
    switch (c) {
      case '|':
        def = SwingConstants.CENTER;
        break;
      case '<':
        def = SwingConstants.LEFT;
        break;
      case '>':
        def = SwingConstants.RIGHT;
        break;
    }
    return def;
  }


  /**
   * Read alignment code for last label read, then reset it to
   * CENTER
   * @return int : alignment code read (or CENTER if none existed)
   * @deprecated
   */
  public int labelAlignment() {
    int lbl = lblAlignment;
    lblAlignment = SwingConstants.CENTER;
    return lbl;
  }

  /**
   * Read next token as a label, and determine its alignment
   * @return String
   */
  public String readLabel(Token t) {
    String lbl = t.text();
    // convert label to unicode
    String lbl2 = TextScanner.convert(lbl,true,lbl.charAt(0));

    int a = -1;
    if (lbl2.length() > 0)
      a = labelAlignment(lbl2.charAt(0),-1);

    if (a >= 0) {
      lblAlignment = a;
      lbl2 = lbl2.substring(1);
    }
    return lbl2;
  }

  public boolean readIfBool(boolean def) {
    if (peek().id(T_BOOL)) {
      def = readBoolean();
    }
    return def;
  }


  // private static DFA scriptDFA;
  private static DFA scriptDFA() {
    return DFA.readFromSet(TestBed.class,"gadget.dfa");
  }

  // alignment to give next label
  protected int lblAlignment = SwingConstants.CENTER;

}
