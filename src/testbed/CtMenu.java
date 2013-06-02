package testbed;


import javax.swing.*;

//public 
class CtMenu
    extends Gadget {
  public String toString() {
    return "CtMenu id=" + getId() + " parent=" + parent;
  }

  public boolean serialized() {
    return false;
  }

  public void writeValue(Object v) {
  //  System.out.print("writeValue "+v+" for "+getComponent());
//    if (this.getId() == 9313)
//      throw new IllegalStateException();
    
    //CtMenu,label="+label+" id="+id);
    
    ( (JMenuItem) getComponent()).setText( (String) v);
  }

  public Object readValue() {
    return ( (JMenuItem) getComponent()).getText();
  }

  private class myJMenuItem
      extends JMenuItem implements GadgetComponent {
    /**
     * Get gadget associated with this object, or null
     * @return Gadget
     */
    public Gadget getGadget() {
      return CtMenu.this;
    }

    public myJMenuItem(String label) {
      super(label);
    }

    public String toString() {
      return "myJMenuItem id=" + getId() + " '" + getText() + "'";
    }

  }

  private class myJMenu
      extends JMenu implements GadgetComponent {
    /**
     * Get gadget associated with this object, or null
     * @return Gadget
     */
    public Gadget getGadget() {
      return CtMenu.this;
    }

    public myJMenu(String label) {
      super(label);
    }

    public String toString() {
      return "myJMenu id=" + getId() + " '" + getText() + "'";
    }
  }

  /**
   * Construct a CtMenu for a JMenu
   * @param id : id of JMenu
   * @param label : label for menu
   * @return CtMenu
   */
  public static CtMenu newMenu(int id, String label) {
    return new CtMenu(0, id, label, true);
  }

  public static CtMenu newItem(int parent, int id, String label) {
    return new CtMenu(parent, id, label, false);
  }

  private CtMenu(int parent, int id, String label, boolean menuFlag) {
    setId(id);
    if (menuFlag) {
      setComponent(new myJMenu(label));
    }
    else {
      setComponent(new myJMenuItem(label));
      this.parent = parent;
    }
  }

  public void addChildItem(int id) {
    children.addInt(id);
  }

  public JMenu getMenu() {
    return (JMenu) button();
  }

  public JMenuItem getItem() {
    return (JMenuItem) button();
  }

  public int parentId() {
    return parent;
  }

  private AbstractButton button() {
    return (AbstractButton) getComponent();
  }

  private int parent;
}
