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
 * The cost does not account for curvature. */
public final class Tse2MinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final Tse2ComboRegion tse2ComboRegion;
  private final Scalar maxSpeed;
  private final Scalar maxTurning;
  private final Tensor minmaxAcc; // TODO make use of max accelerations for heuristic

  public Tse2MinTimeGoalManager(Tse2ComboRegion se2ComboRegion, Collection<Flow> controls, Scalar maxSpeed) {
    this.tse2ComboRegion = se2ComboRegion;
    this.maxSpeed = maxSpeed; // TODO max speed for forward and reverse
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
    return Max.of( //
        tse2ComboRegion.d_xy(tensor).divide(maxSpeed), // FIXME admissible but inacurate heuristic
        tse2ComboRegion.d_angle(tensor).divide(maxTurning));
  }

  @Override // from Region
  public boolean isMember(Tensor xyav) {
    return tse2ComboRegion.isMember(xyav);
  }

  public final GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
