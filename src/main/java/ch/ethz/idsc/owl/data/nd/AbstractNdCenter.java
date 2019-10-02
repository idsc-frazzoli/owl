// code by jph, gjoel
package ch.ethz.idsc.owl.data.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public AbstractNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }
}
