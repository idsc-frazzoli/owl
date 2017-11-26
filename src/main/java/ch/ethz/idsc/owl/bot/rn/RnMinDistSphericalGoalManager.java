// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.data.DontModify;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/** objective is minimum path length.
 * path length is measured in Euclidean distance using Norm._2::ofVector.
 * the distance is independent from the max speed.
 * 
 * <p>The distance cost function is unsuitable for entities that are capable
 * and may need to linger in one spot (u == {0, 0}) because in that case
 * the cost == distance traveled evaluates to 0.
 * 
 * @see SphericalRegion */
@DontModify
public class RnMinDistSphericalGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** creates a spherical region in R^n with given center and radius.
   * min distance to goal is measured in Euclidean distance.
   * the distance is independent from the max speed.
   * 
   * @param center vector with length == n
   * @param radius positive */
  public static GoalInterface create(Tensor center, Scalar radius) {
    return new RnMinDistSphericalGoalManager(new SphericalRegion(center, radius));
  }
  // ---

  private final SphericalRegion sphericalRegion;

  /** @param sphericalRegion */
  public RnMinDistSphericalGoalManager(SphericalRegion sphericalRegion) {
    super(new TimeInvariantRegion(sphericalRegion));
    this.sphericalRegion = sphericalRegion;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return Norm._2.between(glcNode.stateTime().state(), Lists.getLast(trajectory).state()); // ||x_prev - x_next||
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    // max(0, ||x - center|| - radius)
    return Ramp.of(sphericalRegion.apply(x));
  }
}