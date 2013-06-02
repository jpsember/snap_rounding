package testbed;
import static base.Tools.*;
import base.*;
import javax.swing.*;
import testbed.StackPanel.*;
import java.awt.*;

/**
 * Control panel class
 */
class ControlPanel extends JPanel implements Globals, IScript {

  /**
   * Constructor
   */
  ControlPanel() {
    super(new GridBagLayout());
    setOpaque(true);

    // add glue panel with all the weight in row 2.
    if (GC.USEGLUE) {
      GC.addGlue(this, 0, 1);
      //      GC gc = GC.gc(0, 1, GC.REMAINDER, GC.REMAINDER, 1, 1);
      //      add(GC.glue(), gc);
    }

  }

  /**
   * Add gadget to ControlPanel
   * @param c gadget to add
   * @param id id of gadget being added
   * @param parent gadget to add to, or 0 if it's the master
   * @return true if control was actually added, or false if
   *   it was determined to be a hidden control (or was skipped)
   */
  private boolean addControl(Gadget c, String toolTip) {
    C.list.add(c);

    boolean shown = true;
    if (!hideNextControl) {
      Component cp = c.getComponent();
      if (cp != null)
        panel.addItem(cp);
    }
    hideNextControl = false;

    if (toolTip != null) {
      JComponent j = (JComponent) c.getComponent();
      if (j == null) {
        Tools.warn("JComponent is null for tooltip, c=" + c);
      } else
        j.setToolTipText(parseToolTip(toolTip));
    }

    return shown;
  }

  private void addSpinner(int id, String label, double min, double max,
      double value, double step, boolean sliderFlag, boolean withTicks,
      boolean dbl, String toolTip) {
    if (!(value >= min && value <= max))
      throw new IllegalArgumentException(
          "Spinner/slider initial value not in range");
    addControl(new CtSpinner(id, label, min, max, value, step, sliderFlag,
        withTicks, dbl), toolTip);
  }

  private void addTextField(int id, String label, String value, int maxStrLen,
      boolean fw, String toolTip) {
    addControl(new CtTextFieldNew(id, label, value, maxStrLen, fw), toolTip);
  }
  private void addComboBox() {
    // cb <id:int> [<title:label>] <asradio:bool> [<tooltip:label>]
    //       ( {<id:int> <lbl:label>} )

    final boolean db = false;
    if (db)
      Streams.out.println("ControlPanel.addComboBox");

    int cid = tk.readInt();
    String label = tk.readIfLabel();
    boolean asRadio = tk.readBoolean();
    String toolTip = tk.readIfLabel();
    tk.read(T_PAROP);

    CtComboBox box = new CtComboBox(cid, label, toolTip, asRadio);

    if (db)
      Streams.out.println(" reading ComboFields(");

    while (!tk.readIf(T_PARCL)) {
      int id = tk.readInt();
      String fldLabel = tk.readLabel();
      if (db)
        Streams.out.println(" adding id=" + id + " label=" + fldLabel);

      box.addItem(id, fldLabel);
    }
    addControl(box, toolTip);
  }

  /**
   * Process a script to add controls
   * @param script script to process
   */
  void processScript(String script) {

    //pr("ControlPanel: processScript["+script+"]");
    
    boolean db = false;

    // create an outermost panel for the script, and add it to 
    // this control panel, in the first row
    panel = new StackPanel(null);
    {
      GC gc = GC.gc(0, 0, 1, 1, 0, 0);
      gc.fill = GC.HORIZONTAL;
      add(panel.getComponent(), gc);
    }

    if (db) {
      System.out.println("processScript:\n[" + script + "]");
    }
    tk = new GadgetTokenizer(script);
    hideNextControl = false;

    panelStack = new DArray();

    processScript();
    tk.read(Token.T_EOF);
    tk = null;
  }

  private int readIdn() {
    int tabId = tk.readIfInt(-1);
    if (tabId < 0)
      tabId = C.getAnonId();
    return tabId;
  }

