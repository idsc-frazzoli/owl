// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.IdentityWrap;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.TensorMetric;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Ramp;

/** minimizes driving time (=distance, since unit speed)
 * 
 * {@link Se2WrapGoalManagerExt} works with {@link Se2Wrap} as well as with {@link IdentityWrap} */
public class Se2WrapGoalManagerExt implements Region<Tensor>, CostFunction, Serializable {
  private final TensorMetric tensorMetric;
  private final Se2AbstractGoalManager goalManager;

  /** @param tensorMetric
   * @param goalManager */
  public Se2WrapGoalManagerExt(TensorMetric tensorMetric, Se2AbstractGoalManager goalManager) {
    this.tensorMetric = tensorMetric;
    this.goalManager = goalManager;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode node, List<StateTime> trajectory, Flow flow) {
    return goalManager.costIncrement(node, trajectory, flow);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return Ramp.of(tensorMetric.distance(x, goalManager.center).subtract(goalManager.radiusSpace()));
  }

  @Override // from Region
  public boolean isMember(Tensor x) {
    return Scalars.isZero(Ramp.of( //
        tensorMetric.distance(x, goalManager.center).subtract( //
            tensorMetric.distance(Tensors.vector(0, 0, 0), goalManager.radiusVector))));
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}