package testbed;

import java.awt.*;
import base.*;

public class EdPoint extends EdObject implements Globals {
  /**
    * Render object within editor.
    * Override this to change highlighting behaviour for points.
    */
  public void render() {
    render(isSelected() ? Color.RED : null, -1, -1);
  }

  protected EdPoint() {
  }

  /**
   * Constructor
   * @param loc : location of point
   */
  public EdPoint(FPoint2 loc) {
    setPoint(0, loc);
  }

  public EdPoint(double[] script, int offset) {
    this(script[offset + 0], script[offset + 1]);
  }

  public EdPoint(double originX, double originY) {
    this(new FPoint2(originX, originY));
  }

  public EdPoint(EdPoint c) {
    this(c.getPoint(0));
  }

  public boolean complete() {
    return nPoints() == 1;
  }

  public double distFrom(FPoint2 pt) {
    if (!complete())
      return -1;
    double d = FPoint2.distance(pt, getPoint(0));
    return d;
  }
  /**
   * We don't want to interpret moving individual points of the point, since
   * there's only one.  Otherwise dragging a point always drags just that point,
   * and not others that may be selected.
   */
  public double distFrom(int ptIndex, FPoint2 pt) {
    return -1;
  }

  public EdObjectFactory getFactory() {
    return FACTORY;
  }

  /**
   * Get radius of point.  This always returns zero.
   * Included so points can be treated as degenerate discs.
   */
  public double getRadius() {
    return 0;
  }

  /**
   * Get origin of point.  Returns the point's location.
   * Included so points can be treated as degenerate discs.
   */
  public FPoint2 getOrigin() {
    return getPoint(0);
  }

  public static EdObjectFactory FACTORY = new EdObjectFactory() {

    public EdObject construct() {
      return new EdPoint();
    }

    public String getTag() {
      return "pt";
    }

    public EdObject parse(Tokenizer s, int flags) {
      final boolean db = false;
      if (db)
        Streams.out.println("EdPoint, parse, next=" + s.peek().debug());

      EdPoint pt = new EdPoint(s.extractFPoint2());
      pt.setFlags(flags);
      return pt;
    }

    public void write(StringBuilder sb, EdObject obj) {
      EdPoint pt = (EdPoint) obj;
      sb.append(pt.getPoint(0));
    }

    public String getMenuLabel() {
      return "Add point";
    }
    public String getKeyEquivalent() {
      return "p";
    }
  };

  public void render(Color c, int stroke, int markType) {

    if (c == null)
      c = isActive() ? Color.BLUE : Color.gray;
    do {
      if (!complete())
        break;
      V.pushColor(c);
      if (stroke >= 0)
        V.pushStroke(stroke);
      V.fillCircle(getPoint(0), V.getScale() * .4);

      if (stroke >= 0)
        V.popStroke();
      V.popColor();
    } while (false);
  }
}