  private void processScript() {
    while (true) {

      if (tk.eof() || tk.peek(T_PARCL))
        break;

      tk.trace("");
      Token t = tk.read();

      switch (t.id()) {
      default:
        t.exception("unexpected token");

      case T_BUTTON:
        {
          int id = tk.readInt();
          String label = tk.readLabel();
          String toolTip = tk.readIfLabel();
          addControl(new CtButton(id, Gadget.createAction(id, label, toolTip,
              null)), null);
        }
        break;

      case T_CHECKBOX:
        {
          int id = tk.readInt();
          String label = tk.readIfLabel();

          String toolTip = tk.readIfLabel();
          boolean defValue = tk.readIfBool(false);
          addControl(new CtCheckBox(id, label, defValue, false,
              parseToolTip(toolTip), null), null);
        }
        break;

      case T_COMBOBOX:
        addComboBox();
        break;

      case T_PNL_HTAB:
        {
          if (tk.trace()) {
            System.out.println(" processing tab set");
          }
          int panelId = readIdn();

          TabbedPaneGadget tb = new TabbedPaneGadget(true, panelId);
          panel.addItem(tb.getComponent());

          for (int tabNumber = 0; !tk.readIf(T_PARCL); tabNumber++) {
            int tabId = tk.readIfInt(tabNumber);

            String tabLabel = tk.readLabel();
            pushScope(null);
            tb.addTab(tabLabel, tabId, panel.getComponent());

            tk.read(T_PAROP);
            processScript();
            tk.read(T_PARCL);
            popScope();
          }
          addControl(tb, null);
        }
        break;

      case T_PAROP:
        {
          String title = tk.readIfLabel();
          StackPanel prevScope = panel;
          panelStack.push(panel);
          panel = new StackPanel(title);
          prevScope.addItem(panel.getComponent());
          processScript();
          tk.read(T_PARCL);
          popScope();
        }
        break;

      case T_NEWCOL:
        panel.startNewColumn();
        break;

      case T_SLIDER_INT:
      case T_SLIDER_DBL:
      case T_SPIN_INT:
      case T_SPIN_DBL:
        {
          // s [<lbl:label>] <id:int> [<tooltip:label>] <min:int> <max:int> <def:int> <step:int> 
          boolean slider = (t.id(T_SLIDER_INT) || t.id(T_SLIDER_DBL));
          boolean dbl = (t.id(T_SLIDER_DBL) || t.id(T_SPIN_DBL));

          String lbl = tk.readIfLabel();
          int id = tk.readInt();
          String toolTip = tk.readIfLabel();
          double min = tk.readDouble();
          double max = tk.readDouble();
          double val = tk.readDouble();
          double step = tk.readDouble();

          addSpinner(id, lbl, min, max, val, step, slider, false, dbl, toolTip);
        }
        break;

      case T_TRACE:
        tk.setTrace(!tk.trace());
        break;

      case T_CONSOLE:
        {
          // C <id:int> <rows:int> <cols:int> <commandline:bool>
          int id = tk.readInt();
          int nRows = tk.readInt();
          int nCols = tk.readInt();
          boolean withInput = tk.readIfBool(false);
          addControl(new CtConsole(id, nRows, nCols, withInput), null);
        }
        break;

      case T_LABEL:
        {
          int colWidth = tk.readIfInt(0);
          addControl(new CtLabel(C.getAnonId(), colWidth, tk.readLabel()), null);
        }
        break;

      case T_TEXTAREA:
      case T_TEXTAREA_FW:
        {
          // xf <id:int> [<lbl:label>] 0 [<tooltip:label>] 0 <defaultValue:label> 
          int id = tk.readInt();
          String label = tk.readIfLabel();
          int i0 = tk.readInt();
          String toolTip = tk.readIfLabel();
          int i1 = tk.readInt();
          String defVal = tk.readLabel();
          addControl(new CtTextArea(id, label, SwingConstants.CENTER, defVal,
              i0, i1, t.id(T_TEXTAREA_FW)), toolTip);
        }
        break;

      case T_HIDDEN:
        hideNextControl = true;
        //  hiddenFlag.set(true);
        break;

      case T_TEXTFLD_STR_FW:
      case T_TEXTFLD_STR:
        {
          // t  [<label:label>] <id:int> [<tooltip:label>] <maxlen:int> <value:label>
          String label = tk.readIfLabel();
          int id = tk.readInt();
          String toolTip = tk.readIfLabel();
          int maxlen = tk.readInt();
          String value = tk.readLabel();
          addTextField(id, label, value, maxlen, t.id(T_TEXTFLD_STR_FW),
              toolTip);
        }
        break;

      }
    }
  }

  /**
   * Convert a tooltip string to a multi-line tooltip using
   * embedded HTML tags.
   * @param s String
   * @return String
   */
  private static String parseToolTip(String s) {
    if (s != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("<html><center>");

      DArray a = new DArray();
      TextScanner.splitString(s, 50, a);
      for (int i = 0; i < a.size(); i++) {
        if (i > 0)
          sb.append("<br>");
        String s2 = a.getString(i);
        sb.append(s2);
      }
      sb.append("</center></html>");
      s = sb.toString();
    }
    return s;
  }

  /**
   * Pop scope from stack
   * @return old scope, the one that has been replaced
   */
  private StackPanel popScope() {
    StackPanel ret = panel;
    panel = (StackPanel) panelStack.pop();
    return ret;
  }

  private StackPanel pushScope(String title) {
    panelStack.push(panel);
    panel = new StackPanel(title);
    return panel;
  }

  // stack of StackPanels
  private DArray panelStack;
  // current panel
  private StackPanel panel;
  // hide next control?
  private boolean hideNextControl;
  private GadgetTokenizer tk;
}
