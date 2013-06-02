package base;

import java.util.*;

/**
 * Collection for a map whose keys are also stored in an array
 * for quick access.
 */
public class ArrayMap implements Iterable {
  private static final boolean db = false;

  /**
   * Add a key to the map, storing its position as its value.
   * If it already exists, doesn't replace it.
   * @param key item to add
   * @return position position in array
   */
  public int add(Object key) {
    Integer pos = (Integer) keyPositionMap.get(key);
    if (pos == null) {
      pos = new Integer(size());
      add(key, pos);
    }
    return pos.intValue();
  }

  /**
   * Add an item to the map; replace existing if found
   * @param key key  
   * @param value value to associate with key
   * @return previous value, if one existed
   */
  public Object add(Object key, Object value) {
    if (db)
      Streams.out.println("ArrayMap.add key=" + Tools.tv(key) + " value="
          + Tools.tv(value));

    Object prevValue = null;
    int prevLocation = -1;
    {
      Integer pos = (Integer) keyPositionMap.get(key);
      if (pos != null)
        prevLocation = pos.intValue();
    }

    if (prevLocation < 0) {
      int index = valArray.size();
      valArray.add(value);
      keyArray.add(key);
      keyPositionMap.put(key, new Integer(index));
    } else {
      prevValue = valArray.get(prevLocation);
      valArray.set(prevLocation, value);
    }
    if (db)
      Streams.out.println(" returning previous value " + Tools.tv(prevValue));

    return prevValue;
  }

  /**
   * Get array containing keys
   * @return
   */
  public DArray getKeys() {
    return keyArray;
  }

  /**
   * Determine if map is empty
   * @return true if empty
   */
  public boolean isEmpty() {
    return keyArray.isEmpty();
  }

  /**
   * Get # items in map
   * @return # items in map
   */
  public int size() {
    return keyArray.size();
  }

  /**
   * Get key from array
   * @param index index within the array (0...size()-1)
   * @return key
   */
  public Object getKey(int index) {
    return keyArray.get(index);
  }

  /**
   * Get value associated with key
   * @param key 
   * @return object, or null if none
   */
  public Object getValue(Object key) {
    Object ret = null;
    Integer pos = (Integer) keyPositionMap.get(key);
    if (pos != null) {
      ret = valArray.get(pos.intValue());
    }
    return ret;
  }

  /**
   * Convenience method; read value, cast to Integer
   * @param key
   * @return int value, or -1 if the key wasn't in the map
   */
  public int getIntValue(Object key) {
    Object v = getValue(key);
    return (v == null) ? -1 : ((Integer) v).intValue();
  }

  /**
   * Get value associated with index within array
   * @param index
   * @return object associated with value
   */
  public Object getValue(int index) {
    return valArray.get(index);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ArrayMap[\n");
    for (int i = 0; i < keyArray.size(); i++) {
      sb.append(' ');
      sb.append(Tools.f(i, 3));
      sb.append(':');
      Object key = getKey(i);
      sb.append(key.toString());
      sb.append(" --> ");
      sb.append(valArray.get(i));
      Tools.addCr(sb);
    }
    sb.append("]\n");

    return sb.toString();
  }

  /**
   * Construct a map from [int]->[Object]
   * @param iVals int array
   * @param oVals array of Objects
   * @return Map
   */
  public static Map intKeyMap(int[] iVals, Object[] oVals) {
    Map map = new HashMap(iVals.length);
    for (int i = iVals.length - 1; i >= 0; i--) {
      Object prev = map.put(new Integer(iVals[i]), oVals[i]);
      if (prev != null)
        Tools.warn("intKeyMap, duplicate entry for key: " + iVals[i]);
    }
    return map;
  }

  public static Map intKeyMap(int[] iVals, String labels) {
    StringTokenizer tk = new StringTokenizer(labels);
    DArray lbl = new DArray();
    while (tk.hasMoreTokens()) {
      String l = tk.nextToken();
      l = Tools.f(l, 16);
      lbl.add(l);
    }
    if (lbl.size() != iVals.length)
      throw new IllegalArgumentException("unexpected # of labels");
    return intKeyMap(iVals, lbl.toStringArray());
  }

  public static String readString(Map map, int type) {

    String s = (String) map.get(new Integer(type));
    if (s == null)
      s = Tools.f("<unknown: " + type + ">", 16);
    return s;
  }

  /**
   * Get iterator over values
   */
  public Iterator iterator() {
    return valArray.iterator();
  }

  /**
   * Determine index of a key within the array
   * @param key key
   * @return index of key within array, or -1 if not found
   */
  public int indexOf(Object key) {
    Integer iv = (Integer) keyPositionMap.get(key);
    int ret = (iv == null) ? -1 : iv.intValue();
    if (db)
      Streams.out.println("ArrayMap.indexOf " + Tools.tv(key) + " returning "
          + ret + "\n" + this);
    return ret;

  }

  private Map keyPositionMap = new HashMap();
  private DArray valArray = new DArray();
  private DArray keyArray = new DArray();
}
