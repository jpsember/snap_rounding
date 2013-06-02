package base;

/**
 * Matrix interface, supported by Matrix, Vec, FPoint2, FPoint3
 */
public interface IMatrix {

  /**
   * Set coefficient
   * @param y row (0..height-1)
   * @param x column (0..width-1)
   * @param v value
   */
  public void set(int y, int x, double v);
  /**
   * Get coefficient from matrix, by returning coeff[y*width + x].
   * @param y row (0..height-1)
   * @param x column (0..width-1)
   * @return coefficient
   */
  public double get(int y, int x);

  /**
   * Clear matrix to zeros
   */
  public void clear();

  /**
   * Get array of coefficients.
   * They are indexed by [y*height + x].
   * Not all classes support this method.
   * @return array of coefficients
   */
  public double[] coeff();

  /**
   * Get width of matrix
   * @return width
   */
  public int width();

  /**
   * Get height of matrix
   * @return height
   */
  public int height();
}
