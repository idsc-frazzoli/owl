// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;

/* package */ class GlcMotionPlanWorker extends MotionPlanWorker<TrajectoryPlanner, GlcPlannerCallback> {
  public GlcMotionPlanWorker(int maxSteps, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    super(maxSteps, glcPlannerCallbacks);
  }

  @Override
  protected void expand(TrajectoryPlanner trajectoryPlanner) {
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.setContinued(() -> isRelevant);
    glcExpand.findAny(maxSteps);
  }
}
