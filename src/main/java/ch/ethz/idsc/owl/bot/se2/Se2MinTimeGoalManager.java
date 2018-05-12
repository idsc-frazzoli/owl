// code by jl
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.data.DontModify;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with decent heuristic
 * 
 * The cost does not account for curvature. */
// DO NOT MODIFY THIS CLASS SINCE THE FUNCTIONALITY IS USED IN MANY DEMOS
@DontModify
public final class Se2MinTimeGoalManager extends Se2AbstractGoalManager {
  /** the use of {@link #create(Tensor, Tensor, Collection)}
   * is preferred over the constructor
   * 
   * @param goal {px, py, angle}
   * @param radiusVector {dist_radius, dist_radius, dist_angle}
   * @param controls */
  public static GoalInterface create(Tensor goal, Tensor radiusVector, Collection<Flow> controls) {
    return new Se2MinTimeGoalManager(goal, radiusVector, controls).getGoalInterface();
  }

  public static GoalInterface create(RegionWithDistance<Tensor> regionWithDistance, Scalar angle, Scalar angle_tolerance, Collection<Flow> controls) {
    So2Region so2Region = new So2Region(angle, angle_tolerance);
    return new Se2MinTimeGoalManager(regionWithDistance, so2Region, controls).getGoalInterface();
  }
  // ---

  private final Scalar maxSpeed;
  private final Scalar maxTurning;

  public Se2MinTimeGoalManager(Tensor goal, Tensor radiusVector, Collection<Flow> controls) {
    super(goal, radiusVector);
    maxSpeed = Se2Controls.maxSpeed(controls);
    maxTurning = Se2Controls.maxTurning(controls);
  }

  private Se2MinTimeGoalManager(RegionWithDistance<Tensor> regionWithDistance, So2Region so2Region, Collection<Flow> controls) {
    super(regionWithDistance, so2Region);
    maxSpeed = Se2Controls.maxSpeed(controls);
    maxTurning = Se2Controls.maxTurning(controls);
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_ax [m] / maxSpeed [m/s] -> time [s]
    return Max.of(d_xy(tensor).divide(maxSpeed), d_angle(tensor).divide(maxTurning));
  }
}
