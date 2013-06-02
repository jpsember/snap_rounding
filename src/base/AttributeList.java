package base;

import java.util.*;
import base.*;

public class AttributeList {
  public int size() {
    return am.size();
  }

  public String getValue(int index) {
    return (String) am.getValue(index);
  }

  public String getValueOf(String name) {
    return (String) am.getValue(name);
  }

  public String getName(int index) {
    return (String) am.getKey(index);
  }

  /**
   * Add name/value pair
   * @param name
   * @param value
   * @return previous value that this name had, or null 
   */
  public String set(String name, String value) {
    return (String) am.add(name, value);
  }

  private ArrayMap am = new ArrayMap();
}
