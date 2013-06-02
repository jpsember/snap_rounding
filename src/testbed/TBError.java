package testbed;
import base.*;

/**
 * Make TBError unchecked, since we want to be
 * able to throw them from some interface methods that don't
 * have 'throws' clauses, and having to declare them is irritating.
 */
public class TBError
    extends Error {

  public TBError(Throwable cause) {
  this(cause.toString());
  }
  
  protected String name() {
    return "TBError";
  }
  public TBError(String s) {
    super(s);
    int trim = 0;
    int maxCalls = 5;
    boolean withSt = false;
    outer:while (true) {
      if (s.length() == trim) {
        break;
      }
      switch (s.charAt(trim)) {
        default:
          break outer;
        case '*':
          withSt = true;
          break;
        case '!':
          maxCalls += 5;
          break;
      }
      trim++;
    }
    StringBuilder str = new StringBuilder(name());
    str.append(": ");
//    boolean withSt = s.startsWith("*");
//    int trim = 0;
//    int maxCalls = 5;
//    if (withSt) {
//      trim++;
//      if (s.
    str.append(s.substring(trim));
    if (withSt) {
      str.append("\n");
//      Tools.warn("not sure about stackTrace depth");
      str.append(Tools.stackTrace(1, maxCalls, this));
    }
    msg = str.toString();
  }

  private String msg;

  public String toString() {
    return msg;
  }
}
