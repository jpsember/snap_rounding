package snaptree;

import base.*;
import testbed.*;

/**
 * Class for manipulating pages (either index or leaf pages)
 * 
 * Each page is one of these types:
 *  [] index page 
 *      Has n values and n-1 items; values are page ids 
 *  [] leaf page
 * Has n items
 */
public class BPage {

  KeyEntry popFirst() {
    KeyEntry out = firstEntry();
    deleteEntries(0, 1);
    return out;
  }

  KeyEntry popLast() {
    KeyEntry out = getKey(nKeys() - 1);
    deleteEntries(nKeys() - 1, 1);
    return out;
  }

  public int getId() {
    return id;
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  boolean underFull() {
    return nKeys < maxKeys() / 2;
  }

  boolean moreThanHalfFull() {
    return nKeys > maxKeys() / 2;
  }

  boolean full() {
    return nKeys == maxKeys();
  }

  int maxKeys() {
    return isLeaf() ? tree.maxKeysPerLeafPage() : tree.maxKeysPerIndexPage();
  }

  /**
   * Find point of insertion for an item
   * 
   * Keep in mind that index pages contain a 'right side' entry, which should
   * not be tested.
   * 
   * @param ent :
   *          item to insert, or null for minimum item in tree
   * @param prevFlag :
   *          if true, searches for position strictly before the key
   * @return int : index of first key with key >= input key, or key > input key
   *         if prevFlag set
   */
  int findInsertionPoint(Object ent0, boolean prevFlag) {

    final boolean db = false;

    if (db && T.update())
      T.msg("findInsertionPoint obj=" + ent0 + " prevFlag=" + prevFlag
          + " page=" + this);
    // perform a binary search.
    int min = 0, max = nKeys() - 1;

    // adjust for right-side entry
    if (!isLeaf()) {
      max--;
    }

    while (true) {
      if (db && T.update())
        T.msg(" min=" + min + " max=" + max);
      if (min > max) {
        break;
      }
      int test = (min + max) >> 1;

      KeyEntry ent2 = getKey(test);

      int res = tree.compareItems(ent0, ent2.item());

      if (res == 0 && !prevFlag) {
        // if this is an index page, we want to treat it as if
        // our key is higher.
        if (!isLeaf()) {
          min = test + 1;
          continue;
        }
        min = test;
        break;
      }
      if (res <= 0) {
        max = test - 1;
      } else {
        min = test + 1;
      }
    }
    if (db && T.update())
      T.msg(" returning " + min);
    return min;
  }

  /**
   * Get entry within page
   * 
   * @param keyIndex :
   *          index of key (0..n-1)
   * @return int
   */
  public KeyEntry getKey(int keyIndex) {
    return keys[keyIndex];
  }

  BPage rightEntryPage() {
    return lastEntry().ptr();
  }

  /**
   * Store a KeyEntry within the page.
   * 
   * If necessary, converts KeyEntry so its leaf status matches the page.
   * 
   * @param position
   *          int
   * @param key
   *          KeyEntry
   */
  void setKey(int position, KeyEntry key) {
    keys[position] = key;

    Item item = key.item();
    if (item != null && this.isLeaf())
      item.setPage(this.id);

    if (!isLeaf()) {
      BPage child = key.ptr();
      if (child != null) {
        child.setParent(this);
      }
    }
    touch();
  }

  void change(int position, BPage newValue) {
    KeyEntry k = getKey(position);
    setKey(position, k.modify(newValue));
  }

  void change(int position, Item newItem) {
    KeyEntry existing = getKey(position);
    setKey(position, existing.modify(newItem));
  }

  void change(int position, Handle newHandle) {
    KeyEntry existing = getKey(position);
    setKey(position, existing.modify(newHandle));
  }

  void clearLastKey() {
    if (isLeaf()) {
      throw new BTreeException("Illegal call");
    }
    change(nKeys() - 1, (Handle) null);
  }

  /**
   * Get value within the page
   * 
   * @param keyIndex :
   *          index of key (0..n); note that leaf nodes contain one more values
   *          than keys.
   * @return int
   */
  public BPage getPtr(int position) {
    return getKey(position).ptr();
  }

  void setParent(BPage parent) {
    this.parent = parent;
  }

  public BPage getParentPage() {
    return parent;
  }

  public BPage(BTree tree, int id, boolean isLeaf) {
    this.tree = tree;
    this.id = id;

    this.isLeaf = isLeaf;
    int max = 0;
    if (isLeaf) {
      siblings = new BPage[2];
      max = tree.maxKeysPerLeafPage();
    } else {
      max = tree.maxKeysPerIndexPage();
    }

    keys = new KeyEntry[max];
    tree.store(this);

    touch();
  }

  protected void touch() {
  }

  public int nKeys() {
    return nKeys;
  }

  /**
   * Delete a sequence of entries.
   * 
   * @param nEntries
   *          int
   * @param position
   *          int
   */
  void deleteEntries(int position, int nEntries) {

    for (int i = position; i < nKeys; i++) {
      int j = i + nEntries;
      KeyEntry src = null;
      if (j < nKeys) {
        src = keys[j];
      }
      keys[i] = src;
    }

    nKeys -= nEntries;
    touch();
  }

  void insertEntries(BPage source, int srcPosition, int nEntries,
      int destPosition) {

    for (int i = nKeys + nEntries - 1; i >= destPosition; i--) {
      int j = i - nEntries;
      if (j >= destPosition) {
        setKey(i, keys[j]);
      } else {
        KeyEntry src = source.keys[j - destPosition + nEntries + srcPosition];
        setKey(i, src);
      }
    }

    nKeys += nEntries;
    touch();
  }

  /**
   * Perform recursive search down left side of tree to determine minimum key
   * stored in subtree
   * 
   * @return KeyEntry
   */
  public KeyEntry minKey() {
    if (!isLeaf()) {
      throw new BTreeException("Illegal call");
    }
    return new KeyEntry(firstEntry());
  }

  KeyEntry lastEntry() {
    return getKey(nKeys() - 1);
  }

  KeyEntry firstEntry() {
    return getKey(0);
  }

  void append(KeyEntry ent) {
    insert(nKeys(), ent);
  }

  void insert(int position, KeyEntry ent) {
    for (int i = nKeys; i > position; i--) {
      keys[i] = keys[i - 1];
    }
    setKey(position, ent);
    nKeys += 1;
  }

  protected String treeString() {
    StringBuffer sb = new StringBuffer();
    if (isLeaf()) {
      sb.append(getSiblingPage(false) + "<");
    }
    sb.append(Tools.f(id, 2));
    if (isLeaf()) {
      sb.append(">" + getSiblingPage(true));
    } else {
      sb.append(" --> ");
      for (int i = 0; i < nKeys; i++) {
        sb.append(" " + getKey(i).ptr());
      }
    }
    return sb.toString();
  }

  public String toString() {
    return toString(false);
  }

  /**
   * Get string describing object
   * 
   * @return String
   */
  public String toString(boolean full) {
    StringBuilder sb = new StringBuilder();
    if (!full) {
      sb.append(id);
    } else {
      sb.append("BPage");
      sb.append(" L=" + Tools.f(isLeaf()));
      // sb.append(" f=" + Scanner.toHex(flags, 4));
      sb.append(" k=" + nKeys);
      sb.append(" p=" + parent);
      Tools.tab(sb, 40);
      sb.append(treeString());
      Tools.tab(sb, 65);
      sb.append(" keys: ");
      for (int i = 0; i < nKeys; i++) {
        if (isLeaf()) {
          sb.append(" " + getKey(i).item());
        } else {
          sb.append(" " + getKey(i));
        }
      }
    }
    return sb.toString();
  }

  static void connectLeafNodes(BPage left, BPage right) {
    left.setSibling(true, right);
    right.setSibling(false, left);
  }

  public BPage getSiblingPage(boolean toRight) {
    return siblings[toRight ? 1 : 0];
  }

  void setSibling(boolean toRight, BPage pg) {
    siblings[toRight ? 1 : 0] = pg;
    touch();
  }

  /**
   * Split a page
   * 
   * @param pold :
   *          page to split
   * @return new page
   */
  BPage split(BPage pnew) {

    if (isLeaf()) {
      // establish links between old right sibling and new page.
      BPage rsib = getSiblingPage(true);
      if (rsib != null) {
        BPage.connectLeafNodes(pnew, rsib);
      }
      // establish links between old and new
      BPage.connectLeafNodes(this, pnew);
    }

    // move half the keys from the old to the new
    int moved = nKeys() / 2;
    int moveStart = nKeys() - moved;

    pnew.insertEntries(this, moveStart, moved, 0);
    deleteEntries(moveStart, moved);

    return pnew;
  }

  private boolean isLeaf;

  private KeyEntry[] keys;

  private BPage[] siblings;

  private BPage parent;

  protected int nKeys;

  private int id;

  protected BTree tree;
}
