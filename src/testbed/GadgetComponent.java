package testbed;

/**
 * GadgetComponent interface.  Used to associate Gadget objects to Swing/AWT
 * components
 */
  interface GadgetComponent {
    /**
     * Get gadget associated with this object, or null
     * @return Gadget
     */
    public Gadget getGadget();
}

