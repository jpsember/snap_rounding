package testbed;

import base.*;

/**
 * Maintain ordered sets of EditObj'ects.
 */
class ObjArray extends DArray {

  public EdObject obj(int n) {
    EdObject r = (EdObject) get(n);
    //    Streams.out.println("ObjArray.obj(" + n + ")=" + r.getFactory());
    return r;
  }
  public ObjArray() {
  }
  public ObjArray(DArray a) {
    this(a.size());
    addAll(a);
  }
  public ObjArray(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Constructor.  Constructs an ObjArray containing copies of particular
   * items from another ObjArray.
   * @param items : source ObjArray
   * @param itemNumbers : Integers containing slots to copy
   */
  public ObjArray(ObjArray items, DArray itemNumbers, boolean makeClones) {
    super(itemNumbers.size());
    for (int i = 0; i < itemNumbers.size(); i++) {
      EdObject obj = items.obj(itemNumbers.getInt(i));
      if (makeClones) {
        EdObject obj2 = (EdObject) obj.clone();
        obj = obj2;
      }
      add(obj);
    }
  }
  /**
   * Constructor.  Constructs an ObjArray containing copies of all the items 
   *   from another ObjArray.
   * @param items : source ObjArray
   */
  public ObjArray(ObjArray items) {
    for (int i = 0; i < items.size(); i++) {
      add(items.obj(i).clone());
    }
  }

  //  /**
  //   * Get a list of the selected objects
  //   * @deprecated use ObjArrayUtil
  //   * @return
  //   */
  //  public DArray getSelectedObjects() {
  //    DArray a = new DArray();
  //    for (int i = 0; i < size(); i++) {
  //      EditObj obj = obj(i);
  //      if (!obj.isSelected())
  //        continue;
  //      a.addInt(i);
  //    }
  //    return a;
  //  }
  //
}
