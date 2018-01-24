// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class R2xTEntity extends R2Entity {
  private final Scalar delay;

  public R2xTEntity(TrajectoryControl trajectoryControl, Scalar delay) {
    super(trajectoryControl);
    // represent_entity = StateTime::joined;
    this.delay = delay;
  }

  @Override
  public Scalar delayHint() {
    return delay;
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(obstacleQuery, goal);
    trajectoryPlanner.represent = StateTime::joined;
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    Scalar dt = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    return super.eta().copy().append(dt.reciprocal());
  }

  @Override
  Collection<Flow> createControls() {
    /** 36 corresponds to 10[Degree] resolution */
    Collection<Flow> collection = super.createControls();
    collection.add(r2Flows.stayPut()); // <- does not go well with min-dist cost function
    return collection;
  }
}
