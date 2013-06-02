package testbed;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import base.*;

class MyAction
    extends AbstractAction {

  public MyAction(String name) {
    super(name);
  }

  public void setListener(ActionListener listener) {
    this.listener = listener;
  }

  // ActionListener interface
  public void actionPerformed(ActionEvent actionEvent) {
    if (listener != null)
      listener.actionPerformed(actionEvent);
    else {
      Streams.out.println(this.getValue(Action.NAME));
      Streams.out.println(this.toString());
    }
  }


  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("MyAction: " + getValue(NAME));
    Object[] k = super.getKeys();
    for (int i = 0; i < k.length; i++) {
      String s = (String) k[i];
      if (s.equals(NAME)) {
        continue;
      }
      sb.append("\n " + Tools.f(s, 20) + ": " + super.getValue(s));
    }
    return sb.toString();
  }

  /**
   * Set key accelerator
   * @param expr : String describing KeyStroke
   */
  public void setAccelerator(String expr) {
    KeyStroke ks = parseAccel(expr);
    setAccelerator(ks);
  }
  public void setAccelerator(KeyStroke k) {
    if (k != null) {
     putValue(Action.ACCELERATOR_KEY, k);
//      Streams.out.println("set accel to: " + toString(ks));
   }
 }


  // masks for modifier keys; these are lazy-initialized according
  // to operating system
  private static int[] masks;
//  private static int[]
      static {
//    if (masks == null)
    {
      masks = new int[4];

      masks[0] = KeyEvent.CTRL_MASK;
      masks[1] = KeyEvent.ALT_MASK;
      masks[2] = KeyEvent.META_MASK;
      masks[3] = KeyEvent.SHIFT_MASK;

      int j = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      for (int k = 1; k < masks.length; k++) {
        if (masks[k] == j) {
          int tmp = masks[0];
          masks[0] = j;
          masks[k] = tmp;
          break;
        }
      }
    }
//    return masks;
  }

  /**
   * Get mask for modifier key, according to current OS
   * @param index : 0..3
   * @return int corresponding to KeyEvent.XXX_MASK or MouseEvent.XXX_MASK
   */
  public static int modifierMask(int index) {
  return masks[index];
}
  /**
   * Parse a KeyStroke object from a string description
   *
   * @param s String
   * @return KeyStroke
   */
  public static KeyStroke parseAccel(String s) {
    final boolean ds = false;
    if (ds) {
      System.out.println("parseAccel " + s);
    }
//    constructMasks();

    // pending accelerator key:
    int accelKeyCode;
    boolean shiftFlag;
    boolean ctrlFlag;
    boolean altFlag;

    accelKeyCode = 0;
    shiftFlag = false;
    ctrlFlag = false;
    altFlag = false;

    int i = 0;
    outer:for (; i < s.length(); i++) {
      char c = s.charAt(i);

      switch (c) {
        case '^':
          ctrlFlag = true;
          break;
        case '&':
          altFlag = true;
          break;
        case '@':
          shiftFlag = true;
          break;
        case '#':
          accelKeyCode = Integer.parseInt(s.substring(i + 1));
          i = s.length();
          break outer;

        default:
          if (Character.isUpperCase(c)) {
            shiftFlag = true;
          }
          accelKeyCode = Character.toUpperCase(c);
          i++;
          break outer;
      }
    }
    Tools.ASSERT(accelKeyCode != 0 && i == s.length(),
                 "parse accelerator problem: " + s);

    KeyStroke k = null;
    if (accelKeyCode != 0) {
      k = KeyStroke.getKeyStroke(accelKeyCode,
                                 (ctrlFlag ? modifierMask(0) : 0)
                                 |
                                 (altFlag ? modifierMask(1) : 0)
                                 |
                                 (shiftFlag ? modifierMask(3) : 0));
    }
    if (ds) {
      System.out.println(" code=" + accelKeyCode + " s=" + shiftFlag + " a=" +
                         altFlag +
                         " c=" + ctrlFlag + "\n k=" + k);
    }
    return k;
  }

  /**
   * Return string representation of keystroke, for display
   * in tooltip
   * @param k KeyStroke
   * @return String
   */
  public static String toString(KeyStroke k) {
    StringBuilder sb = new StringBuilder();

    if (false) {
      //   System.out.println("KeyStroke toString: "+k);
      sb.append(k.toString());
      return sb.toString();
    }

    final int[] mf = {
        ActionEvent.CTRL_MASK,
        ActionEvent.ALT_MASK,
        ActionEvent.SHIFT_MASK,
        ActionEvent.META_MASK,
    };
    final String[] mn = {
        "Ctrl", "Alt", "Shift", "Meta", };

    int m = k.getModifiers();
    for (int i = 0; i < mf.length; i++) {
      if ( (m & mf[i]) == 0) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append('+');
      }
      sb.append(mn[i]);
    }
    if (sb.length() > 0) {
      sb.append(' ');
    }

    int code = k.getKeyCode();
    sb.append( (char) code);
    return sb.toString();
  }

  /**
   * Set tooltip text for action
   * @param s : string, it is wrapped in <html>...</html> tags
   */
  public void setTooltipText(String s, boolean wrapInHTML) {
    StringBuilder sb = new StringBuilder();
    if (wrapInHTML)
    sb.append("<html><center>");
    sb.append(s);
    KeyStroke k = (KeyStroke) getValue(ACCELERATOR_KEY);
    if (k != null) {
      sb.append(" (");
      sb.append(toString(k));
      sb.append(")");
    }
    if (wrapInHTML)
    sb.append("</html>");

    putValue(SHORT_DESCRIPTION, sb.toString());
  }

  private ActionListener listener;
}
