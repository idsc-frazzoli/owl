// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.data.DontModify;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;

/** objective is minimum time.
 * 
 * <p>The distance cost function is suitable for entities that are capable
 * and may need to linger in one spot (u == {0, 0}) because in that case
 * the cost == distance traveled evaluates a non-zero, positive value.
 * 
 * <p>The goal region underlying the target area as well as the heuristic is
 * {@link SphericalRegion}. */
@DontModify
public class RnMinTimeGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** creates a spherical region in R^n with given center and radius.
   * 
   * @param center vector with length == n
   * @param radius positive
   * @param controls */
  public static GoalInterface create(Tensor center, Scalar radius, Collection<Flow> controls) {
    return new RnMinTimeGoalManager(new SphericalRegion(center, radius), controls);
  }
  // ---

  private final SphericalRegion sphericalRegion;
  private final Scalar maxSpeed;

  private RnMinTimeGoalManager(SphericalRegion sphericalRegion, Collection<Flow> controls) {
    super(new TimeInvariantRegion(sphericalRegion));
    this.sphericalRegion = sphericalRegion;
    maxSpeed = RnControls.maxSpeed(controls);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    // max(0, ||x - center|| - radius) / maxSpeed
    return Ramp.of(sphericalRegion.signedDistance(x).divide(maxSpeed));
  }
}