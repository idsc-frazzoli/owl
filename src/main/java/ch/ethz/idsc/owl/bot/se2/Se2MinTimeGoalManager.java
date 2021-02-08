// code by jl
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

/** min time cost function with decent heuristic
 * 
 * The cost does not account for other criteria such as curvature. */
public final class Se2MinTimeGoalManager extends AbstractMinTimeGoalManager {
  // ---
  private final Se2ComboRegion se2ComboRegion;
  private final Scalar maxSpeed;
  private final Scalar maxTurning;

  public Se2MinTimeGoalManager(Se2ComboRegion se2ComboRegion, Collection<Tensor> controls) {
    super(se2ComboRegion);
    this.se2ComboRegion = se2ComboRegion;
    maxSpeed = Se2Controls.maxSpeed(controls);
    maxTurning = Se2Controls.maxTurning(controls);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // units: d_xy [m] / maxSpeed [m/s] -> time [s]
    // units: d_an [] / maxTurning [s^-1] -> time [s]
    return Max.of( //
        se2ComboRegion.d_xy(tensor).divide(maxSpeed), //
        se2ComboRegion.d_angle(tensor).divide(maxTurning));
  }
}
