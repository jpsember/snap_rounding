package testbed;

/**
 * Interface to store global variables for the TestBed framework
 *
 */
interface TBGlobals {

  static final int
  // control panel indexes
      CT_MAIN = 0,
      CT_CONSOLE = 1, CT_TOTAL = 2;

  /**
   * Reserved gadget ids
   */
  static final int
  // ids from 8900..8999 are not read/written to individual file headers,
      // but are written to configuration files
      CONFIGSTART = 8900,
      //
      FILLCOLOR = 8901,
      sFILLCOLOR = 8902,
      // start of ids for serializing TestBed variables
      TBFRAME = 8903, // x, y, width, height
      TBCTRLSLIDER = TBFRAME + 4,
      TBCONSOLESLIDER = TBFRAME + 5,

      //
      CONFIGEND = 9000,

      ABOUT = 9001,
      GENERATEEPS = 9002,
      OPER = 9003,
      GENERATEIPE = 9005,
      GENERATEPDF = 9006,
      ENFORCE_ASP = 9029,
      GLOBALSCALE = 9030,
      MENU_TESTBED = 9031,
             ASPECTRATIO = 9032, 
      QUIT = 9033, GRIDACTIVE = 9036,
      GRIDON = 9040,
      GRIDLABELS = 9041,
      GRIDSIZE = 9042, 
      MOUSELOC = 9043,
      ID_CONSOLE = 9060,

      CTRLSVISIBLE = 9062,
      CONSOLEVISIBLE = 9063,
      BTN_TOGGLECTRLS = 9064,
      BTN_TOGGLECONSOLE = 9065,
      BTN_TOGGLEWORKSPACE = 9066,
      AUXTABSET = 9067,
      AUXTAB_VIEW = 9068, AUXTAB_GRID = 9069, AUXTAB_TRACE = 9070,

      TRACESTEP = 9092, TRACEPLOT = 9093,
      TRACEBWD = 9094,
      TRACEFWD = 9095,
      TRACEBTNFWD = 9096, TRACEBTNBWD = 9097, TRACEENABLED = 9098,

      EDITORPARMS = 9300, EDITORPARMS_MAXLENGTH = 100,

      // start of ids to assign to anonymous panels
      ID_ANON_START = 9500
      // Application ids should start at 100.
      // Each operator should have a distinct set of ids,
      // i.e. 100..199, 200.299, etc.
      ;

}
