// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// FIXME GJOEL/JPH the formula is not symmetric! ingoing != outgoing
/* package */ abstract class ClothoidNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public ClothoidNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor p) {
    return ClothoidParametricDistance.distance(clothoid(p));
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }

  public abstract Clothoid clothoid(Tensor p);
}
