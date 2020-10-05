// code by jph, gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.nd.NdCenterInterface;

/* package */ abstract class AbstractNdCenter implements NdCenterInterface, Serializable {
  private static final long serialVersionUID = -7918796578735036405L;
  // ---
  private final Tensor center;

  public AbstractNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }
}
