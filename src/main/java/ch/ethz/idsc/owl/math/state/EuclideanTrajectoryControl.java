// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public class EuclideanTrajectoryControl extends StateTrajectoryControl {
  public EuclideanTrajectoryControl(Tensor fallback) {
    super(fallback);
  }

  @Override // from StateTrajectoryControl
  protected final Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y);
  }
}
