// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class ClothoidCurvature extends AbstractClothoidCurve<ClothoidQuadraticD> {
  private final Scalar v;

  public ClothoidCurvature(Tensor p, Tensor q) {
    super(p, q);
    v = Norm._2.ofVector(diff);
  }

  @Override // from AbstractClothoidCurve
  protected ClothoidQuadraticD clothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    return new ClothoidQuadraticD(b0, bm, b1);
  }

  @Override // from ScalarTensorFunction
  public Scalar apply(Scalar t) {
    return clothoidQuadratic.apply(t).divide(v);
  }

  public Scalar head() {
    return clothoidQuadratic.head().divide(v);
  }

  public Scalar tail() {
    return clothoidQuadratic.tail().divide(v);
  }
}
