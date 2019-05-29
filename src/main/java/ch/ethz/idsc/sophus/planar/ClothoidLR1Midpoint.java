// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum ClothoidLR1Midpoint implements MidpointInterface {
  INSTANCE;
  // ---
  @Override // from MidpointInterface
  public Tensor midpoint(Tensor p, Tensor q) {
    return ClothoidCurve.INSTANCE.split(p, q, RationalScalar.HALF);
  }
}
