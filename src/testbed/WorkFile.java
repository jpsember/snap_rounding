package testbed;
import javax.swing.*;
import java.awt.*;

  class WorkFile extends JFrame {

  public WorkFile() {
     JFrame.setDefaultLookAndFeelDecorated(true);
//     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Workspace");

     w = new Workspace(-1, this);
     w.fixTitle();

     getContentPane().add(w.component(), BorderLayout.CENTER);
     pack();
//     setVisible(true);
   }
  public Dimension getPreferredSize() {
    return new Dimension(300,600);
  }
  private Workspace w;

}
