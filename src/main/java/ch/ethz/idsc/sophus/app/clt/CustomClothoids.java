// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.io.Serializable;

import ch.ethz.idsc.sophus.clt.AbstractClothoidBuilder;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.LagrangeQuadratic;
import ch.ethz.idsc.sophus.math.ScalarBinaryOperator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class CustomClothoids extends AbstractClothoidBuilder implements Serializable {
  private static final Scalar HALF = RealScalar.of(0.5);

  public static CustomClothoids of(Scalar lambda) {
    return new CustomClothoids((s1, s2) -> lambda);
  }

  public static Clothoid of(Scalar lambda, Tensor p, Tensor q) {
    return of(lambda).curve(p, q);
  }

  /***************************************************/
  private final ScalarBinaryOperator scalarBinaryOperator;

  /** @param scalarBinaryOperator mapping (s1, s2) -> lambda */
  public CustomClothoids(ScalarBinaryOperator scalarBinaryOperator) {
    this.scalarBinaryOperator = scalarBinaryOperator;
  }

  @Override // from Clothoids
  public LagrangeQuadratic lagrangeQuadratic(Scalar b0, Scalar b1) {
    Scalar s1 = b0.add(b1).multiply(HALF);
    Scalar s2 = b0.subtract(b1).multiply(HALF);
    Scalar lambda = scalarBinaryOperator.apply(s1, s2);
    Scalar bm = lambda.add(s1);
    return LagrangeQuadratic.interp(b0, bm, b1);
  }
}
