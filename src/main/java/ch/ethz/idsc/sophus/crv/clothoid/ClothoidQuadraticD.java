// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.math.HeadTailInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference: U. Reif slide 8/32
 * 
 * derivative of quadratic polynomial p[t] that interpolates given values at parameters 0, 1/2, 1:
 * <pre>
 * p[0/2] == b0
 * p[1/2] == bm
 * p[2/2] == b1
 * </pre> */
/* package */ class ClothoidQuadraticD implements ScalarUnaryOperator, HeadTailInterface {
  private static final Scalar _3 = RealScalar.of(+3);
  private static final Scalar _4 = RealScalar.of(+4);
  // ---
  private final Scalar c0;
  private final Scalar c1;

  /** The Lagrange interpolating polynomial is linear with the following coefficients
   * {-3 b0 + 4 bm - b1, 4 (b0 - 2 bm + b1)}
   * 
   * @param b0
   * @param bm
   * @param b1 */
  public ClothoidQuadraticD(Scalar b0, Scalar bm, Scalar b1) {
    c0 = bm.multiply(_4).subtract(b1).subtract(b0.multiply(_3));
    c1 = b0.add(b1).subtract(bm.add(bm)).multiply(_4);
  }

  @Override
  public Scalar apply(Scalar s) {
    return c1.multiply(s).add(c0);
  }

  @Override
  public Scalar head() {
    return c0;
  }

  @Override
  public Scalar tail() {
    return c0.add(c1);
  }
}
