// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public final class ApMinTimeGoalManager extends AbstractMinTimeGoalManager {
  private final ApComboRegion apComboRegion;
  private final Scalar maxSpeed;
  //private final Scalar maxTurning;

  public ApMinTimeGoalManager(ApComboRegion apComboRegion, Scalar maxSpeed) { // Collection<Flow> controls, 
    super(apComboRegion);
    this.apComboRegion = apComboRegion;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
    //this.maxTurning = ApControls.maxTurning(controls).multiply(maxSpeed);
  }
  
  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // Euklidian distance to spherical goal region
    return apComboRegion.d_xz(tensor);
  }
}
