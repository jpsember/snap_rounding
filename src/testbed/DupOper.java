package testbed;

import base.*;

class DupOper extends Undoable {
  public DupOper() {
    slots = Editor.getSelectedItemInd();
    valid = !slots.isEmpty();
  }

  /**
   * Constructor for undo operation
   * @param orig : CutOper to be undone
   */
  private DupOper(DupOper orig) {
    isUndo = !orig.isUndo;
    oppOper = orig;
  }

  public Undoable getUndo() {
    if (oppOper == null)
      oppOper = new DupOper(this);
    return oppOper;
  }

  /**
   * Replace items in rec.originalSlots with rec.items;
   * return an operation that restores these items
   */
  public void perform() {
    if (valid) {
      if (isUndo) {
        ObjArray a = Editor.getItems();
        a.removeRange(a.size() - oppOper.slots.size(), a.size());
        Editor.setSelectedItems(oppOper.slots);
      } else {
        Editor.performDup(slots);
      }
    }
  }
  private DArray slots;
  private boolean isUndo;
  private DupOper oppOper;
}
