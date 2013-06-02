package testbed;

import base.*;

class CopyOper extends Undoable {

  public CopyOper() {
    itemSlots = Editor.getSelectedItemInd();
    valid = !itemSlots.isEmpty();
  }

  /**
   * Constructor for undo operation
   * @param orig : CopyOper to be undone
   */
  private CopyOper(CopyOper orig) {
    isUndo = !orig.isUndo;
    oppOper = orig;

    this.itemSlots = oppOper.itemSlots;
    // construct copy of modified items
    this.items = new ObjArray(Editor.getItems(), itemSlots, true) ;

    // save clipboard
    this.savedClipboard = Editor.getClipboard();
  }

  public Undoable getUndo() {
    if (oppOper == null)
      oppOper = new CopyOper(this);
    return oppOper;
  }

  /**
   * Replace items in rec.originalSlots with rec.items;
   * return an operation that restores these items
   */
  public void perform() {
    if (valid) {
      if (isUndo) {
        Editor.setClipboard(savedClipboard);
      } else {
        // undo operation constructs modified items for us
        getUndo();
        Editor.setClipboard(oppOper.items);
      }
    }
  }

  public DArray itemSlots() {
    return itemSlots;
  }
  private DArray itemSlots;
  private ObjArray savedClipboard;
  private boolean isUndo;
  private CopyOper oppOper;
  private ObjArray items;
}
