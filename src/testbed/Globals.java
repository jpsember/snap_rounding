package testbed;

/**
 * Interface to store global variables for the TestBed framework
 *
 */
public interface Globals {


  // predefined strokes:
  public static final int STRK_NORMAL = 0, STRK_THICK = 1, STRK_THIN = 2,
      STRK_VERYTHICK = 3, STRK_RUBBERBAND = 4, STRK_TOTAL = 5

      , MARK_X = 0, MARK_DISC = 1,
      MARK_CIRCLE = 2,
      MARK_SQUARE = 3,
      MARK_FSQUARE = 4,
      MARK_NONE = 5, 
      // text plotting flags

      // do multiline text with no line > n chars wide?

      TX_LINEWIDTH = 0x00ff
      // clear background?
      ,
      TX_BGND = 0x0100
      // draw frame around text?
      ,
      TX_FRAME = 0x0200
      // clamp into range?
      ,
      TX_CLAMP = 0x0400

      // Font indexes:
      , FNT_SMALL = 0, FNT_MEDIUM = 1, FNT_LARGE = 2, FNT_ITALIC = 3,
      FNT_TOTAL = 4;

  public static final int TAB_ID_START = 1000;


}
