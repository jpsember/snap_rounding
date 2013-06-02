package testbed;

import base.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

class CtSpinner extends Gadget {
  private static final boolean db = false;
  //  private static final boolean NEWEXT = true;

  //  static {
  //    if (NEWEXT)
  //      Tools.warn("trying new extent calls");
  //  }
  //
  public int gcFill() {
    if (isSlider) {
      return GridBagConstraints.HORIZONTAL;
    }
    return super.gcFill();
  }

  /**
   * Get string describing object
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("CtSpinner");
    sb.append(" id=" + getId());
    sb.append(" slider=" + Tools.f(isSlider));
    return sb.toString();
  }

  public void writeValue(Object v) {
    if (db) {
      Streams.out.println("writing value " + v + " to CtSpinner " + this);
    }

    if (isSlider) {
      ((mySlider) comp).writeValue(v);
    } else {
      ((mySpinner) comp).writeValue(v);
    }
  }

  public Object readValue() {
    if (db) {
      Streams.out.println("reading value from CtSpinner " + this);
    }
    if (isSlider) {
      return ((mySlider) comp).readValue();
    } else {
      return ((mySpinner) comp).readValue();
    }
  }

  private Component comp;

  public void setEnabled(GadgetList gl, boolean state) {
    comp.setEnabled(state);
  }

  /**
   * Constructor
   * @param label String
   * @param alignment int
   */
  public CtSpinner(int id, String label, double dmin, double dmax,
      double dvalue, double step, boolean sliderFlag, boolean withTicks,
      boolean dbl) {

    this.dataType = dbl ? DT_DOUBLE : DT_INT;
    this.isSlider = sliderFlag;
    setId(id);
    if (db) {
      Streams.out.println("constructing CtSpinner " + this);
    }

    JPanel panel = new JPanel();
    panel.setOpaque(true);

    if (!sliderFlag) {
      JSpinner c1 = null;
      if (!dbl) {
        c1 = new mySpinner((int) dmin, (int) dmax, (int) dvalue, (int) step);
      } else {
        c1 = new mySpinner(dmin, dmax, dvalue, step);
      }

      c1.addChangeListener(this);
      if (label != null) {
        panel.add(new JLabel(label));
      }
      comp = c1;
      panel.add(c1);
    } else {
      if (true) {
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);

        panel.setLayout(layout);
        mySlider cs = null;
        //        if (!dbl) {
        //          cs = new mySlider((int) dmin, (int) dmax, (int) dvalue, (int)step);
        //        } else {
        cs = new mySlider(dbl, dmin, dmax, dvalue, step);
        //        }

        cs.addChangeListener(this);
        if (label != null) {
          if (!label.endsWith(":"))
            label = label + ":";
          JLabel lbl = new JLabel(label);
          lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
          panel.add(lbl);
        }

        if (withTicks) {
          final boolean db = false;
          if (db) {
            System.out.println("slider ticks, dmax=" + dmax + ", dmin=" + dmin);
          }
          {
            int steps = 0;
            double r = dmax - dmin;
            if (dbl) {
              int nsteps = (int) (r / step);
              int stepV = (int) (r / nsteps);

              if (nsteps <= 12) {
                cs.setMinorTickSpacing(stepV);
                cs.setMajorTickSpacing(2 * stepV);
                cs.createStandardLabels(2 * stepV);
                cs.setSnapToTicks(true);
              } else {
                nsteps = Math.min(nsteps, 50);
                double major = (r / 4 / step);
                double minor = major / 2;

                cs.setMinorTickSpacing((int) minor);
                cs.setMajorTickSpacing((int) major);
                cs.createStandardLabels((int) (r / 4));
              }
            } else {
              steps = (int) (dmax + 1 - dmin);

              if (steps <= 12) {
                cs.setMinorTickSpacing(1);
                cs.setMajorTickSpacing(2);
                cs.createStandardLabels(2);
                cs.setSnapToTicks(true);
              } else {
                cs.setMinorTickSpacing(steps / 20);
                cs.setMajorTickSpacing(steps / 4);
                cs.createStandardLabels(steps / 4);
              }

            }
            if (db) {
              System.out.println(" steps=" + steps);
            }
            cs.setPaintTicks(true);
            cs.setPaintLabels(true);

            if (false && steps > 12) {
              Dictionary labels = new Hashtable();
              for (int i = 0; i < 5; i++) {
                int val = (i * steps) / 4;

                double x = (val * r) / steps + dmin;
                String s = null;
                if (db) {
                  System.out.println(" x = " + x);
                }
                if (dmin < -5 || dmax > 5) {
                  if (dbl) {
                    s = Double.toString(x);
                  } else {
                    s = Integer.toString((int) x);
                  }
                } else {
                  if (dbl) {
                    s = Tools.f(x);
                  } else {
                    s = Tools.f((int) x);
                  }
                }
                if (db) {
                  System.out.println("adding label " + val + " " + s);
                }
                labels.put(new Integer(val), new JLabel(s));
              }
              cs.setLabelTable(labels);
            }
          }
        }
        comp = cs;
        panel.add(cs);
      } else {
        //        if (label != null)
        //          Gfx.addBorder(panel, Gfx.BD_LINE_PAD, label, labelAlignment);
        //        label = null;
        //
        //        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        //
        //        panel.setLayout(layout);
        //        mySlider cs = null;
        //        //        if (!dbl) {
        //        //          cs = new mySlider((int) dmin, (int) dmax, (int) dvalue, (int)step);
        //        //        } else {
        //        cs = new mySlider(dbl, dmin, dmax, dvalue, step);
        //        //        }
        //
        //        cs.addChangeListener(this);
        //        if (label != null) {
        //          JLabel lbl = new JLabel(label);
        //          lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        //          panel.add(lbl);
        //        }
        //
        //        if (withTicks) {
        //          final boolean db = false;
        //          if (db) {
        //            System.out.println("slider ticks, dmax=" + dmax + ", dmin=" + dmin);
        //          }
        //          {
        //            int steps = 0;
        //            double r = dmax - dmin;
        //            if (dbl) {
        //              int nsteps = (int) (r / step);
        //              int stepV = (int) (r / nsteps);
        //
        //              if (nsteps <= 12) {
        //                cs.setMinorTickSpacing(stepV);
        //                cs.setMajorTickSpacing(2 * stepV);
        //                cs.createStandardLabels(2 * stepV);
        //                cs.setSnapToTicks(true);
        //              } else {
        //                nsteps = Math.min(nsteps, 50);
        //                double major = (r / 4 / step);
        //                double minor = major / 2;
        //
        //                cs.setMinorTickSpacing((int) minor);
        //                cs.setMajorTickSpacing((int) major);
        //                cs.createStandardLabels((int) (r / 4));
        //              }
        //            } else {
        //              steps = (int) (dmax + 1 - dmin);
        //
        //              if (steps <= 12) {
        //                cs.setMinorTickSpacing(1);
        //                cs.setMajorTickSpacing(2);
        //                cs.createStandardLabels(2);
        //                cs.setSnapToTicks(true);
        //              } else {
        //                cs.setMinorTickSpacing(steps / 20);
        //                cs.setMajorTickSpacing(steps / 4);
        //                cs.createStandardLabels(steps / 4);
        //              }
        //
        //            }
        //            if (db) {
        //              System.out.println(" steps=" + steps);
        //            }
        //            cs.setPaintTicks(true);
        //            cs.setPaintLabels(true);
        //
        //            if (false && steps > 12) {
        //              Dictionary labels = new Hashtable();
        //              for (int i = 0; i < 5; i++) {
        //                int val = (i * steps) / 4;
        //
        //                double x = (val * r) / steps + dmin;
        //                String s = null;
        //                if (db) {
        //                  System.out.println(" x = " + x);
        //                }
        //                if (dmin < -5 || dmax > 5) {
        //                  if (dbl) {
        //                    s = Double.toString(x);
        //                  } else {
        //                    s = Integer.toString((int) x);
        //                  }
        //                } else {
        //                  if (dbl) {
        //                    s = Tools.f(x);
        //                  } else {
        //                    s = Tools.f((int) x);
        //                  }
        //                }
        //                if (db) {
        //                  System.out.println("adding label " + val + " " + s);
        //                }
        //                labels.put(new Integer(val), new JLabel(s));
        //              }
        //              cs.setLabelTable(labels);
        //            }
        //          }
        //        }
        //        comp = cs;
        //        panel.add(cs);
      }
    }
    setComponent(panel);
  }

  private boolean isSlider;

  private class mySpinner extends JSpinner implements GadgetComponent {

    public Gadget getGadget() {
      return CtSpinner.this;
    }

    public mySpinner(int min, int max, int value, int step) {
      super(new SpinnerNumberModel(value, min, max, step));

      disableKeybd();

    }

    private void disableKeybd() {
    }

    private boolean dblFlag;

    public mySpinner(double dmin, double dmax, double dvalue, double step) {
      dblFlag = true;
      //Tools.warn("add special model to edit value in popup");
      super.setModel(new SpinnerNumberModel(dvalue, dmin, dmax, step));
      ((JSpinner.DefaultEditor) getEditor()).getTextField().setColumns(5);
      disableKeybd();

    }

    public void writeValue(Object v) {
      SpinnerNumberModel m = (SpinnerNumberModel) getModel();
      if (!dblFlag) {
        m.setValue(v);
      } else {
        double vd = ((Double) v).doubleValue();
        m.setValue(new Double(vd));
      }

    }

    public Object readValue() {
      Number n = ((SpinnerNumberModel) getModel()).getNumber();
      if (dblFlag)
        return new Double(n.doubleValue());
      else
        return new Integer(n.intValue());
    }

  }

  private class mySlider extends JSlider implements GadgetComponent {

    public Gadget getGadget() {
      return CtSpinner.this;
    }

    //    public mySlider(int min, int max, int value, int step) {
    //      super(min, max, value);
    //      if (NEWEXT)      this.setExtent(step);
    //
    //    }

    public mySlider(boolean dbls, double dmin, double dmax, double dvalue,
        double step) {
      dblFlag = dbls;
      double range = dmax - dmin;
      if (dbls)
        scale = 1 / step;
      else
        scale = 1;
      offset = dmin;
      this.setMaximum((int) (range * scale));
      this.setValue((int) ((dvalue - dmin) * scale));

      // note: sv works out to be exactly 1
      // but this seems to be ignored by the underlying BoundedRangeModel
      int sv = (int) (step * scale);
      setExtent(sv);

      //      if (true) {
      //        Tools.warn("testing...");
      //        BoundedRangeModel m = this.getModel();
      //        m.addChangeListener(new ChangeListener() {
      //
      //          public void stateChanged(ChangeEvent arg0) {
      //            Streams.out.println("stateChanged: "+arg0);
      //            
      //          }
      //        });
      //      }
    }

    private boolean dblFlag;
    private double scale;
    private double offset;

    public void writeValue(Object v) {
      if (dblFlag) {
        double vd = ((Double) v).doubleValue();
        getModel().setValue((int) ((vd - offset) * scale));
      } else {
        getModel().setValue(((Integer) v).intValue());
      }
    }

    public Object readValue() {
      if (dblFlag) {
        return new Double(getModel().getValue() / scale + offset);
      } else {
        return new Integer((int) (getModel().getValue() + offset));
      }
    }

  }
}
