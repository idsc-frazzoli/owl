// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.StateTimeCoordinateWrap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TemporalTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
class CarxTEntity extends CarEntity {
  CarxTEntity(StateTime stateTime) {
    super(stateTime, TemporalTrajectoryControl.INSTANCE, PARTITIONSCALE, CARFLOWS, SHAPE);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(2.0);
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(obstacleQuery, goal);
    trajectoryPlanner.represent = new StateTimeCoordinateWrap(SE2WRAP);
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    Scalar dt = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    return super.eta().copy().append(dt.reciprocal());
  }
}
