package snap;

import base.*;

public class HotPixelList {

  public int size() {
    return lst.size();
  }

  public IPoint2 last() {
    return get(lst.size() - 1);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HotPixelList");
    sb.append(lst.toString());
    return sb.toString();
  }
  private DArray lst = new DArray();

  public void add(IPoint2 hpix) {
    lst.add(hpix);
  }

  public IPoint2 get(int i) {
    IPoint2 ret = null;
    if (lst.exists(i))
      ret = (IPoint2) lst.get(i);
    return ret;

  }

  public void replaceWith(HotPixelList newList) {
    lst.clear();
    lst.addAll(newList.lst);
  }

  public IPoint2 pop() {
    IPoint2 ret = last();
    lst.pop();
    return ret;
  }

  public void remove(int i) {
    lst.remove(i);
  }
}
