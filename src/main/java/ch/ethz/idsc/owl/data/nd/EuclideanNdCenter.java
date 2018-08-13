// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class EuclideanNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public EuclideanNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    return Norm._2.between(vector, center);
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }
}
