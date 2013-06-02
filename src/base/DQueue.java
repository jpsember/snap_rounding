package base;

import java.util.*;

public class DQueue implements Cloneable {
  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (int i = 0; i < length(); i++) {
      Object obj = peekAt(i);
      sb.append(' ');
      sb.append(obj);
      Tools.addCr(sb);
    }
    sb.append("]");
    return sb.toString();
  }

  public Object peek(boolean atFront) {
    return peek(0,atFront);
  }
  
  public int peekInt(int n, boolean atFront) {
    return ((Integer)peek(n,atFront)).intValue();
  }
  
  public Object peek(int n, boolean atFront) {
    if (n >= size())
      throw new IllegalArgumentException();
    if (!atFront)
      n = size() - 1 - n;
    return a.get(calcPos(n));
  }

  public Object peek() {
    return peek(0,true);
//    return peekAt(0);
  }

  /**
   * Clone object.
   * For good summary of what's entailed in Java cloning,
   * see Core Java, Vol. 1, p. 262.
   *
   * @return Object
   */
  public Object clone() {
    try {
      DQueue d = (DQueue) super.clone();

      // clone the array
      d.a = (ArrayList) d.a.clone();
      return d;
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }

  /**
   * Push an item to the rear of the queue
   * @param obj
   */
  public void push(Object obj) {
    push(obj, false);
  }

  /**
   * Push an item onto the queue
   * @param obj
   * @param toFront if true, pushes to head of queue vs rear
   */
  public void push(Object obj, boolean toFront) {
    if (spaceRemaining() <= 1) {
      expandBuffer();
    }
    if (!toFront) {
      //      while (tail >= a.size())
      //        a.add(null);
      a.set(tail++, obj);
      if (tail == a.size()) {
        tail = 0;
      }
    } else {
      if (--head < 0) {
        head = a.size() - 1;
      }
      a.set(head, obj);
    }

  }

  /**
   * Get number of items in queue
   * @return # items
   */
  public int size() {
    final boolean db = false;

    int k = tail - head;
    if (tail < head) {
      k += a.size();
    }
    if (db)
      Streams.out.println("length, tail=" + tail + ", head=" + head
          + ", a.length=" + a.size() + ", returning " + k);
    return k;
  }

  /**
   * @deprecated use size()
   * @return
   */
  public int length() {
    return size();
  }
  //    final boolean db = false;
  //
  //    int k = tail - head;
  //    if (tail < head) {
  //      k += a.size();
  //    }
  //    if (db)
  //      Streams.out.println("length, tail=" + tail + ", head=" + head
  //          + ", a.length=" + a.size() + ", returning " + k);
  //    return k;
  //  }

  /**
   * Pop an item from the queue
   * @param fromFront true to pop from front, vs tail
   * @return
   */
  public Object pop(boolean fromFront) {
    Object ret;
    if (size() == 0) {
      throw new Error("pop of empty queue");
    }
    if (!fromFront) {
      if (tail-- == 0)
        tail = a.size() - 1;
      ret = a.get(tail);
    } else {
      ret = a.get(head);
      if (++head == a.size())
        head = 0;
    }
    return ret;
  }

  /**
   * Pop an item from the front of the queue
   * @return
   */
  public Object pop() {
    return pop(true);
  }

  /**
   * @deprecated
   * @param n
   * @return
   */
  public Object peekAt(int n) {
    if (n >= size()) {
      throw new Error("peek past end of queue");
    }
    return a.get(calcPos(n));
  }

  public String peekString(int n) {
    return (String) peekAt(n);
  }

  private int calcPos(int fromStart) {
    int k = head + fromStart;
    if (k >= a.size()) {
      k -= a.size();
    }
    return k;
  }

  private void expandBuffer() {
    ArrayList a2 = construct(a.size() * 2);

    for (int i = 0, j = head; j != tail; i++) {
      a2.set(i, a.get(j));
      if (++j == a.size()) {
        j = 0;
      }
    }
    tail = size();
    head = 0;
    a = a2;
  }

  private static ArrayList construct(int capacity) {
    ArrayList a = new ArrayList(capacity);
    while (capacity-- > 0)
      a.add(null);
    return a;
  }

  public int popInt() {
    return popInt(true);
  }
  public int popInt(boolean fromFront) {
    return ((Integer) pop(fromFront)).intValue();
  }
  /**
   * @deprecated
   * @param i
   */
  public void pushInt(int i) {
    pushInt(i, false);
  }
  public void push(int i) {
    push(i, false);
  }
  public void push(int i, boolean toFront) {
    push(new Integer(i), toFront);
  }

  /**
   * @deprecated
   * @param i
   * @param toFront
   */
  public void pushInt(int i, boolean toFront) {
    push(new Integer(i), toFront);
  }

  public double popDouble() {
    return ((Double) pop()).doubleValue();
  }

  public void pushDouble(double d) {
    push(new Double(d));
  }

  public String popString() {
    return (String) pop();
  }

  public FPoint2 popFPoint2() {
    return (FPoint2) pop();
  }

  public boolean isEmpty() {
    return length() == 0;
  }

  private int spaceRemaining() {
    return a.size() - length();
  }

  int head() {
    return head;
  }

  int tail() {
    return tail;
  }

  public DQueue() {
    this(16);
  }

  public DQueue(int initialCapacity) {
    a = construct(1 + initialCapacity);
  }

  /**
   * @deprecated
   * @return
   */
  public Object last() {
    return peekAt(size() - 1);
  }

  public void addAll(Collection c) {
    for (Iterator it = c.iterator(); it.hasNext();) {
      push(it.next());
    }
  }
  private ArrayList a;

  private int head, tail;

}
