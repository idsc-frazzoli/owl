// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** 3-point Gauss Legendre quadrature on interval [0, 1] */
/* package */ class ClothoidCurve3 extends ClothoidCurve {
  private static final Tensor W = Tensors.vector(5, 8, 5).divide(RealScalar.of(18.0));
  private static final Tensor X = Tensors.vector(-1, 0, 1) //
      .multiply(Sqrt.FUNCTION.apply(RationalScalar.of(3, 5))) //
      .map(RealScalar.ONE::add) //
      .divide(RealScalar.of(2));
  private static final Scalar X0 = X.Get(0);
  private static final Scalar X1 = X.Get(1);
  private static final Scalar X2 = X.Get(2);
  private static final Scalar W0 = W.Get(0);
  private static final Scalar W1 = W.Get(1);

  // ---
  public ClothoidCurve3(Tensor p, Tensor q) {
    super(p, q);
  }

  @Override // from ClothoidCurve
  protected Scalar il(Scalar t) {
    // return W.dot(X.multiply(t).map(clothoidQuadratic::exp_i)).Get().multiply(t);
    Scalar v0 = clothoidQuadratic.exp_i(X0.multiply(t));
    Scalar w1 = clothoidQuadratic.exp_i(X1.multiply(t)).multiply(W1);
    Scalar v2 = clothoidQuadratic.exp_i(X2.multiply(t));
    return v0.add(v2).multiply(W0).add(w1).multiply(t);
  }

  @Override // from ClothoidCurve
  protected Scalar ir(Scalar t) {
    Scalar _1_t = _1.subtract(t);
    // return W.dot(X.multiply(_1_t).map(t::add).map(clothoidQuadratic::exp_i)).Get().multiply(_1_t);
    Scalar v0 = clothoidQuadratic.exp_i(X0.multiply(_1_t).add(t));
    Scalar w1 = clothoidQuadratic.exp_i(X1.multiply(_1_t).add(t)).multiply(W1);
    Scalar v2 = clothoidQuadratic.exp_i(X2.multiply(_1_t).add(t));
    return v0.add(v2).multiply(W0).add(w1).multiply(_1_t);
  }
}
