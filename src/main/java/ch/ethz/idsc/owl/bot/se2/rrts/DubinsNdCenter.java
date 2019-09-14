// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class DubinsNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public DubinsNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public final Scalar ofVector(Tensor other) {
    return dubinsPath(other).length();
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }

  /** @param other
   * @return clothoid either from center to other, or from other to center */
  protected abstract DubinsPath dubinsPath(Tensor other);
}
