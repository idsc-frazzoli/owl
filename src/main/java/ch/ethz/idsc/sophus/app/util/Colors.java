// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JProgressBar;

/** static functionality */
/* package */ enum Colors {
  ;
  /** JToggleButton background when selected is 184 207 229 selection color
   * subtracts 24 from each RGB value */
  public static final Color SELECTION = new Color(160, 183, 205);
  public static final Color SELECTION_BRIGHTER = new Color(182, 199, 216);
  //
  /** imitates color of {@link JProgressBar} text */
  public static final Color PROGRESS_BAR = new Color(99, 130, 191);
  /** background color of java native dialogs, e.g. JFileChooser color can replace
   * gradient paint of {@link JButton}s */
  public static final Color PANEL = new Color(238, 238, 238);
  /** approximation of color gold */
  public static final Color GOLD = new Color(224, 149, 4);
  /** foreground color of JLabel */
  public static final Color LABEL = new Color(51, 51, 51);
  /** background for items in menus that are selected; not Java official */
  public static final Color ACTIVE_ITEM = new Color(243, 239, 124);

  public static Color alpha064(Color color) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), 64);
  }

  public static Color alpha128(Color color) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
  }

  public static Color withAlpha(Color color, int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
  }
}
