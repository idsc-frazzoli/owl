// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.sophus.crv.clothoid.LagrangeQuadratic;
import ch.ethz.idsc.sophus.math.ScalarBinaryOperator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class CustomClothoids extends Clothoids implements Serializable {
  private static final Scalar HALF = RealScalar.of(0.5);
  // ---
  private final ScalarBinaryOperator scalarBinaryOperator;

  /** @param scalarBinaryOperator mapping (s1, s2) -> lambda */
  public CustomClothoids(ScalarBinaryOperator scalarBinaryOperator) {
    this.scalarBinaryOperator = scalarBinaryOperator;
  }

  @Override // from Clothoids
  protected LagrangeQuadratic lagrangeQuadratic(Scalar b0, Scalar b1) {
    Scalar s1 = b0.add(b1).multiply(HALF);
    Scalar s2 = b0.subtract(b1).multiply(HALF);
    Scalar lambda = scalarBinaryOperator.apply(s1, s2);
    Scalar bm = lambda.add(s1);
    // System.out.println(Tensors.of(s1, s2, lambda).map(Round._4));
    return LagrangeQuadratic.interp(b0, bm, b1);
  }
}
