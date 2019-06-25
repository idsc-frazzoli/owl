// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum ClothoidLR1Midpoint implements MidpointInterface {
  INSTANCE;
  // ---
  @Override // from MidpointInterface
  public Tensor midpoint(Tensor p, Tensor q) {
    return Clothoid2.INSTANCE.split(p, q, RationalScalar.HALF);
  }
}
