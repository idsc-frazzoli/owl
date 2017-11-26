// code by jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryGoalManager;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/** objective is minimum time */
public class RnTrajectoryGoalManager extends TrajectoryGoalManager {
  private final List<StateTime> heuristicTrajectory;
  private final Scalar radius;

  public RnTrajectoryGoalManager(List<Region<Tensor>> goalRegions, List<StateTime> heuristicTrajectory, Tensor radius) {
    super(goalRegions);
    this.heuristicTrajectory = heuristicTrajectory;
    if (!radius.Get(0).equals(radius.Get(1)))
      throw TensorRuntimeException.of(radius); // x-y radius have to be equal
    this.radius = radius.Get(0);
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return Ramp.of(Norm._2.between(x, Lists.getLast(heuristicTrajectory).state()).subtract(radius)//
        .divide(RealScalar.ONE)); // divide by maximum "speed"
  }
}
