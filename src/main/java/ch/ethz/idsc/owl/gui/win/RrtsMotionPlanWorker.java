// code by jph, gjoel
package ch.ethz.idsc.owl.gui.win;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.TransitionPlanner;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Min;

/* package */ class RrtsMotionPlanWorker extends MotionPlanWorker<TransitionPlanner, RrtsPlannerCallback> {
  private final Scalar delayHint;

  public RrtsMotionPlanWorker(int maxSteps, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    super(maxSteps, rrtsPlannerCallbacks);
    delayHint = rrtsPlannerCallbacks.stream().filter(rrtsPlannerCallback -> rrtsPlannerCallback instanceof TrajectoryEntity) //
        .map(rrtsPlannerCallback -> ((TrajectoryEntity) rrtsPlannerCallback).delayHint()).reduce(Min::of).orElse(null);
  }

  @Override
  protected void expand(TransitionPlanner trajectoryPlanner) {
    Expand<RrtsNode> expand = new Expand<>(trajectoryPlanner);
    expand.setContinued(() -> isRelevant);
    if (Objects.nonNull(delayHint))
      expand.maxTime(RealScalar.of(.5));
    else
      expand.steps(maxSteps);
    trajectoryPlanner.checkConsistency();
  }
}
