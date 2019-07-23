// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference: U. Reif slide 8/32
 * 
 * quadratic polynomial that interpolates given values at parameters 0, 1/2, 1:
 * <pre>
 * p[0/2] == b0
 * p[1/2] == bm
 * p[2/2] == b1
 * </pre> */
/* package */ class ClothoidQuadratic extends AbstractClothoidQuadratic {
  private final ScalarUnaryOperator series;

  /** The Lagrange interpolating polynomial has the following coefficients
   * {b0, -3 b0 - b1 + 4 bm, 2 (b0 + b1 - 2 bm)}
   * 
   * @param b0
   * @param bm
   * @param b1 */
  public ClothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    super(b0, bm, b1);
    series = Series.of(Tensors.of(c0, c1, c2.add(c2)));
  }

  @Override
  public Scalar apply(Scalar s) {
    return series.apply(s);
  }

  public Scalar exp_i(Scalar s) {
    return ComplexScalar.unit(apply(s));
  }
}
