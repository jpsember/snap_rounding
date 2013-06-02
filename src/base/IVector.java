package base;

public interface IVector extends IMatrix {
  
  
  
  /**
   * Get size of vector
   * @return size
   */
  public int size() ;

  /**
   * Set coefficient
   * @param index : location 
   * @param v : value
   */
  public void set(int index, double v);

  /**
   * Get coefficient from vector
   * @param y : row (0..size-1)
   * @return coefficient
   */
  public double get(int y);

  
  /**
   * Calculate length of vector
   * @return Euclidean length
   */
  public double length();

  /**
   * Calculate squared length of vector
   * @return Euclidean length
   */
  public double lengthSq();

  /**
   * Normalize vector so it has length 1, if not all zeros
   * @return length of original vector
   */
  public double normalize();

  /**
   * Scale every component by a value
   * @param s : scale factor
   */
  public void scale(double s);

  /**
   * Negate every component
   */
  public void negate();

  public void setTo(IVector v);


  public double x();
  public double y();
  public double z();
  public void setX(double x);
  public void setY(double y);
  public void setZ(double z);
}
