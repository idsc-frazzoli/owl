// code by jl, ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Sign;

/** min time cost function with indecent heuristic
 * 
 * Note: class cannot be derived from Se2MinTimeGoalManager
 * because the se2 flows assume constant speed.
 * For Tse2, the min-time to reach goal formula is more complicated. */
public final class Tse2MinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final Tse2ComboRegion tse2ComboRegion;
  private final Scalar maxSpeed;
  private final Scalar maxTurning;

  public Tse2MinTimeGoalManager(Tse2ComboRegion tse2ComboRegion, Collection<Tensor> controls, Scalar maxSpeed) {
    super(tse2ComboRegion);
    this.tse2ComboRegion = tse2ComboRegion;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
    this.maxTurning = Tse2Controls.maxTurning(controls).multiply(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [] / maxTurning [s^-1] -> time [s]
    return Max.of( //
        tse2ComboRegion.d_xy(tensor).divide(maxSpeed), //
        tse2ComboRegion.d_angle(tensor).divide(maxTurning));
  }
}
