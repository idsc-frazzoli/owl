// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ class ApMinTimeGoalManager extends AbstractMinTimeGoalManager {
  // ---
  private final ApComboRegion apComboRegion;
  private final Scalar maxVerticalSpeed;

  public ApMinTimeGoalManager(ApComboRegion apComboRegion, Scalar maxVerticalSpeed) {
    super(apComboRegion);
    this.apComboRegion = apComboRegion;
    this.maxVerticalSpeed = Abs.of(maxVerticalSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // Euclidian distance to goal region
    return apComboRegion.d_z(tensor).divide(maxVerticalSpeed);
  }
}
