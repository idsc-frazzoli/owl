// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ abstract class LimitedClothoidNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;
  private final Clip clip;

  public LimitedClothoidNdCenter(Tensor center, Clip clip) {
    this.center = center.copy().unmodifiable();
    this.clip = clip;
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }

  @Override // from ClothoidNdCenter
  public final Scalar ofVector(Tensor p) {
    Clothoid clothoid = clothoid(p);
    Scalar cost = clothoid.length();
    return clip.isInside(clothoid.curvature().absMax()) //
        ? cost
        : infinity(cost);
  }

  /** @param other
   * @return clothoid either from center to other, or from other to center */
  protected abstract Clothoid clothoid(Tensor other);

  private static Scalar infinity(Scalar cost) {
    return cost instanceof Quantity //
        ? Quantity.of(DoubleScalar.POSITIVE_INFINITY, ((Quantity) cost).unit()) //
        : DoubleScalar.POSITIVE_INFINITY;
  }
}
