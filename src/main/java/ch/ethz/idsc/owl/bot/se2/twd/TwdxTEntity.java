// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** two wheel drive entity with state space augmented with time */
/* package */ class TwdxTEntity extends TwdEntity {
  public TwdxTEntity(TwdDuckieFlows twdConfig, StateTime stateTime) {
    super(stateTime, new TwdTrajectoryControl(), twdConfig); // LONGTERM choice of traj ctrl was not thorough
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.1);
  }

  @Override
  public TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    TrajectoryPlanner trajectoryPlanner = super.createTrajectoryPlanner(plannerConstraint, goal);
    trajectoryPlanner.represent = StateTime::joined;
    return trajectoryPlanner;
  }

  @Override
  protected Tensor eta() {
    Scalar dt = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    return super.eta().copy().append(dt.reciprocal());
  }
}
