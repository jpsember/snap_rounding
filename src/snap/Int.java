package snap;

/**
 * Scalar for integer types
 */
public class Int implements Scalar {

  public Int(int n) {
    this.value = n;
  }
  private int value;
  public double doubleValue() {
    return value;
  }
  public int intValue() {
    return value;
  }
  public int denom() {
    return 1;
  }
  public int numer() {
    return intValue();
  }
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(intValue());
    return sb.toString();
  }
}
