// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.rrts.core.RrtsTrajectoryPlanner;

/* package */ class RrtsMotionPlanWorker extends MotionPlanWorker<RrtsTrajectoryPlanner, RrtsPlannerCallback>{
  public RrtsMotionPlanWorker(int maxSteps, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    super(maxSteps, rrtsPlannerCallbacks);
  }

  protected void expand(RrtsTrajectoryPlanner trajectoryPlanner) {
    Expand expand = new Expand<>(trajectoryPlanner);
    expand.setContinued(() -> isRelevant);
    expand.steps(maxSteps);
    trajectoryPlanner.checkConsistency();
  }
}
