// code by jl, ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with indecent heuristic
 * 
 * Note: class cannot be derived from Se2MinTimeGoalManager
 * because the se2 flows assume constant speed.
 * For Tse2, the min-time to reach goal formula is more complicated. */
public final class Tse2MinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final Tse2ComboRegion tse2ComboRegion;
  private final Scalar maxSpeed;
  private final Scalar maxTurning;
  @SuppressWarnings("unused")
  private final Tensor minmaxAcc; // TODO YN make use of max accelerations for heuristic

  public Tse2MinTimeGoalManager(Tse2ComboRegion tse2ComboRegion, Collection<Flow> controls, Scalar maxSpeed) {
    this.tse2ComboRegion = tse2ComboRegion;
    this.maxSpeed = maxSpeed; // TODO YN max speed for forward and reverse
    this.maxTurning = Tse2Controls.maxTurning(controls);
    this.minmaxAcc = Tensors.of(Tse2Controls.minAcc(controls), Tse2Controls.maxAcc(controls));
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [rad] / maxTurning [rad/s] -> time [s]
    // FIXME YN admissible but inaccurate heuristic -> use accelerations for a better bound
    return Max.of( //
        tse2ComboRegion.d_xy(tensor).divide(maxSpeed), //
        tse2ComboRegion.d_angle(tensor).divide(maxTurning.multiply(maxSpeed)));
  }

  @Override // from Region
  public boolean isMember(Tensor xyav) {
    return tse2ComboRegion.isMember(xyav);
  }

  public final GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
