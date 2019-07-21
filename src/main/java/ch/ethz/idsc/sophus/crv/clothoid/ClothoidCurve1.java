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
    // function value at t / 2 TIMES size of interval [0, t]
    return clothoidQuadratic.exp_i(HALF.multiply(t)).multiply(t);
  }

  @Override // from ClothoidCurve
  protected Scalar ir(Scalar t) {
    // function value at midpoint of interval [t, 1] == (t + 1) / 2 TIMES size of interval [t, 1]
    Scalar _1_t = _1.subtract(t);
    return clothoidQuadratic.exp_i(HALF.multiply(_1_t).add(t)).multiply(_1_t);
  }
}
