package testbed;

import java.awt.*;
import base.*;

public class MyColor {

  public static final int WHITE = 0, LIGHTGRAY = 1, GRAY = 2, DARKGRAY = 3,
      BLACK = 4, RED = 5, PINK = 6, ORANGE = 7, YELLOW = 8, GREEN = 9,
      MAGENTA = 10, CYAN = 11, BLUE = 12, BROWN = 13, PURPLE = 14,
      DARKGREEN = 15, DEFAULT_COLORS = 16;

  public static final int SLOTS = 256;

  private static final int COLOR_LEVELS = 64;

  /**
   * Get color
   * @param slot : 0..SLOTS-1
   * @return Color
   */
  public static Color get(int slot) {
    return get(slot, .5);
  }

  private static void add(int hue, int shade, double r, double g, double b) {
    Color c = construct(r, g, b);
    colors[hue * COLOR_LEVELS + shade] = c;
  }

  public static Color get(int hue, double level) {
    int iLevel = MyMath.clamp((int) (level * ((double) COLOR_LEVELS)), 0,
        COLOR_LEVELS - 1);
    Color c = colors[hue * COLOR_LEVELS + iLevel];
    return c;

  }

  static {
    colors = new Color[SLOTS * COLOR_LEVELS];

    add(WHITE, 1, 1, 1);
    add(LIGHTGRAY, 0.75, 0.75, 0.75);
    add(GRAY, 0.50, 0.50, 0.50);
    add(DARKGRAY, 0.25, 0.25, 0.25);
    add(BLACK, 0, 0, 0);
    add(RED, Color.RED);
    add(PINK, Color.PINK);//1, 0.68, 0.68);
    add(ORANGE, Color.ORANGE);
    add(YELLOW, Color.YELLOW);
    add(GREEN, Color.GREEN);
    add(MAGENTA, Color.MAGENTA);
    add(CYAN, Color.CYAN);
    add(BLUE, Color.BLUE);
    add(BROWN, .45, .25, .05); //0.60, 0.40, 0.20);
    add(PURPLE, .516, .125, .94);
    add(DARKGREEN, 0.06, 0.38, 0.06);
  }
  // static init above must occur before these:
  public static Color cDARKGREEN = get(DARKGREEN);
  public static Color cBLUE = get(BLUE);
  public static Color cRED = get(RED);
  public static Color cLIGHTGRAY = get(LIGHTGRAY);
  public static Color cPURPLE = get(PURPLE);
  public static Color cDARKGRAY = Color.DARK_GRAY;
  /**
   * Initialize colors.
   * If already initialized, does nothing.
   * @deprecated
   */
  public static void init() {
    //    if (colors == null) {
    //      colors = new Color[SLOTS * COLOR_LEVELS];
    //
    //      add(WHITE, 1, 1, 1);
    //      add(LIGHTGRAY, 0.75, 0.75, 0.75);
    //      add(GRAY, 0.50, 0.50, 0.50);
    //      add(DARKGRAY, 0.25, 0.25, 0.25);
    //      add(BLACK, 0, 0, 0);
    //      add(RED,Color.RED);
    //      add(PINK, Color.PINK);//1, 0.68, 0.68);
    //      add(ORANGE,Color.ORANGE);
    //      add(YELLOW, Color.YELLOW);
    //      add(GREEN, Color.GREEN);
    //      add(MAGENTA, Color.MAGENTA);
    //      add(CYAN, Color.CYAN);
    //      add(BLUE, Color.BLUE);
    //      add(BROWN, .45,.25,.05); //0.60, 0.40, 0.20);
    //      add(PURPLE, .516,.125,.94);
    //      add(DARKGREEN, 0.06, 0.38, 0.06);
    //    }
  }

  /**
   * Construct a Color object from rgb values, after clamping them
   *
   * @param r : red 0..1
   * @param g : green 0..1
   * @param b : blue 0..1
   * @return
   */
  private static Color construct(double r, double g, double b) {
    return new Color((float) MyMath.clamp(r, 0, 1.0), (float) MyMath.clamp(g,
        0, 1.0), (float) MyMath.clamp(b, 0, 1.0));

  }

  /**
   * Add a color as a transition between two colors
   * @param slot : 0..SLOTS-1
   * @param a : start color
   * @param b : end color
   */
  public static void addTransition(int slot, Color a, Color b) {
    double accR = a.getRed() / 256.0;
    double accG = a.getGreen() / 256.0;
    double accB = a.getBlue() / 256.0;

    double ri = ((b.getRed() / 256.0) - accR) / COLOR_LEVELS;
    double gi = ((b.getGreen() / 256.0) - accG) / COLOR_LEVELS;
    double bi = ((b.getBlue() / 256.0) - accB) / COLOR_LEVELS;

    for (int i = 0; i < COLOR_LEVELS; i++) {
      add(slot, i, accR, accG, accB);
      accR += ri;
      accG += gi;
      accB += bi;
    }
  }

  /**
   * Add color
   * 
   * @param slot : 0..SLOTS-1
   * @param r
   * @param g
   * @param b : components, 0..1
   */
  public static void add(int slot, double r, double g, double b) {
    for (int i = 0; i < COLOR_LEVELS; i++) {
      double scale = (i * 2) / (double) COLOR_LEVELS;
      double r0 = r * scale, g0 = g * scale, b0 = b * scale;
      double extra = 0;
      if (r0 > 1.0)
        extra += r0 - 1.0;
      if (g0 > 1.0)
        extra += g0 - 1.0;
      if (b0 > 1.0)
        extra += b0 - 1.0;
      r0 += extra * .3;
      g0 += extra * .3;
      b0 += extra * .3;
      add(slot, i, r0, g0, b0);
    }
  }

  /**
   * Add color
   * 
   * @param slot : 0..SLOTS-1
   * @param c : Color to take components from
   */
  public static void add(int slot, Color c) {
    add(slot, c.getRed() / 256.0, c.getGreen() / 256.0, c.getBlue() / 256.0);
  }

  private static Color[] colors;

}
