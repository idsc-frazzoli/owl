// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class ClothoidNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public ClothoidNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    return PseudoClothoidDistance.INSTANCE.distance(vector, center);
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }
}
