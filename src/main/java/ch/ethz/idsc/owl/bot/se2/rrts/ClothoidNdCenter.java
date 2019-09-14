// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class ClothoidNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public ClothoidNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor other) {
    return ClothoidParametricDistance.distance(clothoid(other));
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }

  /** @param other
   * @return clothoid either from center to other, or from other to center */
  protected abstract Clothoid clothoid(Tensor other);
}
