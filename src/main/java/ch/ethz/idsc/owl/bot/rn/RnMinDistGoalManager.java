// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

/** objective is minimum path length.
 * path length is measured in Euclidean distance using Norm._2::ofVector.
 * the distance is independent from the max speed.
 * 
 * <p>The distance cost function is unsuitable for entities that are capable
 * and may need to linger in one spot (u == {0, 0}) because in that case
 * the cost == distance traveled evaluates to 0.
 * 
 * @see BallRegion */
public class RnMinDistGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** creates a spherical region in R^n with given center and radius.
   * min distance to goal is measured in Euclidean distance.
   * the distance is independent from the max speed.
   * 
   * @param center vector with length == n
   * @param radius positive */
  public static GoalInterface sperical(Tensor center, Scalar radius) {
    return new RnMinDistGoalManager(new BallRegion(center, radius));
  }

  /***************************************************/
  private final RegionWithDistance<Tensor> regionWithDistance;

  /** @param regionWithDistance */
  public RnMinDistGoalManager(RegionWithDistance<Tensor> regionWithDistance) {
    super(new TimeInvariantRegion(regionWithDistance));
    this.regionWithDistance = regionWithDistance;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    // assumes that flow is along a straight line
    return Vector2Norm.between(glcNode.stateTime().state(), Lists.getLast(trajectory).state()); // ||x_prev - x_next||
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return regionWithDistance.distance(x);
  }
}