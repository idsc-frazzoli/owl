// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.sophus.clt.LagrangeQuadratic;
import ch.ethz.idsc.sophus.clt.mid.ClothoidQuadratic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class CustomClothoidQuadratic implements ClothoidQuadratic, Serializable {
  private static final Scalar HALF = RealScalar.of(0.5);

  @SuppressWarnings("unchecked")
  public static ClothoidQuadratic of(Scalar lambda) {
    return new CustomClothoidQuadratic((BinaryOperator<Scalar> & Serializable) (s1, s2) -> lambda);
  }

  /***************************************************/
  private final BinaryOperator<Scalar> binaryOperator;

  /** @param binaryOperator mapping (s1, s2) -> lambda */
  public CustomClothoidQuadratic(BinaryOperator<Scalar> binaryOperator) {
    this.binaryOperator = binaryOperator;
  }

  @Override // from ClothoidQuadratic
  public LagrangeQuadratic lagrangeQuadratic(Scalar b0, Scalar b1) {
    Scalar s1 = b0.add(b1).multiply(HALF);
    Scalar s2 = b0.subtract(b1).multiply(HALF);
    Scalar lambda = binaryOperator.apply(s1, s2);
    Scalar bm = lambda.add(s1);
    return LagrangeQuadratic.interp(b0, bm, b1);
  }
}
