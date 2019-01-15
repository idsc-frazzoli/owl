// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.se2.Se2Controls;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;

/** minimizes driving time (=distance, since unit speed)
 * 
 * {@link Se2WrapMinTimeGoalManager} works with {@link Se2Wrap} as well as with {@link TnIdentityWrap} */
@Deprecated
/* package */ class Se2WrapMinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final TensorMetric tensorMetric;
  private final Tensor center;
  private final Scalar radius;
  private final Scalar maxSpeed;

  /** @param tensorMetric
   * @param center consists of x,y,theta
   * @param radius
   * @param controls */
  public Se2WrapMinTimeGoalManager(TensorMetric tensorMetric, Tensor center, Scalar radius, Collection<Flow> controls) {
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
    this.center = center;
    this.radius = radius;
    maxSpeed = Sign.requirePositive(Se2Controls.maxSpeed(controls));
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    // FIXME JPH wrong implementation! should be more like Se2MinTimeGoalManager
    return Ramp.of(tensorMetric.distance(x, center).subtract(radius)).divide(maxSpeed);
  }

  @Override // from Region
  public boolean isMember(Tensor x) {
    return Scalars.isZero(minCostToGoal(x));
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}