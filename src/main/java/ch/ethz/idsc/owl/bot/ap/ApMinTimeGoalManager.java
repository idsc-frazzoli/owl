// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class ApMinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final ApComboRegion apComboRegion;
  private final Scalar maxSpeed;

  public ApMinTimeGoalManager(ApComboRegion apComboRegion, Scalar maxSpeed) {
    super(apComboRegion);
    this.apComboRegion = apComboRegion;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // Euclidian distance to goal region
    return apComboRegion.d_z(tensor).divide(maxSpeed);
  }
}
