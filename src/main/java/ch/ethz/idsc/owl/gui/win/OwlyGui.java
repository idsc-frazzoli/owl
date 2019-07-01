// code by jph
package ch.ethz.idsc.owl.gui.win;

import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;

public enum OwlyGui {
  ;
  public static OwlyFrame start() {
    OwlyFrame owlyFrame = new OwlyFrame();
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }

  public static OwlyFrame glc(GlcTrajectoryPlanner trajectoryPlanner) {
    OwlyFrame owlyFrame = new OwlyFrame();
    owlyFrame.setGlc(trajectoryPlanner);
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }
}
