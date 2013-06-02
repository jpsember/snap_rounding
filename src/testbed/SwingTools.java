package testbed;
import java.awt.*;

  class SwingTools {
  public static GridBagConstraints setGBC(int gx,
                                            int gy,
                                            int gw, int gh, int wx, int wy) {
      GridBagConstraints gbc = new GridBagConstraints();

      gbc.gridx = gx;
      gbc.gridy = gy;
      gbc.gridwidth = gw;
      gbc.gridheight = gh;
      gbc.weightx = wx;
      gbc.weighty = wy;

      gbc.fill = GridBagConstraints.BOTH;
      return gbc;
    }

  private static Font monoFont;
  static Font monoFont() {
    if (monoFont == null) {
      monoFont = new Font("Monospaced", Font.PLAIN, 16);
    }
    return monoFont;
  }


}
