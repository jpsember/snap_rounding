package snap;

/**
 * Scalar interface.
 * 
 * This is for adding rational endpoints to the snap rounding algorithm.
 */
public interface Scalar {
  /**
   * Get numerator of value.
   * If value is integral, returns the value.
   * @return numerator
   */
  public int numer();
  /**
   * Get denominator of value.
   * If value is integral, returns 1.
   * @return denominator
   */
  public int denom();
  /**
   * Get value as integer
   * @throws UnsupportedOperationException if type is not integral
   * @return int
   */
  public int intValue();
  /**
   * Get value as a double
   * @return double
   */
  public double doubleValue();
}
