// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Reference:
 * Ulrich Reif slides */
/* package */ abstract class ClothoidCurve extends AbstractClothoidCurve<ClothoidQuadratic> {
  protected static final Scalar _1 = RealScalar.of(1.0);

  public ClothoidCurve(Tensor p, Tensor q) {
    super(p, q);
  }

  @Override // from AbstractClothoidCurve
  protected ClothoidQuadratic clothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    return new ClothoidQuadratic(b0, bm, b1);
  }

  /** @param t
   * @return approximate integration of exp i*clothoidQuadratic on [0, t] */
  protected abstract Scalar il(Scalar t);

  /** @param t
   * @return approximate integration of exp i*clothoidQuadratic on [t, 1] */
  protected abstract Scalar ir(Scalar t);

  @Override // from ScalarTensorFunction
  public final Tensor apply(Scalar t) {
    Scalar il = il(t);
    Scalar ir = ir(t);
    /** ratio z enforces interpolation of terminal points
     * t == 0 -> (0, 0)
     * t == 1 -> (1, 0) */
    Scalar z = il.divide(il.add(ir));
    return pxy.add(StaticHelper.prod(z, diff)) //
        .append(clothoidQuadratic.apply(t).add(da));
  }
}
