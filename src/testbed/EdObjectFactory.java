package testbed;

import base.*;

public abstract class EdObjectFactory {

  /**
   * Utility function for writing object to file:
   * Add FPoint2 to StringBuilder, with spaces as necessary
   * @param sb StringBuilder to add to
   * @param pt point
   */
  public static void toString(StringBuilder sb, FPoint2 pt) {
    sb.append(" ");
    toString(sb, pt.x);
    toString(sb, pt.y);
  }

  /**
   * Utility function for writing object to file:
   * Add double value to StringBuilder, with spaces as necessary
   * @param sb StringBuilder to add to
   * @param v value
   */
  public static void toString(StringBuilder sb, double v) {
    sb.append(Tools.f(v));
  }

  /**
   * Utility function for writing object to file:
   * Add integer value to StringBuilder, with spaces as necessary
   * @param sb StringBuilder to add to
   * @param v value
   */
  public static void toString(StringBuilder sb, int v) {
    sb.append(Tools.f(v));
  }

  /**
   * Utility function for writing object to file:
   * Add boolean flag to StringBuilder, with spaces as necessary
   * @param sb StringBuilder to add to
   * @param f boolean flag
   */
  public static void toString(StringBuilder sb, boolean f) {
    sb.append(' ');
    sb.append(Tools.f(f));
  }

  /**
   * Get name of this object.  This is an identifier that is written
   * to text files to identify this object.
   * @return String
   */
  public abstract String getTag();

  /**
   * Get editor menu text for adding items of this type 
   * @return text to put in menu label, or null if user can't add these types.
   *   Specify keyboard equivalent by prefixing with "!... ".
   */
  public abstract String getMenuLabel();

  /**
   * Get key equivalent to associate with menu item.  This
   * is usually a single character, e.g. 'v' or 's'.  The editor 
   * may require the alt / option key to accompany this keypress.
   * @return key equivalent, or null if it has none
   */
  public abstract String getKeyEquivalent();

  /**
   * Construct an EditObj of this type.  Used when user wants to add a new
   * object in the editor.
   * @return EditObj
   */
  public abstract EdObject construct();

  /**
   * Parse EditObj from scanner
   * @param s : TextScanner
   * @return EditObj
   */
  public abstract EdObject parse(Tokenizer s, int flags);

  /**
   * Write EditObj in format suitable for parsing
   * @param sb : where to store text
   * @param obj : EditObj to write
   */
  public abstract void write(StringBuilder sb, EdObject obj);

//  /**
//   * Write attributes as text, for saving to file;
//   * assumes tag, flags have already been written.
//   * @param sb destination 
//   * @param obj object to write
//   */
//  public final void toString(StringBuilder sb, EdObject obj) {
//  }
}
