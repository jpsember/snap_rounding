package testbed;

import base.*;

 class ChangeItemsOper extends Undoable {

  private static final boolean db = false;

  public ChangeItemsOper(EdObjectFactory objType, boolean selectedOnly, boolean skipInactive) {
    this.selectedOnly = selectedOnly;
    itemSlots = ObjArrayUtil.getItemSlots(objType,
        Editor.getItems(), selectedOnly,skipInactive);
    if (db)
      Streams.out.println("constructed: " + this);
  }

  private ChangeItemsOper() {
  }

  public Undoable getUndo() {
    if (oppOper == null) {
      oppOper = new ChangeItemsOper();
      oppOper.isUndo = true;
      oppOper.selectedOnly = selectedOnly;
      oppOper.itemSlots = this.itemSlots;
      if (!(isUndo && selectedOnly))
        oppOper.itemSlots = ObjArrayUtil.getItemSlots(null,
            Editor.getItems(),
            selectedOnly, false);
      oppOper.items = new ObjArray(Editor.getItems(), oppOper.itemSlots, true);
      if (db)
        Streams.out.println("getUndo, constructed: " + oppOper);
    }

    return oppOper;
  }

  /**
   * Replace items in rec.originalSlots with rec.items;
   * return an operation that restores these items
   */
  public void perform() {
    if (db)
      Streams.out.println("perform ChangeItemsOper, undo=" + isUndo);
    if (isUndo) {
      if (selectedOnly) {
        if (db)
          Streams.out.println("replacing selected items:\n" + items()
              + "\n slots=" + itemSlots);

        ObjArrayUtil.replaceSelectedObjects(items, itemSlots, true);
      } else {
        if (db)
          Streams.out.println(" replacing all items with " + items);

        ObjArray e = Editor.getItems();
        e.clear();
        e.addAll(items);
      }
    }
  }

  public ObjArray items() {
    return items;
  }
  public DArray itemSlots() {
    return itemSlots;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ChangeItemsOper");
    sb.append(" items=" + items);
    sb.append(" itemSlots=" + itemSlots);
    sb.append(" isUndo=" + isUndo);
    return sb.toString();
  }
  private ObjArray items;
  private boolean selectedOnly;
  private DArray itemSlots;
  private boolean isUndo;
  private ChangeItemsOper oppOper;
}
