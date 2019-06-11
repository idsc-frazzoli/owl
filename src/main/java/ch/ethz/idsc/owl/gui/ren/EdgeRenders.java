// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;

import ch.ethz.idsc.owl.glc.rl2.RelaxedDebugUtils;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;

public enum EdgeRenders {
  ;
  private static final int LIMIT = 2000;
  private static final Color COLOR = new Color(0, 0, 255, 128);

  public static RenderInterface of(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    EdgeRender edgeRender = new EdgeRender(LIMIT, COLOR);
    edgeRender.setCollection(RelaxedDebugUtils.allNodes(relaxedTrajectoryPlanner));
    return edgeRender.getRender();
  }
}
