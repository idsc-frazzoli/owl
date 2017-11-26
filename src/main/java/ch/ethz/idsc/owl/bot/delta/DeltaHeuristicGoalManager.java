// code by jl
package ch.ethz.idsc.owl.bot.delta;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

public class DeltaHeuristicGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  private final Tensor center;
  private final Scalar radius;
  private final Scalar maxSpeed;
  private final Scalar timeCostScalingFactor;

  // Constructor with Default value in CostScaling
  public DeltaHeuristicGoalManager(Tensor center, Tensor radius, Scalar maxSpeed) {
    this(center, radius, maxSpeed, RealScalar.ONE);
  }

  public DeltaHeuristicGoalManager(Tensor center, Tensor radius, Scalar maxSpeed, Scalar timeCostScalingFactor) {
    super(new TimeInvariantRegion(new EllipsoidRegion(center, radius)));
    this.center = center;
    this.maxSpeed = maxSpeed;
    if (!radius.Get(0).equals(radius.Get(1)))
      throw TensorRuntimeException.of(radius); // x-y radius have to be equal
    this.radius = radius.Get(0);
    this.timeCostScalingFactor = timeCostScalingFactor;
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    // 2Norm of flow.getU() used for future implemenation of a relative speed of the boat (inputNorm)
    Scalar sum = Norm._2.ofVector(flow.getU()).add(timeCostScalingFactor);
    // Costfunction: integrate (|u| +1, t)
    return sum.multiply(StateTimeTrajectories.timeIncrement(glcNode, trajectory));
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    // B. Paden: A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning
    // p. 79 Eq: 6.4.14
    // Heuristic needs to be underestimating: (Euclideandistance-radius) / (MaxControl+Max(|Vectorfield|)
    return Ramp.of(Norm._2.between(x, center).subtract(radius).divide(maxSpeed));
  }
}
