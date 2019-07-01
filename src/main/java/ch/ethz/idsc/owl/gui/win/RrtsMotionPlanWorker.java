// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.rrts.core.RrtsTrajectoryPlanner;

public class RrtsMotionPlanWorker extends MotionPlanWorker<RrtsTrajectoryPlanner, RrtsPlannerCallback>{
  public RrtsMotionPlanWorker(int maxSteps, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    super(maxSteps, rrtsPlannerCallbacks);
  }

  protected void expand(RrtsTrajectoryPlanner trajectoryPlanner) {
    trajectoryPlanner.getProcess().ifPresent(process -> process.run(maxSteps));
  }
}
