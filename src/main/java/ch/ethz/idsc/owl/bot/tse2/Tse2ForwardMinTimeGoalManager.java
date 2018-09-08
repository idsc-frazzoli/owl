// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with indecent heuristic
 * 
 * Note: class cannot be derived from Se2MinTimeGoalManager
 * because the se2 flows assume constant speed.
 * For Tse2, the min-time to reach goal formula is more complicated. */
public final class Tse2ForwardMinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final Tse2ComboRegion tse2ComboRegion;
  private final Scalar v_max;
  private final Scalar maxTurning;
  @SuppressWarnings("unused")
  private final Tensor a_max;

  public Tse2ForwardMinTimeGoalManager(Tse2ComboRegion tse2ComboRegion, Collection<Flow> controls, Scalar v_max) {
    super(tse2ComboRegion);
    this.tse2ComboRegion = tse2ComboRegion;
    this.v_max = v_max;
    this.maxTurning = Tse2Controls.maxTurning(controls);
    this.a_max = Tse2Controls.maxAcc(controls);
    Tse2Controls.minAcc(controls).negate(); // TODO JPH check consistency
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [rad] / maxTurning [rad/s] -> time [s]
    // FIXME YN admissible but inaccurate heuristic -> use accelerations for a better bound
    return Max.of( //
        tse2ComboRegion.d_xy(tensor).divide(v_max), //
        tse2ComboRegion.d_angle(tensor).divide(maxTurning.multiply(v_max)));
  }
}
