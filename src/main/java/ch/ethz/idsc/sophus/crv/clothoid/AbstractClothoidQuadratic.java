// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference: U. Reif slide 8/32
 * 
 * quadratic polynomial that interpolates given values at parameters 0, 1/2, 1:
 * <pre>
 * p[0/2] == b0
 * p[1/2] == bm
 * p[2/2] == b1
 * </pre> */
/* package */ abstract class AbstractClothoidQuadratic implements ScalarUnaryOperator {
  private static final Scalar _3 = RealScalar.of(+3);
  // ---
  protected final Scalar c0;
  protected final Scalar c1;
  protected final Scalar c2;

  /** @param b0
   * @param bm
   * @param b1 */
  public AbstractClothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    c0 = b0;
    Scalar b2 = bm.add(bm);
    c1 = b2.add(b2).subtract(b0.multiply(_3).add(b1));
    c2 = b0.add(b1).subtract(b2);
  }
}
