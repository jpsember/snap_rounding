package testbed;

import base.*;

class AdjustOrderOper extends Undoable {

  private static final boolean db = false;

  public void perform() {
    if (!valid)
      throw new IllegalStateException();

    if (db)
      Streams.out.println("origSlots=" + originalSlots + "\n finalSlots="
          + finalSlots);

    moveItems(Editor.getItems(), originalSlots, finalSlots);
  }

  private static void moveItems(ObjArray items, DArray origSlots,
      DArray finalSlots) {

    if (db)
      Streams.out.println("moveItems\n origSlots= " + origSlots
          + "\n finalSlots=" + finalSlots);

    boolean[] itemMovedFlags = new boolean[items.size()];
    DArray newItems = new ObjArray(items.size());
    for (int i = 0; i < origSlots.size(); i++) {
      int initSlot = origSlots.getInt(i);
      int finalSlot = finalSlots.getInt(i);
      itemMovedFlags[initSlot] = true;
      newItems.growSet(finalSlot, items.get(initSlot));
    }
    int k = 0;
    for (int i = 0; i < itemMovedFlags.length; i++) {
      if (itemMovedFlags[i])
        continue;
      // advance to next unclaimed slot
      while (newItems.exists(k) && newItems.get(k) != null)
        k++;
      newItems.growSet(k, items.get(i));
    }

    // replace old items with rearranged versions
    items.clear();
    items.addAll(newItems);
  }

  public Undoable getUndo() {
    return new AdjustOrderOper(finalSlots, originalSlots);
  }

  private AdjustOrderOper(DArray originalSlots, DArray finalSlots) {
    this.originalSlots = originalSlots;
    this.finalSlots = finalSlots;
  }

  public AdjustOrderOper(int distance) {
    do {
      valid = false;
      originalSlots = Editor.getSelectedItemInd();
      if (originalSlots.isEmpty())
        break;

      ObjArray items = Editor.getItems();

      int first = originalSlots.getInt(0);
      int nonMoving = items.size() - originalSlots.size();

      // determine final slot for first moved item
      long destIndL = first + distance;
      if (destIndL < 0)
        destIndL = 0;
      if (destIndL > nonMoving)
        destIndL = nonMoving;
      int destInd = (int) destIndL;

      finalSlots = new DArray();
      for (int i = 0; i < originalSlots.size(); i++) {
        int finalSlot = destInd + i;
        if (finalSlot != originalSlots.getInt(i))
          valid = true;
        finalSlots.addInt(finalSlot);
      }
      if (!valid)
        break;
    } while (false);
  }

  private DArray finalSlots;
  private DArray originalSlots;
}
