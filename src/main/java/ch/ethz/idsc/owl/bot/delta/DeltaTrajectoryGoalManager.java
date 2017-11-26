// code by jl
package ch.ethz.idsc.owl.bot.delta;

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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

public class DeltaTrajectoryGoalManager extends TrajectoryGoalManager {
  private final List<StateTime> heuristicTrajectory;
  private final Scalar radius;
  private final Scalar maxSpeed;
  private final Scalar timeCostScalingFactor;

  public DeltaTrajectoryGoalManager(List<Region<Tensor>> goalRegions, List<StateTime> heuristicTrajectory, Tensor radius, Scalar maxSpeed,
      Scalar timeCostScalingFactor) {
    super(goalRegions);
    this.heuristicTrajectory = heuristicTrajectory;
    if (!radius.Get(0).equals(radius.Get(1)))
      throw TensorRuntimeException.of(radius); // x-y radius have to be equal
    this.radius = radius.Get(0);
    this.maxSpeed = maxSpeed;
    this.timeCostScalingFactor = timeCostScalingFactor;
  }

  // Constructor with Default value in CostScaling
  public DeltaTrajectoryGoalManager(List<Region<Tensor>> goalRegions, List<StateTime> heuristicTrajectory, Tensor radius, Scalar maxSpeed) {
    this(goalRegions, heuristicTrajectory, radius, maxSpeed, RealScalar.ONE);
  }

  public DeltaTrajectoryGoalManager(List<Region<Tensor>> goalRegions, List<StateTime> heuristicTrajectory, Scalar maxSpeed) {
    this(goalRegions, heuristicTrajectory, Tensors.vector(0, 0), maxSpeed, RealScalar.ONE);
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Scalar sum = Norm._2.ofVector(flow.getU()).add(timeCostScalingFactor);
    // Costfunction: integrate (u^2 +1, t)
    return sum.multiply(StateTimeTrajectories.timeIncrement(glcNode, trajectory));
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    // B. Paden: A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning
    // p. 79 Eq: 6.4.14
    // Heuristic needs to be underestimating: (Euclideandistance-radius) / (MaxControl+Max(|Vectorfield|)
    // return RealScalar.ZERO;
    return Ramp.of(Norm._2.between(x, Lists.getLast(heuristicTrajectory).state()) //
        .subtract(radius).divide(maxSpeed));
  }
}
