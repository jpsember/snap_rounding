package testbed;

import static base.Tools.*;
import base.*;
import java.util.*;

class GadgetList implements IEditorScript, Comparator {

  private static final boolean WARN = false;
  
  // ----------- comparator interface
  public int compare(Object object, Object object1) {
    return id(object) - id(object1);
  }

  private static int id(Object obj) {
    int id = -1;
    if (obj instanceof Integer) {
      id = ((Integer) obj).intValue();
    } else
      id = ((Gadget) obj).getId();
    return id;
  }

  public boolean equals(Object object) {
    return compare(this, object) == 0;
  }

  /**
   * Enable each gadget in a list
   * @param idList : list of ids to enable
   * @param state : enable state to set to
   */
  public void setEnable(int[] idList, boolean state) {
    for (int i = 0; i < idList.length; i++) {
      setEnable(idList[i], state);
    }
  }

  /**
   * Read enable state of gadget
   * @param id : gadget id
   * @return true if gadget is enabled
   */
  public boolean enabled(int id) {
    Gadget c = get(id);
    return c.getComponent().isEnabled();
  }

  /**
   * Set enable status of a gadget and its children
   * @param id : gadget id
   * @param state : true to enable, false to disable
   */
  public void setEnable(int id, boolean state) {
    Gadget c = get(id);
    c.getComponent().setEnabled(state);
  }

  /**
   * Get value of integer-valued gadget
   * @param id : id of gadget
   * @return value
   */
  public int intValue(int id) {
    Gadget obj = get(id);
    if (obj == null) {
      if  (WARN)
      Tools.warn("no such integer-valued gadget " + id);
      return 0;
    }
    Integer iVal = null;
    Object v = obj.readValue();
    if (v instanceof Integer) {
      iVal = (Integer) v;
    } else {
      iVal = new Integer(Integer.parseInt(v.toString()));
    }
    if (iVal == null) {
      if  (WARN)
         Tools.warn("gadget " + obj + " has no value");
      return 0;
    }
    return iVal.intValue();
  }

  /**
   * Get value of boolean-valued gadget
   * @param id : id of gadget
   * @return value
   */
  public boolean booleanValue(int id) {
    Gadget g = get(id);
    if (g == null) {
      if  (WARN)
         Tools.warn("no such boolean-valued gadget " + id);
      return false;
    }
    Boolean b = (Boolean) g.readValue();
    if (b == null) {
      if  (WARN)
          Tools.warn("boolean gadget " + g + " has no value");
      b = Boolean.FALSE;
    }

    return b.booleanValue();
  }

  /**
   * Set value of integer-valued gadget
   * @param id int
   * @param v int
   */
  public void setValue(int id, int v) {
    Object val = null;
    Gadget g = get(id);
    if (g.dataType == Gadget.DT_STRING) {
      val = Integer.toString(v);
    } else
      val = new Integer(v);
    g.writeValue(val);
  }

  /**
   * Set value of boolean-valued gadget
   * @param id int
   * @param v boolean
   */
  public void setValue(int id, boolean v) {
    Gadget g = get(id);
    if (g != null)
      g.writeValue(new Boolean(v));
    else {
      if  (WARN)
         Tools.warn("no gadget id=" + id + " found");
    }
  }

  /**
   * Get value of double-valued gadget
   * @param id : id of gadget
   * @return value
   */
  public double doubleValue(int id) {
    final boolean db = false;
    if (db)
      System.out.println("doubleValue id=" + id + " readValue is "
          + get(id).readValue());
    return ((Double) get(id).readValue()).doubleValue();
  }

  /**
   * Set value of double-valued gadget
   * @param id int
   * @param v double
   */
  public void setValue(int id, double v) {
    get(id).writeValue(new Double(v));
  }

  /**
   * Get value of string-valued gadget
   * @param id : id of gadget
   * @return value
   */
  public String stringValue(int id) {
    return (String) (get(id).readValue());
  }

  /**
   * Set value of string-valued gadget
   * @param id int
   * @param v String
   */
  public void setValue(int id, String v) {
    get(id).writeValue(v);
  }

  private Gadget find(int id) {
    SortedSet s2 = set.tailSet(new Integer(id));
    Gadget f = null;
    if (!s2.isEmpty()) {
      f = (Gadget) s2.first();
      if (f.getId() != id)
        f = null;
    }
    return f;
  }

  public boolean exists(int id) {
    return (find(id) != null);
  }

  public Gadget get(int id) {
    Gadget f = find(id);

    if (WARN && f == null) {
      Tools.warn("can't find gadget: " + id + "\n" + Tools.st());
    }
    return f;
  }

  public void free(int id) {
    Gadget f = get(id);
    set.remove(f);
    f.setId(0);
  }

