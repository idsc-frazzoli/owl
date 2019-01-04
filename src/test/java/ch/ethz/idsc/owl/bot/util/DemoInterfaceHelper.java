// code by jph
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.owl.gui.win.BaseFrame;

public enum DemoInterfaceHelper {
  ;
  public static void brief(DemoInterface demoInterface) {
    BaseFrame baseFrame = demoInterface.start();
    baseFrame.jFrame.setVisible(true);
    try {
      Thread.sleep(400);
    } catch (Exception exception) {
      // ---
    }
    baseFrame.jFrame.setVisible(false);
  }
}
