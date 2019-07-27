// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Hint:
 * If the given points p and q have identical (x, y)-coordinates, then
 * the result is undefined. */
public class ClothoidCurvature implements ScalarUnaryOperator {
  private final ClothoidQuadraticD clothoidQuadraticD;
  private final Scalar v;

  /** @param p start point
   * @param q end point */
  public ClothoidCurvature(Tensor p, Tensor q) {
    Tensor pxy = p.extract(0, 2);
    Scalar pa = p.Get(2);
    Tensor qxy = q.extract(0, 2);
    Scalar qa = q.Get(2);
    // ---
    Tensor diff = qxy.subtract(pxy);
    Scalar da = ArcTan2D.of(diff); // special case when diff == {0, 0}
    Scalar b0 = So2.MOD.apply(pa.subtract(da)); // normal form T0 == b0
    Scalar b1 = So2.MOD.apply(qa.subtract(da)); // normal form T1 == b1
    // ---
    clothoidQuadraticD = new ClothoidQuadraticD(b0, ClothoidApproximation.f(b0, b1), b1);
    v = Norm._2.ofVector(diff);
  }

  @Override
  public Scalar apply(Scalar t) {
    return clothoidQuadraticD.apply(t).divide(v);
  }

  public Scalar head() {
    return clothoidQuadraticD.head().divide(v);
  }

  public Scalar tail() {
    return clothoidQuadraticD.tail().divide(v);
  }
}
