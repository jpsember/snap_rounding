package base;
public class ScanException extends RuntimeException {
  public ScanException(String s) {
  super(s);
  }
  public ScanException(String s, Token t) {
    super(s+", "+t.display());
  }
  public static void toss(Exception e) {
    throw new ScanException(e.toString());
  }
  public String toString() {return getMessage();}
}
