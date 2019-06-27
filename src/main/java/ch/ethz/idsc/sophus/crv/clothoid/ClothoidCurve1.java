// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** 1-point Gauss Legendre quadrature on interval [0, 1] */
/* package */ class ClothoidCurve1 extends ClothoidCurve {
  private static final Scalar HALF = RealScalar.of(0.5);

  // ---
  public ClothoidCurve1(Tensor p, Tensor q) {
    super(p, q);
  }

  @Override // from ClothoidCurve
  protected Scalar il(Scalar t) {
    return clothoidQuadratic.apply(HALF.multiply(t)).multiply(t);
  }

  @Override // from ClothoidCurve
  protected Scalar ir(Scalar t) {
    Scalar _1_t = _1.subtract(t);
    return clothoidQuadratic.apply(HALF.multiply(_1_t).add(t)).multiply(_1_t);
  }
}
