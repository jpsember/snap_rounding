package testbed;

import base.*;

class AddObjectsOper extends Undoable {

  private static final boolean db = false;

  public AddObjectsOper(EdObject obj) {
    ObjArray newObj = new ObjArray();
    newObj.add(obj);
    construct(null, newObj);
  }
  public AddObjectsOper(DArray slots, ObjArray newItems) {
    construct(slots, newItems);
  }

  private AddObjectsOper(AddObjectsOper orig) {
    this.isUndo = !orig.isUndo;
    this.oppOper = orig;
  }

  private void construct(DArray slots, ObjArray newItems) {
    if (slots == null) {
      slots = new DArray();
      for (int i = 0; i < newItems.size(); i++)
        slots.addInt(i + Editor.getItems().size());
    }
    this.itemSlots = slots;
    this.items = newItems;

    if (db)
      Streams.out.println("constructed:\n" + this);

  }
  public void perform() {
    if (db)
      Streams.out.println("AddObjectsOper.perform:\n" + this);

    if (!isUndo)
      ObjArrayUtil.addItems(itemSlots, items, true);

    else
      ObjArrayUtil.deleteItems(oppOper.itemSlots);
  }

  public Undoable getUndo() {
    if (oppOper == null)
      oppOper = new AddObjectsOper(this);
    return oppOper;

  }
  private AddObjectsOper oppOper;
  private boolean isUndo;
  private DArray itemSlots;
  private ObjArray items;
}
