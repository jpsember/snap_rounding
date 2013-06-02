package snaptree;

/**
 * Handle interface for SegTree data structure.
 * Using these handles, leaf items can be exchanged in constant time,
 * and the tree is updated at no extra cost.
 */
public class Handle {
  public String toString() {
    return item.toString();
  }

  public Handle(Item item) {
    setItem(item);
  }

  public Item getItem() {
    return item;
  }

  public Item setItem(Item item) {
    Item old = this.item;
    this.item = item;
    return old;
  }
  private Item item;
}
