package testbed;

import base.*;

class ObjArrayUtil {

  private static final boolean db = false;

  /**
   * Delete specific items 
   * @param itemSlots : indexes of items to delete
   */
  public static void deleteItems(DArray itemSlots) {
    if (db)
      Streams.out.println("deleteItems " + itemSlots);

    int[] ord = sortedOrder(itemSlots);
    for (int i = ord.length - 1; i >= 0; i--) {
      int j = ord[i];
      if (db)
        Streams.out.println(" removing j=" + j + ", slot "
            + itemSlots.getInt(j));
      Editor.getItems().remove(itemSlots.getInt(j));
    }
  }

  /**
   * Add items
   * @param itemSlots : final indexes of items
   * @param items : items to add
   */
  public static void addItems(DArray itemSlots, ObjArray items,
      boolean setSelected) {
    if (db)
      Streams.out.println("addItems slots=" + itemSlots);

    int[] ord = sortedOrder(itemSlots);
    for (int i = 0; i < ord.length; i++) {
      int j = ord[i];
      if (db)
        Streams.out.println(" inserting item j=" + j + ", slot="
            + itemSlots.getInt(j));
      EdObject obj = items.obj(j);
      if (setSelected)
        obj.setSelected(true);

      Editor.getItems().add(itemSlots.getInt(j), obj);
    }
  }
  /**
   * Determine indexes of values in sorted order
   * @param values : DArray of ints
   * @return indexes of values sorted into increasing order
   */
  private static int[] sortedOrder(DArray values) {
    int[] ret = new int[values.size()];
    for (int i = 0; i < ret.length; i++)
      ret[i] = i;
    for (int i = 0; i < ret.length; i++) {
      for (int j = i + 1; j < ret.length; j++) {
        if (values.getInt(i) > values.getInt(j)) {
          values.swap(i, j);
          int tmp = ret[i];
          ret[i] = ret[j];
          ret[j] = tmp;
        }
      }
    }
    if (db) {
      Streams.out.println("sortedOrder for\n"
          + DArray.toString(values.toIntArray()) + "\n is\n"
          + DArray.toString(ret));
    }

    return ret;
  }

  /**
   * Get DArray containing item slots
   * @param source : ObjArray to examine
   * @param selectedOnly : if true, returns only slots of selected items;
   *  else, every item (i.e. 0...n-1)
   * @return DArray of slots
   */
   static DArray getItemSlots(EdObjectFactory objType, ObjArray source,
      boolean selectedOnly, boolean skipInactive) {
//    Tools.warn("what to do about incomplete objects?");
    DArray ret = new DArray();
    for (int i = 0; i < source.size(); i++) {
      EdObject obj = source.obj(i);
      if (objType != null && obj.getFactory() != objType)
        continue;
      if (skipInactive && !obj.isActive())
        continue;
      if (selectedOnly && !obj.isSelected())
        continue;
      if (!obj.complete()) {
        Tools.warn("skipping incomplete objects");
        continue;
      }
      ret.addInt(i);
    }
    return ret;
  }

  /**
   * Replace selected EditObjects within this array with those from another
   * @param source : source ObjArray
   * @param slots : where to store each source object in this array
   * @param setSelected : if true, sets each object's highlighted flag
   */
  public static void replaceSelectedObjects(ObjArray source, DArray slots,
      boolean setSelected) {
    for (int i = 0; i < slots.size(); i++) {
      EdObject obj = source.obj(i);
      if (setSelected)
        obj.setSelected(true);
      Editor.getItems().set(slots.getInt(i), obj);
    }
  }
}
