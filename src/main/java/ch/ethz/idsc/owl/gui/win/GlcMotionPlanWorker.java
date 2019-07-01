// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;

public class GlcMotionPlanWorker extends MotionPlanWorker<GlcTrajectoryPlanner, GlcPlannerCallback> {

  public GlcMotionPlanWorker(int maxSteps, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    super(maxSteps, glcPlannerCallbacks);
  }

  protected void expand(GlcTrajectoryPlanner trajectoryPlanner) {
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.setContinued(() -> isRelevant);
    glcExpand.findAny(maxSteps);
  }
}
