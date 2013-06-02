package testbed;

import base.*;

class CutOper extends Undoable {

  public CutOper() {
    itemSlots = Editor.getSelectedItemInd();
    valid = !itemSlots.isEmpty();
  }

  private boolean isUndo;
  private CutOper oppOper;

  /**
   * Constructor for undo operation
   * @param orig : CutOper to be undone
   */
  private CutOper(CutOper orig) {
    isUndo = !orig.isUndo;
    oppOper = orig;

    this.itemSlots = oppOper.itemSlots;
    // construct copy of modified items
    this.items = new ObjArray(Editor.getItems(), itemSlots, true);

    // save clipboard
    this.savedClipboard = Editor.getClipboard();
  }

  public Undoable getUndo() {
    if (oppOper == null)
      oppOper = new CutOper(this);
    return oppOper;
  }

  /**
   * Replace items in rec.originalSlots with rec.items;
   * return an operation that restores these items
   */
  public void perform() {
    if (valid) {
      if (isUndo) {
        ObjArrayUtil.addItems(itemSlots, this.items, true);
        Editor.setClipboard(savedClipboard);
      } else {
        // undo operation constructs modified items for us
        getUndo();
        Editor.setClipboard(oppOper.items);
        ObjArrayUtil.deleteItems(itemSlots);
      }
    }
  }

  public DArray itemSlots() {
    return itemSlots;
  }
  private DArray itemSlots;
  private ObjArray savedClipboard;
  private ObjArray items;
}
