// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public class EuclideanTrajectoryControl extends StateTrajectoryControl {
  public static final TrajectoryControl INSTANCE = new EuclideanTrajectoryControl();

  private EuclideanTrajectoryControl() {
  }

  @Override // from StateTrajectoryControl
  protected final Scalar pseudoDistance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y);
  }
}