  public void add(Gadget c) {
    
    
    
    int id = c.getId();
    
    //pr("GadgetList.add "+id);
   /* 
    if (id == 9334) {
    pr("stack trace:\n"+stackTrace(0,12));
    }
   */
 
    if (exists(id)) {
      ASSERT(!exists(id), "Id already exists:" + id + "\n"
          + get(id).toString());
    }
    set.add(c);
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("GadgetList ");
    ourIterator it = new ourIterator(set);
    while (it.hasNext()) {
      Gadget g = it.next();
      sb.append(" " + g.getId() + ":" + g.toString() + "\n");
    }
    return sb.toString();
  }

  public DArray getList(boolean configContext) {
    DArray ret = new DArray();
    Iterator it = set.iterator();
    while (it.hasNext()) {
      Gadget g = (Gadget) it.next();
      int id = g.getId();
      
      // skip this; it messes up saving of window locations
      /*
      if (!configContext) {
        if (id >= TBGlobals.CONFIGSTART && id < TBGlobals.CONFIGEND)
          continue;
      }
      */
      ret.addInt(id);
    }
    return ret;
  }

  //  public void test() {
  //
  //    System.out.println("Reading values for gadgets");
  //
  //    String s = getValues(1, 20000, null); //1, 20000, true);
  //    System.out.println(Tools.insertLineFeeds(s, 75));
  //  }

  /**
   * Get string describing gadget values
   * @param configContext true if configuration file, false if editor file
   * @return string containing values
   */
  String getValues(boolean configContext) {
    final boolean db = false;

    if (db)
      System.out.println("getValues ");

    StringBuilder sb = new StringBuilder();
    sb.append('[');

    DArray idList = getList(configContext);

    Gadget g = null;
    Object v = null;

    int lastCR = 0;

    for (int i = 0; i < idList.size(); i++) {
      int id = idList.getInt(i);
      g = get(id);

      // If it's not a gadget we're interested in retaining the value of, skip.

      //Streams.out.println("id="+id+" value="+g.readValue()+" ser="+g.serialized());
      if (!g.serialized())
        continue;
      if (db)
        System.out.println(" attempting to read value for id " + id + ", g=\n"
            + g);
      v = g.readValue();
      if (db)
        System.out.println(" value " + id + " is " + v);
      if (v == null)
        continue;

      sb.append(g.getId());
      sb.append(' ');
      switch (g.dataType) {
      case Gadget.DT_BOOL:
        sb.append(Tools.f(((Boolean) v).booleanValue()));
        break;
      case Gadget.DT_DOUBLE:
        sb.append(Tools.f(((Double) v).doubleValue()));
        break;
      case Gadget.DT_STRING:
        sb.append(TextScanner.convert((String) v, false, '"'));
        break;
      case Gadget.DT_INT:
        sb.append(v);
        break;
      }
      if (sb.length() - lastCR > 60) {
        sb.append('\n');
        lastCR = sb.length();
      } else
        sb.append(' ');
    }
    sb.append(']');
    if (db)
      System.out.println(" returning\n" + sb.toString());
    return sb.toString();
  }

  /**
   * Parse a sequence of gadget values.
   * Assumes the values are surrounded by '[' and ']' tokens.
   *
   * @param tk Tokenizer
   */
  public void setValues(Tokenizer tk) {
    if (tk.readIf(T_BROP)) {
      outer: while (!tk.readIf(T_BRCL)) {

        // if unexpected boolean, skip
        if (tk.readIf(T_BOOL))
          continue outer;

        int id = tk.readInt();

        if (!exists(id)) {
          tk.read();
          continue;
        }

        Object v = null;
        Gadget g = get(id);
        switch (g.dataType) {
        case Gadget.DT_BOOL:
          v = new Boolean(tk.readBoolean());
          break;
        case Gadget.DT_DOUBLE:
          v = new Double(tk.readDouble());
          break;
        case Gadget.DT_STRING:
          // skip unexpected booleans
          if (tk.peek(T_BOOL))
            continue outer;
          if (tk.peek(T_INT)) {
            v = Integer.toString(tk.readInt());
          } else
            v = tk.readString();
          break;
        case Gadget.DT_INT:
          v = new Integer(tk.readInt());
          break;
        }

        //        Streams.out.println("attempting to write value="+Tools.d(v)+" to gadget "+g);
        g.writeValue(v);
      }
    }
  }

  static class ourIterator {
    public ourIterator(SortedSet s) {
      it = s.iterator();
    }

    public Gadget next() {
      return (Gadget) (it.next());
    }

    public boolean hasNext() {
      return it.hasNext();
    }

    private Iterator it;
  }

  private TreeSet set = new TreeSet(this);

}
