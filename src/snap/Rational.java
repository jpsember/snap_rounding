package snap;

import base.*;

public class Rational implements Scalar {

  /**
   * Construct a scalar 
   * @param numer
   * @param denom
   * @return Scalar, either a Rational, or if |denominator| = 1, an int
   */
  public static Scalar valueOf(int numer, int denom) {
    if (denom < 0) {
      denom = -denom;
      numer = -numer;
    }
    if (MyMath.mod(numer, denom) == 0)
      return new Int(numer / denom);
    return new Rational(numer, denom);
  }

  public Rational(int numer, int denom) {
    if (denom == 0)
      throw new IllegalArgumentException();
    if (denom < 0) {
      denom = -denom;
      numer = -numer;
    }
    this.numer = numer;
    this.denom = denom;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(numer);
    sb.append('/');
    sb.append(denom);
    return sb.toString();
  }

  public double doubleValue() {
    return ((double) numer) / denom;
  }

  public int intValue() {
    if (denom == 1)
      return numer;
    throw new UnsupportedOperationException("Can't convert rational to int");
  }
  private int numer;
  private int denom;
  public int denom() {
    return denom;
  }

  public int numer() {
    return numer;
  }
}
