// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with indecent heuristic
 * 
 * Note: class cannot be derived from Se2MinTimeGoalManager
 * because the se2 flows assume constant speed.
 * For Tse2, the min-time to reach goal formula is more complicated. */
public final class Tse2ForwardMinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final Tse2ComboRegion tse2ComboRegion;
  private final Scalar maxTurning;
  private final LinearVelocity2MinTime linearVelocity2MinTime;

  /** @param tse2ComboRegion
   * @param controls
   * @throws Exception if permitted velocity region is not an interval of the form [0, v_max] */
  public Tse2ForwardMinTimeGoalManager(Tse2ComboRegion tse2ComboRegion, Collection<Tensor> controls) {
    super(tse2ComboRegion);
    this.tse2ComboRegion = tse2ComboRegion;
    if (Scalars.nonZero(tse2ComboRegion.v_range().min()))
      throw TensorRuntimeException.of(tse2ComboRegion.v_range().min());
    Scalar v_max = tse2ComboRegion.v_range().max();
    this.maxTurning = Tse2Controls.maxTurning(controls).multiply(v_max);
    Scalar a_min = Tse2Controls.minAcc(controls);
    Scalar a_max = Tse2Controls.maxAcc(controls);
    if (!a_max.equals(a_min.negate()))
      throw TensorRuntimeException.of(a_min, a_max);
    linearVelocity2MinTime = new LinearVelocity2MinTime(v_max, a_max);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [] / maxTurning [s^-1] -> time [s]
    Scalar d_tar = tse2ComboRegion.d_xy(tensor);
    Scalar v_cur = tensor.Get(Tse2StateSpaceModel.STATE_INDEX_VEL);
    return Max.of( //
        linearVelocity2MinTime.minTime(d_tar, v_cur), //
        tse2ComboRegion.d_angle(tensor).divide(maxTurning));
  }
}
