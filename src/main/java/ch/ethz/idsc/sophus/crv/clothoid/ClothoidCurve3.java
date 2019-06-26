// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** 3-point Gauss Legendre quadrature on interval [0, 1] */
/* package */ class ClothoidCurve3 extends ClothoidCurve {
  private static final Tensor W = Tensors.vector(5, 8, 5).divide(RealScalar.of(18.0));
  private static final Tensor X = Tensors.vector(-1, 0, 1) //
      .multiply(RealScalar.of(Math.sqrt(3 / 5))) //
      .map(RealScalar.ONE::add) //
      .divide(RealScalar.of(2));

  // ---
  public ClothoidCurve3(Tensor p, Tensor q) {
    super(p, q);
  }

  @Override // from ClothoidCurve
  protected Scalar il(Scalar t) {
    return W.dot(X.multiply(t).map(clothoidQuadratic)).Get().multiply(t);
  }

  @Override // from ClothoidCurve
  protected Scalar ir(Scalar t) {
    Scalar _1_t = _1.subtract(t);
    return W.dot(X.multiply(_1_t).map(t::add).map(clothoidQuadratic)).Get().multiply(_1_t);
  }
}
