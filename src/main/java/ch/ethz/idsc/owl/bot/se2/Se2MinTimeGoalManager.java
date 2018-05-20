// code by jl
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.data.DontModify;
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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with decent heuristic
 * 
 * The cost does not account for curvature. */
// DO NOT MODIFY THIS CLASS SINCE THE FUNCTIONALITY IS USED IN MANY DEMOS
@DontModify
public final class Se2MinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final Se2ComboRegion se2ComboRegion;
  private final Scalar maxSpeed;
  private final Scalar maxTurning;

  public Se2MinTimeGoalManager(Se2ComboRegion se2ComboRegion, Collection<Flow> controls) {
    this.se2ComboRegion = se2ComboRegion;
    maxSpeed = Se2Controls.maxSpeed(controls);
    maxTurning = Se2Controls.maxTurning(controls);
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [rad] / maxTurning [rad/s] -> time [s]
    return Max.of( //
        se2ComboRegion.d_xy(tensor).divide(maxSpeed), //
        se2ComboRegion.d_angle(tensor).divide(maxTurning));
  }

  @Override
  public boolean isMember(Tensor xya) {
    return se2ComboRegion.isMember(xya);
  }

  public final GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
