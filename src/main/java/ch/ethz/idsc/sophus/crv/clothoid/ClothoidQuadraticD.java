// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** Reference: U. Reif slide 8/32
 * 
 * quadratic polynomial that interpolates given values at parameters 0, 1/2, 1:
 * <pre>
 * p[0/2] == b0
 * p[1/2] == bm
 * p[2/2] == b1
 * </pre> */
/* package */ class ClothoidQuadraticD extends AbstractClothoidQuadratic {
  private static final Scalar _4 = RealScalar.of(+4);
  // ---
  private final Scalar c0_;
  private final Scalar c1_;

  /** The Lagrange interpolating polynomial has the following coefficients
   * {-3 b0 - b1 + 4 bm, 4 (b0 + b1 - 2 bm)}
   * 
   * @param b0
   * @param bm
   * @param b1 */
  public ClothoidQuadraticD(Scalar b0, Scalar bm, Scalar b1) {
    super(b0, bm, b1);
    c0_ = c1;
    c1_ = c2.multiply(_4);
  }

  @Override
  public Scalar apply(Scalar s) {
    return c0_.add(c1_.multiply(s));
  }

  public Scalar head() {
    return c0_;
  }

  public Scalar tail() {
    return c0_.add(c1_);
  }
}
