package snap;

import java.awt.*;
import java.util.*;
import testbed.*;

public class BundleSet implements Renderable {

  private static final boolean db = false;

  public BundleSet(SweepStrip sweepLine, Comparator c) {

    setTraceSweepStrip(sweepLine);
    this.segComparator = c;
    this.tree = new TreeSet(new Comparator() {

      public int compare(Object arg0, Object arg1) {

        Object iarg0 = arg0, iarg1 = arg1;
        // If either argument is a Bundle, replace it with the bundle's upper segment.
        if (arg0 instanceof Bundle)
          arg0 = ((Bundle) arg0).getHigh();
        if (arg1 instanceof Bundle)
          arg1 = ((Bundle) arg1).getHigh();

        int ret = segComparator.compare(arg0, arg1);

        if (db && T.update())
          T.msg("BundleSet.compare\n " + iarg0 + " with\n " + iarg1 + "\n ("
              + arg0 + " with\n " + arg1 + ")\n returning " + ret);
        return ret;
      }
    });
  }

  public void add(Bundle bundle) {
    tree.add(bundle);
    if (db && T.update())
      T.msg("added " + bundle + ", tree now\n" + this);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("BundleSet:");
    Iterator it = tree.iterator();
    while (it.hasNext()) {
      Bundle b = (Bundle) it.next();
      //      b.encode(sb);
      //      sb.append("\n ");
      //      if (false)
      //        b.encode(sb);
      //      else
      //        sb.append(b);
      sb.append("\n " + b);
    }

    return sb.toString();
  }

  private TreeSet getTree() {
    return tree;
  }
  public void remove(Bundle bundle) {
    tree.remove(bundle);
    if (db && T.update())
      T.msg("removed " + bundle + ", tree now\n" + this);
  }

  public void render(Color c, int stroke, int markType) {
    Iterator it = tree.iterator();
    while (it.hasNext()) {
      Bundle b = (Bundle) it.next();
      b.render(c, stroke, markType);
    }
  }

  public Bundle lastBundle() {
    Bundle ret = null;
    if (!tree.isEmpty())
      ret = (Bundle) tree.last();
    return ret;
  }

  /**
   * Find lowest bundle whose lower bounding segment is >= a particular segment
   * @param wedge
   * @return
   */
  public Bundle findBundleAt(Object wedge) {
    Bundle ret = null;

    // Get lowest bundle >= this pixel
    SortedSet set = getTree().tailSet(wedge);

    Iterator it2 = set.iterator();
    if (it2.hasNext())
      ret = (Bundle) it2.next();

    if (db && T.update())
      T.msg("findBundleAt " + wedge + "\n returning " + ret);
    return ret;
  }

  /**
   * For tracing purposes, we maintain a reference to a singleton SweepStrip
   * that indicates the current position of the sweep 
   * 
   * @param s : SweepStrip
   */
  private static void setTraceSweepStrip(SweepStrip s) {
    traceSweepStrip = s;
  }

  public static SweepStrip getTraceSweepStrip() {
    return traceSweepStrip;
  }

  public Iterator iterator() {
    return tree.iterator();
  }
  private Comparator segComparator;
  private TreeSet tree;

  private static SweepStrip traceSweepStrip;

}
