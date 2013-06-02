package testbed;

/**
 * Interface for TestBed operations
 */
public interface TestBedOperation extends Globals {
  /**
   * Add controls for this operation.  Each operation exists in its own
   * tab, so the controls should consist of:
   * <pre>
   *   C.sOpenTab( "Oper name");
   *    :
   *    :
   *   C.sCloseTab();
   * </pre>
   */
  public void addControls();

  /**
   * Process an action
   * @param a action
   */
  public void processAction(TBAction a);

  /**
   * Execute an algorithm.  If no algorithm is to be run, this method
   * can do nothing.
   * <br>
   * Tracing can be performed by inserting these lines in various places:
   *
   * <pre>
   *   if (T.update())
   *     T.msg("Examining vertex #"+vertexNumber);
   * </pre>
   * 
   * If certain objects are to appear when an algorithm trace is displayed,
   * they should be included by using a show() command.  For instance, if 
   * the object 'vertex' implements the Traceable interface, then the following
   * code will cause it to be rendered when the algorithm results are shown:
   * 
   * <pre>
   *   if (T.update())
   *     T.msg("Processing vertex #"+vertexNumber + T.show(vertex));
   * <pre>
   * 
   */
  public void runAlgorithm();

  /**
   * Paint view for operation.  This is called after runAlgorithm(), which
   * may have been interrupted by an algorithm trace.
   */
  public void paintView();

}
