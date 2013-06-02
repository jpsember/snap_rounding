package testbed;

import static base.Tools.*;
import base.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class C implements Globals {

  private C() {
  }

  /**
   * Add a push button gadget
   * @param id
   * @param label 
   * @param toolTip if not null, tool tip to display 
   */
  public static void sButton(int id, String label, String toolTip) {
    // b <id:int> <text:label> [<tooltip:label>] 
    sText("b");
    //    if (toolTip != null)
    //      sLbl(toolTip);
    sIValue(id);
    sLbl(label);
    sLblnn(toolTip);

    sNewLine();
  }

  /**
   * Add a checkbox gadget 
   * 
   * @param id   id of gadget
   * @param label  label of gadget
   * @param toolTip if not null, tool tip to display 
   * @param defaultValue   initial value of gadget
   */
  public static void sCheckBox(int id, String label, String toolTip,
      boolean defaultValue) {

    // c <id:int> [<lbl:label>] [<tooltip:label>] <defvalue:bool> 

    if (label == null)
      sHide();

    sText("c");
    sIValue(id);
    sLblnn(label);
    sLblnn(toolTip);
    sBool(defaultValue);
    sNewLine();
  }

  static void sConsole(int idConsole, int rows, boolean commandLine) {
    // C <id:int> <rows:int> <cols:int> <commandline:bool>
    sText('C');
    sIValue(idConsole);
    sIValue(rows);
    sIValue(80);
    sBool(commandLine);
    sNewLine();
  }

  /**
   * Adjust a gadget's enabled state
   * @param id id of gadget
   * @param f true to enable it
   */
  public static void enable(int id, boolean f) {
    list.setEnable(id, f);
  }

  /**
   * Adjust a series of gadget enabled states
   * @param idList array of gadget ids
   * @param f true to enable them
   */
  public static void enable(int[] idList, boolean f) {
    list.setEnable(idList, f);
  }

  /**
   * Get (integer) value of gadget
   * @param id  id of gadget
   * @return value
   */
  public static int vi(int id) {
    return list.intValue(id);
  }

  /**
   * Set value of (integer-valued) gadget 
   * @param id id of gadget
   * @param v value to set
   * @return new value
   */
  public static int seti(int id, int v) {
    list.setValue(id, v);
    return v;
  }

  /**
   * Get (boolean) value of gadget
   * @param id id of gadget
   * @return value
   */
  public static boolean vb(int id) {
    return list.booleanValue(id);
  }

  //  /**
  //   * Get value of boolean-valued gadget
  //   * (assumed to be in main control panel)
  //   * @param id : id of gadget
  //   * @param mustBeEnabled : if true, returns false if gadget is disabled
  //   * @return value
  //   * @deprecated
  //   */
  //  public static boolean vb(int id, boolean mustBeEnabled) {
  //    boolean val = false;
  //    if (!mustBeEnabled || list.enabled(id)) {
  //      val = list.booleanValue(id);
  //    }
  //    return val;
  //  }

  /**
   * Set boolean value of gadget
   * @param id  id of gadget
   * @param boolvalue  boolean value
   * @return new value
   */
  public static boolean setb(int id, boolean boolvalue) {
    list.setValue(id, boolvalue);
    return boolvalue;
  }

  /**
   * Toggle boolean value of gadget
   * @param id  id of gadget
   * @return new value
   */
  public static boolean toggle(int id) {
    return setb(id, !vb(id));
  }

  static ControlPanel getControlPanel(int pnlId) {
    return ctrlPanels[pnlId];
  }

  /**
   * Get (double) value of gadget
   * @param id of gadget
   * @return value
   */
  public static double vd(int id) {
    return list.doubleValue(id);
  }

  /**
   * Set double value of gadget
   * @param id id of gadget
   * @param v  value to set
   * @return new value
   */
  public static double setd(int id, double v) {
    list.setValue(id, v);
    return v;
  }

  /**
   * Get (string) value of gadget
   * @param id id of gadget
   * @return string value
   */
  public static String vs(int id) {
    return list.stringValue(id);
  }

  /**
   * Set value of (string-valued) gadget 
   * @param id id of gadget
   * @param s object to set value to; if null, sets to empty string; otherwise,
   *  calls object's toString() method
   * @return new value
   */
  public static String sets(int id, Object s) {
    String str = "";
    if (s != null)
      str = s.toString();
    list.setValue(id, str);
    return str;
  }

  //  /**
  //   * Add a menu 
  //   * @param script : script describing menu
  //   */
  //    static void addMenu(String script) {
  //    menuPanel.processScript(script);
  //  }

  static Component getComponent(int id) {
    return list.get(id).getComponent();
  }

  /**
   * Determine if a gadget with a particular id exists
   * @param id  id of gadget
   * @return true if it exists
   */
  public static boolean exists(int id) {
    return list.exists(id);
  }

  /**
   * Get gadget
   * @param id : id of gadget
   * @return Gadget
   */
  static Gadget get(int id) {
    return list.get(id);
  }

  /**
   * Start constructing a new script
   * @return StringBuilder containing script
   */
  static void openScript() {
    script = new StringBuilder();
    if (TestBed.parms.traceScript)
      script.append("*\n");
  }

  /**
   * Close constructed script, return as string
   * @return String
   */
  static String closeScript() {
    String s = script.toString();
    script = null;
    
    //pr("closeScript: "+s);
    return s;
  }

  /**
   * Hide the next gadget to be added.
   * A hidden gadget can still be read/written to, and its values will
   * be saved, but it just won't show up on the screen.
   */
  public static void sHide() {
    script.append(" ? ");
  }

  /**
   * Optionally hide the next gadget to be added.
   * A hidden gadget can still be read/written to, and its values will
   * be saved, but it just won't show up on the screen.
   * @param visible if false, hides next gadget
   * @return visibility state of gadget
   */
  public static boolean sHide(boolean visible) {
    if (!visible)
      sHide();
    return visible;
  }

  /**
   * Add an integer-valued spinner
   * @param id
   * @param label
   * @param toolTip
   * @param minValue
   * @param maxValue
   * @param defaultValue
   * @param stepSize
   */
  public static void sIntSpinner(int id, String label, String toolTip,
      int minValue, int maxValue, int defaultValue, int stepSize) {
    // s <lbl:label> <id:int> [<tooltip:label>] <min:int> <max:int> <def:int> <step:int> 
    sText('s');
    sLbl(label);
    sIValue(id);
    sLblnn(toolTip);
    sIValue(minValue);
    sIValue(maxValue);
    sIValue(defaultValue);
    sIValue(stepSize);
    sNewLine();
  }

  /**
   * Add an integer-valued slider
   * @param id
   * @param label
   * @param toolTip
   * @param minValue
   * @param maxValue
   * @param defaultValue
   * @param stepSize
   */
  public static void sIntSlider(int id, String label, String toolTip,
      int minValue, int maxValue, int defaultValue, int stepSize) {
    // sl <lbl:label> <id:int> [<tooltip:label>] <min:int> <max:int> <def:int> <step:int> 
    sText('S');
    sLblnn(label);
    sIValue(id);
    sLblnn(toolTip);
    sIValue(minValue);
    sIValue(maxValue);
    sIValue(defaultValue);
    sIValue(stepSize);
    sNewLine();
  }

  /**
   * Add a spinner to manipulate a double value
   * @param id
   * @param label
   * @param toolTip
   * @param minValue
   * @param maxValue
   * @param defaultValue
   * @param stepSize
   */
  public static void sDoubleSpinner(int id, String label, String toolTip,
      double minValue, double maxValue, double defaultValue, double stepSize) {
    sText("sd");
    sLblnn(label);
    sIValue(id);
    sLblnn(toolTip);
    sDValue(minValue);
    sDValue(maxValue);
    sDValue(defaultValue);
    sDValue(stepSize);
    sNewLine();
  }

  /**
   * Add static (immutable) text to panel
   * @param s
   */
  public static void sStaticText(String s) {
    sText('l');
    sIValue(TestBed.parms.staticTextWidth);
    sLbl(s);
    sNewLine();
  }

  static void sIValue(int iValue) {
    script.append(' ');
    script.append(iValue);
  }

  static void sDValue(double dValue) {
    script.append(' ');
    script.append(dValue);
  }

  /**
   * Add a text area.  These are not (at present) editable by the user, but
   * their contents can be changed by the program
   * @param id id
   * @param label if not null, label to display
   * @param toolTip
   * @param fixedWidth if true, uses a monotone-spaced font
   * @param defaultValue initial value of text (if null, uses empty string)
   */
  public static void sTextArea(int id, String label, String toolTip,
      boolean fixedWidth, String defaultValue) {
    // xf <id:int> [<lbl:label>] 0 [<tooltip:label>] 0 <defaultValue:label> 
    sText(fixedWidth ? "xf" : "x");

    sIValue(id);
    sLblnn(label);
    sIValue(0);
    sLblnn(toolTip);
    sIValue(0);
    if (defaultValue == null)
      defaultValue = "";
    sLbl(defaultValue);
  }

  /**
   * Add a gadget that is not displayed, but is capable of storing
   * an integer
   * @param id id
   * @param defaultValue default value
   */
  public static void sStoreIntField(int id, int defaultValue) {
    sTextField(id, null, null, 11, true, Integer.toString(defaultValue));
  }

  /**
   * Add a text field.  Currently displayed in a button, which when pressed,
   * brings up a modal dialog box for the user to change its value.
   * This is necessary due to conflicts with menu accelerators and other 
   * focus-related issues.
   * @param id id
   * @param label if not null, label to display
   * @param toolTip
   * @param maxLength maximum length of text; any user edits will be truncated
   *   to fit within this limit
   * @param defaultValue initial value of text (if null, uses empty string)
   */
  public static void sTextField(int id, String label, String toolTip,
      int maxLength, boolean fixedWidth, String defaultValue) {
    // t  [<label:label>] <id:int> [<tooltip:label>] <maxlen:int> <value:label>
    // tf [<label:label>] <id:int> [<tooltip:label>] <maxlen:int> <value:label>

    sHide(label != null);
    sText(fixedWidth ? " tf" : " t");
    sLblnn(label);
    sIValue(id);
    sLblnn(toolTip);
    sIValue(maxLength);
    if (defaultValue == null)
      defaultValue = "";
    sLbl(defaultValue);
    sNewLine();
  }

  static void sNewLine() {
    script.append('\n');
  }

  static void sBool(boolean v) {
    script.append(v ? " T" : " F");
  }

  /**
   * Open a nested panel within the current panel.
   * Must be balanced by a call to sClose().
   * @param title if not null, surrounds panel with title box
   */
  public static void sOpen(String title) {
    // ( <title:label> <script> )
    sNewLine();
    sText('(');
    sLblnn(title);
    sNewLine();
  }

  static char sLastChar() {
    char c = ' ';
    int j = C.script.length();
    while (j > 0) {
      j--;
      c = C.script.charAt(j);
      if (c > ' ')
        break;
    }
    return c;
  }

  /**
   * Open a nested panel within the current panel.
   * Must be balanced by a call to sClose()
   */
  public static void sOpen() {
    sOpen(null);
  }

  private static void sText(char c) {
    script.append(' ');
    script.append(c);
  }

  private static void sText(String s) {
    script.append(' ');
    script.append(s);
  }

  /**
   * Close combo box (previously opened with sOpenComboBox())
   */
  public static void sCloseComboBox() {
    if (!inComboBox)
      throw new IllegalStateException("not in combo box");
    sText(')');
    inComboBox = false;
    sNewLine();
  }

  /**
   * Close panel (previously opened with sOpen())
   */
  public static void sClose() {
    sText(')');
    sNewLine();
  }

  /**
   * Add a combo box gadget.
   * Must be balanced by a call to sCloseComboBox()
   * @param id id of gadget
   * @param label if not null, label to display 
   * @param toolTip if not null, tooltip to display
   * @param asRadio if true, displays choices as radio buttons instead
   */
  public static void sOpenComboBox(int id, String label, String toolTip,
      boolean asRadio) {
    // cb <id:int> [<title:label>] <asradio:bool> [<tooltip:label>]
    //       ( {<id:int> <lbl:label>} )
    if (inComboBox)
      throw new IllegalStateException("cannot nest combo boxes");

    inComboBox = true;

    sText("cb");
    sIValue(id);
    sLblnn(label);
    sBool(asRadio);
    sLblnn(toolTip);
    sText('(');
  }

  /**
   * Add a choice to a combobox
   * @param id id of choice
   * @param label 
   */
  public static void sChoice(int id, String label) {
    sIValue(id);
    sLbl(label);
  }

  /**
   * Open a tab set. 
   * Must be balanced by a call to sCloseTabSet().  
   * @param id id of set; reading the value of this gadget will return the
   * identifier of the currently selected tab
   */
  public static void sOpenTabSet(int id) {
    tabSetCount++;
    // (h  [<tabsetid:int>] { [<tabid:int>] <tablabel:label> ( <gadgets:script> ) } )
    sText("(h");
    if (id >= 0)
      sIValue(id);
  }

  /**
   * Add a tab to the tab set (previously opened with sOpenTabSet()).
   * Must be balanced by a call to sCloseTab().  The id of the tab will be
   * its index within the set.
   * @param title title of tab
   */
  public static void sOpenTab(String title) {
    sOpenTab(-1, title);
  }

  /**
   * Add a tab to the tabbed pane (previously opened with sOpenTabSet()).
   * Must be balanced by a call to sCloseTab().
   * @param id id of tab; if < TAB_ID_START, ignores this value and
   *   uses the tab's index as the id
   * @param title title of tab
   */
  public static void sOpenTab(int id, String title) {
    if (tabSetCount == 0)
      throw new IllegalStateException("tab set parity problem");
    sNewLine();
    if (id >= TAB_ID_START)
      C.sIValue(id);
    if (title == null)
      title = "<name?>";
    C.sLbl(title);
    C.sText('(');
    sNewLine();

    tabPaneCount++;
  }

  /**
   * Close tab (previously opened with sOpenTab())
   */
  public static void sCloseTab() {
    if (tabSetCount == 0 || tabPaneCount == 0)
      throw new IllegalStateException("tab set parity problem");
    sText(')');
    sNewLine();
    tabPaneCount--;
  }

  /**
   * Close tab set (previously opened with sOpenTabSet())
   */
  public static void sCloseTabSet() {
    if (tabSetCount == 0)
      throw new IllegalStateException("tab set parity problem");
    tabSetCount--;

    sText(')');
    sNewLine();
  }

  /**
   * Move to the next column within the current panel (previously
   * opened with sOpen())
   */
  public static void sNewColumn() {
    C.script.append(" |");
    sNewLine();
  }

  private static void sLblnn(CharSequence label) {
    if (label != null)
      sLbl(label);
  }

  /**
   * Add a label to the script, enclosed within single quotes '....', and
   * with appropriate escape characters
   * 
   * @param label String, if null, uses empty string
   */
  private static void sLbl(CharSequence label) {
    if (label == null)
      throw new IllegalArgumentException();

    script.append(" '");
    if (label != null) {
      for (int i = 0; i < label.length(); i++) {
        char c = label.charAt(i);
        switch (c) {
        default:
          script.append(c);
          break;
        case '\n':
          script.append("\\n");
          break;
        case '\'':
          script.append("\\'");
          break;
        }
      }
    }
    script.append("'");
  }

  /**
   * Append a string to the script
   * @param obj
   */
  static void sAppend(Object obj) {
    script.append(obj.toString());
  }

  /**
   * Add a script of controls to the main control panel
   * @param script : control script
   */
  static void addControls(String script) {
    addControls(script, TBGlobals.CT_MAIN);
  }

  /**
   * Add a script of controls to one of the panels
   * @param script : control script
   * @param panel : CT_xxx
   */
  static void addControls(String script, int panel) {
    ctrlPanels[panel].processScript(script);
    TestBed.topLevelContainer().validate();
  }

  /**
   * Remove a menu from the menu bar
   * @param id id of menu
   */
  public static void removeMenu(int id) {
    menuPanel.removeMenu(id);
  }

  /**
   * Clear console (if one exists)
   */
  public static void cls() {
    if (consoleTextArea != null) {
      consoleTextArea.cls();
    }
  }

  static MenuPanel menuPanel() {
    return menuPanel;
  }

  /**
   * Get the next anonymous id
   * @return int
   */
  static int getAnonId() {
    return anonIdBase++;
  }

  static void parseGadgets(Tokenizer tk) {
    if (tk.peek(IEditorScript.T_BROP)) {
      list.setValues(tk);
      
      TestBed.app.readGadgetGUIValues();
    }
  }

  /**
   * Print gadgets to configuration file or to editor file header
   * @param pw PrintWriter to print to
   * @param configContext true if writing configuration file, false if writing
   *   editor file header
   */
  static void printGadgets(PrintWriter pw, boolean configContext) {
    TestBed.app.writeGadgetGUIValues();
    String s = list.getValues(configContext);
    pw.println(s);
  }

  static void unsetConsole() {
    if (savedOut != null) {
      Streams.out = savedOut;
      savedOut = null;
      consoleTextArea = null;
    }
  }

  static void setConsole(WorkTextArea w) {
    if (savedOut == null) {
      savedOut = Streams.out;
    }
    Streams.out = w.printStream();
    consoleTextArea = w;
  }

  /**
   * Open a menu.  Must be balanced by a call to sCloseMenu().
   * @param id id of menu
   * @param title title of menu
   */
  public static void sOpenMenu(int id, String title) {
    // <id:int> ( {<menuitem> | <menusep> } )
    C.openScript();
    sIValue(id);
    script.append("( ");
    sLbl(title);
    sNewLine();
  }

  /**
   * Add an item to the current menu
   * @param id id of menu
   * @param label label of menu
   * @param keyEquiv if not null, keyboard equivalent of the form
   * <pre>
   *   ^c   for ctrl-c (command-c on Mac)
   *   ^C   for ctrl-shift c
   *   #ddd keycode for decimal value ddd (i.e., KeyEvent.VK_BACK_SPACE
   * </pre>
   */
  public static void sMenuItem(int id, String label, String keyEquiv) {
    // <id:int> [<keyequiv>] <label>
    sIValue(id);
    if (keyEquiv != null) {
      script.append(' ');
      if (!keyEquiv.startsWith("!"))
        script.append('!');
      script.append(keyEquiv);
    }
    sLbl(label);
    sNewLine();
  }

  /**
   * Add a separator item to the current menu
   */
  public static void sMenuSep() {
    // =
    sNewLine();
    script.append(" =");
    sNewLine();
    sNewLine();
  }

  /**
   * Close menu (previously opened with sOpenMenu())
   */
  public static void sCloseMenu() {
    script.append(")");
    sNewLine();
    menuPanel.processScript(C.closeScript());
    //    C.addMenu(C.closeScript());
  }

  static void init(JFrame forMenu) {
    anonIdBase = TBGlobals.ID_ANON_START;
    consoleTextArea = null;
    ctrlPanels = new ControlPanel[TBGlobals.CT_TOTAL];
    for (int i = 0; i < TBGlobals.CT_TOTAL; i++)
      ctrlPanels[i] = new ControlPanel();
    inComboBox = false;
    list = new GadgetList();
    menuPanel = new MenuPanel(forMenu);
    savedOut = null;
    script = null;
    tabPaneCount = 0;
    tabSetCount = 0;
  }

  private static int anonIdBase;
  private static WorkTextArea consoleTextArea;
  private static ControlPanel[] ctrlPanels;
  private static boolean inComboBox;
  static GadgetList list;
  private static MenuPanel menuPanel;
  private static PrintStream savedOut;
  private static StringBuilder script;
  private static int tabPaneCount;
  private static int tabSetCount;
}
