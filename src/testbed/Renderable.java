package testbed;

import java.awt.*;

/**
 * Interface for rendering objects during algorithm tracing
 */
public interface Renderable {
  /**
   * Plot results during trace
   */
  public void render(Color c, int stroke, int markType);
}
