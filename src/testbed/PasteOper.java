package testbed;


class PasteOper extends Undoable {
  private boolean applyOffsets;
  public PasteOper(boolean applyOffsets) {
    this.applyOffsets = applyOffsets;
    valid = !Editor.getClipboard().isEmpty();
  }

  /**
   * Constructor for undo operation
   * @param orig : CutOper to be undone
   */
  private PasteOper(PasteOper orig) {
    isUndo = !orig.isUndo;
    oppOper = orig;
  }

  public Undoable getUndo() {
    if (oppOper == null)
      oppOper = new PasteOper(this);
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
        ObjArray c = Editor.getClipboard();
        a.removeRange(a.size() - c.size(), a.size());
      } else {
        Editor.doPaste(applyOffsets);
      }
    }
  }
  private boolean isUndo;
  private PasteOper oppOper;
}
