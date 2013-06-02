package testbed;

import base.*;

/**
 * Undoable procedure 
 */
abstract class Undoable {

  /**
   * Get a procedure that will undo this procedure
   * @return Undoable object
   */
  public abstract Undoable getUndo();

  /**
   * Determine if this procedure is 'valid'; i.e., can be performed
   * @return
   */
  public boolean valid() {
    return valid;
  }

  public static String toString(Undoable u) {
    StringBuilder sb = new StringBuilder();
    sb.append(u.getClass().getSimpleName());
    Tools.tab(sb, 24);
    sb.append("V:" + Tools.f(u.valid));
    return sb.toString();
  }

  /**
   * Perform this operation
   */
  public abstract void perform();

//  /**
//   * Get items being changed by this operation
//   */
//  public ObjArray items() {
//    return items;
//  }
//
//  /**
//   * Set items being changed by this operation
//   * @param items
//   * @deprecated
//   */
//  public void setItems(ObjArray items) {
//    this.items = items;
//  }
//
//  // items affected by operation
//  /**
//   * @deprecated
//   */
//  protected ObjArray items;

  public boolean isValid() {
    return valid;
  }
  protected boolean valid = true;
}
