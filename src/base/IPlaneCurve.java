package base;

/**
 * Interface representing curves in 2d plane
 */
public interface IPlaneCurve {

  /**
   * Get degree of curve (1=line, 2=conic)
   * @return degree
   */
  public int degree();

  /**
   * Get coefficient of polynomial of implicit representation of curve.
   * @param n index, 0..2 for line, 0..5 for conic
   * @return coefficient
   */
  public double coeff(int n);

  public void render(double t0, double t1);

  public FPoint2 pt(double t);

  public double parameterFor(FPoint2 pt);

}
