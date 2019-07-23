// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Reference:
 * Ulrich Reif slides */
// TODO rename
/* package */ abstract class AbstractClothoidCurve<T extends AbstractClothoidQuadratic> implements ScalarTensorFunction {
  protected final Tensor pxy;
  protected final Tensor diff;
  protected final Scalar da;
  protected final T clothoidQuadratic;

  public AbstractClothoidCurve(Tensor p, Tensor q) {
    pxy = p.extract(0, 2);
    Scalar pa = p.Get(2);
    Tensor qxy = q.extract(0, 2);
    Scalar qa = q.Get(2);
    // ---
    diff = qxy.subtract(pxy);
    da = ArcTan2D.of(diff); // special case when diff == {0, 0}
    Scalar b0 = So2.MOD.apply(pa.subtract(da)); // normal form T0 == b0
    Scalar b1 = So2.MOD.apply(qa.subtract(da)); // normal form T1 == b1
    // ---
    clothoidQuadratic = clothoidQuadratic(b0, ClothoidApproximation.f(b0, b1), b1);
  }

  protected abstract T clothoidQuadratic(Scalar b0, Scalar bm, Scalar b1);
}
