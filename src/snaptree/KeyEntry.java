package snaptree;

/**
 * Class for elements in BTree nodes.
 *
 * These are pairs: <ptr, item> where item is the user-supplied object to
 * be stored in the tree
 *
 * For internal (index) nodes, the ptr is a BPage;
 * for leaf nodes, the ptr is null
 */
public class KeyEntry {

  public KeyEntry(BPage ptr, Handle handle) {
    this.ptr = ptr;
    this.handle = handle;

    if ( isLeaf() && (ptr != null || item() == null) ) {
      throw new BTreeException("attempt to store null item, or BPage ptr, in leaf node");
    }
  }

  /**
   * Constructor
   */
  public KeyEntry(BPage ptr, Item item) {
    this(ptr, handleFor(item));
  }

  /**
   * Deep copy constructor
   * @param src KeyEntry
   */
  public KeyEntry(KeyEntry src) {
    this(src.ptr, src.handle);
  }

  public BPage ptr() {
    return ptr;
  }

  public boolean isLeaf() {
    return ptr == null;
  }

  public Handle handle() {
    return handle;
  }

  public Item item() {
    Item out = null;
    if (handle != null) {
      out = handle.getItem();
    }
    return out;
  }

  private static Handle handleFor(Item item) {
    Handle out = null;
    if (item != null) {
      out = item.getHandle();
    }
    return out;
  }

  public KeyEntry modify(BPage newPtr) {
    return new KeyEntry(newPtr, handle);
  }

  public KeyEntry modify(Item newData) {
    return new KeyEntry(ptr, handleFor(newData));
  }

  public KeyEntry modify(Handle newHandle) {
    return new KeyEntry(ptr, newHandle);
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    return toString(false);
  }

  public String toString(boolean keyOnly) {
    StringBuffer sb = new StringBuffer();
    if (!keyOnly) {
      sb.append("(");
      sb.append(handle);
    }
    else {
      sb.append("" + item());
    }
    if (!keyOnly) {
      if (!isLeaf())
      sb.append("/" + ptr);
      sb.append(")");
    }
    return sb.toString();
  }

  private final BPage ptr;
  private final Handle handle;
}
