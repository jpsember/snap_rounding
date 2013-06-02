package testbed;

import base.*;
import static base.Tools.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*;

/*
 EBNF for menu scripts

 <script>   ::= ( <menu> | <nextid:num> )*
 <menu>     ::= '(' <menuname:label> <menuarg>* ')'
 <menuarg> ::=
  | '<' <itemarg>* '>'         Defines radio buttons
  | <menu>                     Nested menus
  | <itemarg>                  Menu item or arguments

 <itemarg>  ::=
  | <nextid:num>
  | <applicationflag>
  | <accel>
  | 'r' <item:label> [<value:bool>]
  | 'c' <item:label> [<value:bool>]
  | <item:label>
  | <sep>

 <sep>      ::= '='

 <accel> defines the keyboard accelerator.  The prefix '^' denotes ctrl,
 '&' denotes alt, and if the character is in upper case, the Shift key
 will also be required.  If the key is not a letter or digit, use
 #dddd to specify the VK_xxx value from KeyEvent.java.

 */

class MenuPanel extends JMenuBar implements Globals, ActionListener,
    MenuListener, IScript {

  /**
   * Constructor
   * @param gList : gadget list (shared with ControlPanel)
   * @param frame : JFrame to add menu bar to (if not an applet)
   */
  public MenuPanel(JFrame frame) {

    // if running as an application, add a menu bar to
    // the application frame.  If as an applet, we will
    // use 'this' pointer.

    if (!Streams.isApplet()) {
      frame.setJMenuBar(this);
      this.setMinimumSize(new Dimension(100, 20));
    }
  }

  /**
   * Process a script to add a sequence of menus
   * @param script String with EBNF format described above
   */
  public void processScript(String script) {
    
    
    t = new GadgetTokenizer(script);
    skipFlags = new DArray();
    skipFlags.pushBoolean(false);

    //    initRadioGroup();

    nextId = 0;
    while (true) {
      Token tk = t.read();
      if (tk.eof()) {
        break;
      }

      switch (tk.id()) {
      case T_TRACE:
        t.setTrace(!t.trace());
        break;
      case T_INT:
        nextId = t.readInt(tk);
        break;
      default:
        parseMenu(0);
        break;
      }
    }
    t = null;
    displayChanges();
  }

  private boolean consumeSkip() {
    boolean f = skipFlags.lastBoolean() && Streams.isApplet();
    if (f) {
      replaceSkip(true);
    }
    return f;
  }

  private void replaceSkip(boolean v) {
    skipFlags.pop();
    skipFlags.pushBoolean(v);
  }

  /**
   * Calculate next id and increment; error if no nextId defined
   * @return id
   */
  private int useID() {
    Tools.ASSERT(nextId != 0, "Id is missing");
    int id = nextId++;
    return id;
  }

  /**
   * Construct a new menu gadget, and add to menu bar (or as submenu
   *  to existing menu)
   * @param parentId : id of parent menu, if submenu; else 0
   * @param id : id of new menu
   * @param name : name of menu
   */
  private void createMenu(int parentId, int id, String name) {
    final boolean db = false;
    if (db) {
      System.out.println("createMenu parent=" + parentId + " id=" + id
          + " name=" + name);
    }

    // create a new menu gadget
    CtMenu c = CtMenu.newMenu(id, name);
    C.list.add(c);

    if (db) {
      System.out.println(" gadget=" + c);
    }

    // add as submenu to existing menu?

    if (parentId > 0) {
      CtMenu cParent = get(parentId);
      JMenu cp = cParent.getMenu();
      if (db) {
        System.out.println(" adding JMenu " + c.getMenu().getText());
      }
      cp.add(c.getMenu());
      cParent.addChildItem(id);
    } else {
      if (db) {
        System.out.println(" adding to menubar, " + c.getMenu());
      }
      add(c.getMenu());
    }
    c.getMenu().addMenuListener(this);
  }

  private CtMenu get(int id) {
    return (CtMenu) C.list.get(id);
  }

  /**
   * Parse <menu>
   * @param parentMenuId : id of parent menu, if it's a submenu; or 0
   */
  private void parseMenu(int parentMenuId) {

    final boolean db = false;

    boolean skip = skipFlags.lastBoolean();

    if (db) {
      Streams.out.println("parseMenu parent=" + parentMenuId + " skip=" + skip);
    }

    skipFlags.pushBoolean(skip);
    //    ControlPanel.pushSkipLevel();

    if (verbose) {
      System.out.println("parseMenu parent=" + parentMenuId);
    }

    String menuName = t.readLabel();
    int id = useID();

    if (db) {
      Streams.out.println(" menuName=" + menuName + " id=" + id);
    }

    if (!skip) {
      createMenu(parentMenuId, id, menuName);
    }

    while (!t.readIf(T_PARCL)) {
      if (verbose) {
        System.out.println("...parsing next menu arg, peek=" + t.peek());
      }
      if (db) {
        Streams.out.println(" peek=" + t.peek());
      }

      parseMenuArg(id);
    }
    skipFlags.popBoolean();
    consumeSkip();
  }

  private void parseMenuArg(int menuId) {

    final boolean db = false;

    if (db) {
      Streams.out.println(" pmarg mid=" + menuId + " peek=" + t.peek());
    }

    if (verbose) {
      System.out.println("parseMenuArg");
    }

    if (t.readIf(T_PAROP)) {
      if (verbose || db) {
        System.out.println("...parsing nested menu for parent " + menuId);
      }
      parseMenu(menuId);
    } else {
      parseItemArg(menuId, false);
    }
  }

  private void parseItemArg(int menuId, boolean rbFlag) {

    final boolean db = false;
    if (db)  
      pr(" pitem mid=" + menuId + " rb=" + rbFlag + " peek="
          + t.peek());
    if (verbose) {
      System.out.println("parseItemArg menuId=" + menuId + " rbFlag=" + rbFlag);
    }

    if (t.peek(T_INT)) {
      nextId = t.readInt();
      if (db) {
        Streams.out.println("  nextId=" + nextId);
      }
      if (verbose) {
        System.out.println("nextId=" + nextId);
      }
    } else {
      Token tk = t.read();
      switch (tk.id()) {
      case T_ACCEL:
        // skip the '!' which marks token as accelerator
        nextAccel = MyAction.parseAccel(tk.text().substring(1));
        break;
      case T_SEP:
        if (!consumeSkip()) {
          addSeparator(menuId);
        }
        break;
      default:
        {
          int id = useID();
          String lbl = t.readLabel(tk);
          if (!consumeSkip()) {
            addItem(menuId, id, lbl);
          }
        }
        break;
      }
    }
  }

  private static KeyStroke getAccelerator() {
    KeyStroke k = nextAccel;
    nextAccel = null;
    return k;
  }
  // pending accelerator key:
  private static KeyStroke nextAccel;

  private void addSeparator(int menuId) {
    if (verbose) {
      System.out.println("addSeparator");
    }
    get(menuId).getMenu().addSeparator();
  }

  /**
   * Add an item to a menu
   *
   * @param menuId : id of menu
   * @param itemId : id to assign to menu item
   * @param label : text of menu item
   */
  private void addItem(int menuId, int itemId, String label) {
    if (verbose) {
      System.out.println("addItem m=" + menuId + " i=" + itemId + " lbl="
          + label);
    }
    CtMenu c = CtMenu.newItem(menuId, itemId, label);
    CtMenu p = get(menuId);
    p.addChildItem(itemId);

    addAccelerator(c);
    C.list.add(c);
    get(menuId).getMenu().add(c.getItem());
  }

  private void addAccelerator(Gadget item) {
    JMenuItem c = null;
    c = ((CtMenu) item).getItem();
    KeyStroke k = getAccelerator();
    if (k != null) {
      c.setAccelerator(k);
    }
    c.addActionListener(this);
  }

  /**
   * Update menu bar to reflect changes due to insertion/deletion of
   * menu
   */
  private void displayChanges() {
    validate();
    repaint(100);
  }

  /**
   * Remove a menu.  It must be a top-level menu, not a submenu.
   * @param menuId : id of menu
   */
  public void removeMenu(int menuId) {
    removeMenuItem(0, menuId);
    displayChanges();
  }

  /**
   * Remove a menu or menu item gadget and any submenus
   * @param parent : id of menu containing this item, or 0 if topmost
   * @param id : id of item to be removed
   */
  private void removeMenuItem(int parent, int id) {
    final boolean db = false;
    if (db) {
      System.out.println("removeMenuItem, gadget " + id);
    }
    Gadget g = C.get(id);
    if (db) {
      System.out.println(" gadget=" + g);
    }
    if (g instanceof CtMenu) {
      CtMenu m = (CtMenu) g;
      // remove any child gadgets
      for (int i = m.nChildren0() - 1; i >= 0; i--) {
        removeMenuItem(id, m.child0(i));
      }
    }

    // remove actions associated with gadget
    //  g.destroyAction(id);
    // remove gadget from list of gadgets
    C.list.free(id);

    if (parent != 0) {
      CtMenu p = get(parent);
      p.getMenu().remove(g.getComponent());
    } else {
      remove(g.getComponent());
    }
  }

  // ------------------------------------------------------
  // MenuListener interface.  Used to enable items when menu
  // is displayed.
  // ------------------------------------------------------
  public void menuSelected(MenuEvent evt) {
    final boolean db = false;
    if (db) {
      System.out.println("MenuPanel.menuSelected " + evt);
    }
    // process menu actions for every item in the menu
    int id = ((GadgetComponent) evt.getSource()).getGadget().getId();

    Gadget g = get(id);
    if (db) {
      System.out.println(" gadget = " + g);
    }
    if (g instanceof CtMenu) {
      CtMenu c = (CtMenu) g;
      if (db) {
        System.out.println(" examining children of " + c);
      }
      for (int j = 0; j < c.nChildren0(); j++) {
        int child = c.child0(j);
        TestBed.procAction(new TBAction(TBAction.ITEMENABLE, child, id));
      }
    }
  }

  public void menuDeselected(MenuEvent evt) {
  }

  public void menuCanceled(MenuEvent evt) {
  }

  // ------------------------------------------------------
  // ActionListener interface
  // ------------------------------------------------------
  public void actionPerformed(ActionEvent e2) {
    final boolean db = false;
    if (db)
      System.out.println("MenuPanel actionPerformed " + e2);
    TBAction a = null;
    Gadget c = ((GadgetComponent) e2.getSource()).getGadget();
    Gadget g = C.list.get(c.getId());
    if (g instanceof CtMenu) {
      CtMenu cm = (CtMenu) g;
      a = new TBAction(TBAction.CTRLVALUE, cm.getId());
    }
    TestBed.procAction(a);
  }

  // ------------------------------------------------------

  //  public void test() {
  //    processScript(" 80 ( "
  //        + " 'File and whatnot' "
  //        + "100 !&d 'open' 'close' = 'save' 'quit' "
  //        + " )"
  //        + " 90 ( "
  //        + " 'engineering' 200  < !^F r 'engines' T r 'wheels' r 'suspension' > "
  //        + " c 'checkbox' " + " ) ");
  //  }

  // next id to use, or 0 if none defined
  private int nextId;
  // verbose operation
  private boolean verbose;
  private GadgetTokenizer t;
  private DArray skipFlags;
}
