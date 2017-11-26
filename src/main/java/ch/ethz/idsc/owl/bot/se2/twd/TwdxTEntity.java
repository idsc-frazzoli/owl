// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** two wheel drive entity with state space augmented with time */
/* package */ class TwdxTEntity extends TwdEntity {
  private static final Tensor PARTITIONSCALE = Tensors.vector(6, 6, 50 / Math.PI, 4).unmodifiable(); // 50/pi == 15.9155

  public TwdxTEntity(TwdDuckieFlows twdConfig, StateTime stateTime) {
    super(twdConfig, stateTime);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.1);
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(obstacleQuery, goal);
    trajectoryPlanner.represent = StateTime::joined;
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    return PARTITIONSCALE;
  }
}
