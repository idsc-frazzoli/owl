// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class ClothoidCurvature implements ScalarUnaryOperator {
  private final Tensor pxy;
  private final Tensor diff;
  private final Scalar da;
  protected final ClothoidQuadraticD clothoidQuadraticD;
  private final Scalar v;

  public ClothoidCurvature(Tensor p, Tensor q) {
    pxy = p.extract(0, 2);
    Scalar pa = p.Get(2);
    Tensor qxy = q.extract(0, 2);
    Scalar qa = q.Get(2);
    // ---
    diff = qxy.subtract(pxy);
    v = Norm._2.ofVector(diff);
    da = ArcTan2D.of(diff); // special case when diff == {0, 0}
    Scalar b0 = So2.MOD.apply(pa.subtract(da)); // normal form T0 == b0
    Scalar b1 = So2.MOD.apply(qa.subtract(da)); // normal form T1 == b1
    // ---
    clothoidQuadraticD = new ClothoidQuadraticD(b0, ClothoidApproximation.f(b0, b1), b1);
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
