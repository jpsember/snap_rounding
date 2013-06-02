package snaptree;

import java.util.*;

public class BTreeIterator
    implements Iterator {

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BTreeIterator");
		sb.append("\n bpage="+page.toString(true));
		sb.append("\n position="+position);
		sb.append("\n page length="+page.nKeys());
		
		return sb.toString();
	}
  public boolean hasNext() {
    tree.verifyUnchanged(changeCounter);

    return (position < page.nKeys()
            || page.getSiblingPage(true) != null);
  }

  public boolean hasPrev() {
    tree.verifyUnchanged(changeCounter);

    return (position > 0 || page.getSiblingPage(false) != null);
  }

  public Object prev() {
    tree.verifyUnchanged(changeCounter);
    if (!hasPrev()) {
      throw new NoSuchElementException();
    }
    if (position == 0) {
      page = page.getSiblingPage(false);
      position = page.nKeys();
    }
    position--;

    Object out = page.getKey(position).item();
    return out;
  }

  public Object next() {
    tree.verifyUnchanged(changeCounter);
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    if (position == page.nKeys()) {
      page = page.getSiblingPage(true);
      position = 0;
    }
    Object out = page.getKey(position).item();
    position++;
    return out;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  BTreeIterator(BTree tree, BPath path, int changeCounter) {
    this.tree = tree;

    this.changeCounter = changeCounter;
    PathNode node = path.tail();
    page = node.page();
    position = node.position();
  }

  private int changeCounter;
  private BPage page;
  private int position;
  private BTree tree;
}
