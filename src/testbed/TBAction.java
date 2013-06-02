package testbed;

import base.*;
import java.awt.event.*;

public class TBAction {

  public static final int NONE = 0, DOWN1 = 1, UP1 = 2, DOWN2 = 3, UP2 = 4,
      DRAG = 5,
      // begin an operation (application use only)
      ENTER = 6,
      // ending an operation (application use only)
      EXIT = 7,
      // enable/disable item as required
      ITEMENABLE = 8,
      // CtButton has been pressed,
      // or valued control has changed
      CTRLVALUE = 9,
      // command issued by user in console
      COMMAND = 10,
      // mouse moving over window, but not pressed
      HOVER = 11,
      // update of application title required
      UPDATETITLE = 12,

      _UNUSED_ = 999;

  // code for action
  public int code;
  // position of mouse, in logic space (or null if not mouse related)
  public FPoint2 loc;
  // position of mouse, int view space (or null if not mouse related)
  public FPoint2 vLoc;
  // mouse event triggering action, or null (if not mouse related)
  public MouseEvent mouse;
  // id of menu (if MENUITEM)
  public int menuId;
  // id of item within menu (if MENUITEM)
  // public int itemId;
  // id of control (if NEWVALUE)
  public int ctrlId;

  // command string (if COMMAND), or menu title
  public String strArg;

  public boolean differsFrom(TBAction a) {
    boolean d = false;
    do {
      if (code != a.code) {
        d = true;
        break;
      }
      if (a.mouse == null || mouse == null) {
        break;
      }
      if (mouse.getModifiers() != a.mouse.getModifiers()) {
        System.out.println("mouseMod= " + mouse.getModifiers() + " a="
            + a.mouse.getModifiers());
        d = true;
        break;
      }
    } while (false);

    System.out.println("differs = " + d + "\n" + a + "\n" + this);
    return d;
  }

  public TBAction(int code, String strArg) {
    this.code = code;
    this.strArg = strArg;
  }

  public TBAction(int code) {
    this.code = code;
  }

  public TBAction(int code, int controlId) {
    this.code = code;
    this.ctrlId = controlId;
  }

  public TBAction(int code, int controlId, int parentId) {
    this.code = code;
    this.ctrlId = controlId;
    this.menuId = parentId;
  }

  /**
   * Constructor
   * @param code   type of action
   * @param evt    mouse event (or null if not mouse related)
   */
  protected TBAction(int code, MouseEvent evt) {
    this.code = code;

    if (evt != null) {
      this.mouse = evt;
      vLoc = new FPoint2(evt.getX(), evt.getY());
      loc = new FPoint2();
      V.viewToLogic(vLoc, loc);
    }
  }

  public void enable(boolean f) {
    C.get(ctrlId).getComponent().setEnabled(f);
  }

  /**
   * Returns true if it's a mouse event AND the ctrl key was pressed.
   * @return boolean
   */
  public boolean ctrlPressed() {
    return (mouse != null && ((mouse.getModifiers() & MyAction.modifierMask(0)) != 0));
  }

  public boolean altPressed() {
    return (mouse != null && ((mouse.getModifiers() & MyAction.modifierMask(1)) != 0));
  }

  public boolean shiftPressed() {
    return (mouse != null && ((mouse.getModifiers() & MyAction.modifierMask(3)) != 0));
  }

  private static final String[] codes = { "NONE", "DOWN1", "UP1", "DOWN2",
      "UP2", "DRAG", "ENTER", "EXIT", "ITEMENABLE", "CTRLVALUE", "COMMAND",
      "HOVER", "UPDATETITLE", };

  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("TBAction");

    sb.append(" <");
    sb.append(shiftPressed() ? 's' : '.');
    sb.append(ctrlPressed() ? 'c' : '.');
    sb.append(altPressed() ? 'a' : '.');
    sb.append(">");

    sb.append("code ");
    sb.append(code);
    if (code >= 0 && code < codes.length) {
      sb.append(' ');
      sb.append(codes[code]);
    }

    sb.append(' ');
    switch (code) {
    case ITEMENABLE:
      sb.append("[" + menuId + ":" + ctrlId);
      sb.append("]");
      break;
    case CTRLVALUE:
      sb.append(ctrlId);
      break;
    }

    return sb.toString();
  }
}
