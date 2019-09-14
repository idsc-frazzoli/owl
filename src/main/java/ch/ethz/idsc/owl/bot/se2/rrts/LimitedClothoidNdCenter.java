// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid.Curvature;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidParametricDistance;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ abstract class LimitedClothoidNdCenter extends ClothoidNdCenter {
  private final Clip clip;

  public LimitedClothoidNdCenter(Tensor center, Clip clip) {
    super(center);
    this.clip = clip;
  }

  @Override // from ClothoidNdCenter
  public Scalar ofVector(Tensor p) {
    Clothoid clothoid = clothoid(p);
    Scalar cost = ClothoidParametricDistance.distance(clothoid);
    Curvature curvature = clothoid.new Curvature();
    try {
      if (clip.isInside(curvature.head()) && //
          clip.isInside(curvature.tail()))
        return cost;
    } catch (Exception exception) {
      // ---
    }
    return infinity(cost);
  }

  private static Scalar infinity(Scalar cost) {
    return cost instanceof Quantity //
        ? Quantity.of(DoubleScalar.POSITIVE_INFINITY, ((Quantity) cost).unit()) //
        : DoubleScalar.POSITIVE_INFINITY;
  }
}
