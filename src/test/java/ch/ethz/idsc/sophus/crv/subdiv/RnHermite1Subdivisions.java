// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * reproduces polynomials of up to degree 3
 * 
 * implementation for R^n
 * 
 * References:
 * "A family of Hermite interpolants by bisection algorithms", 1992,
 * by Merrien
 * 
 * "de Rham Transform of a Hermite Subdivision Scheme", 2007
 * by Dubuc, Merrien, p.9
 * [in the paper the signs of the matrix entries seem to be incorrect]
 * 
 * @see BSpline1CurveSubdivision */
/* package */ enum RnHermite1Subdivisions {
  ;
  /** @param lambda
   * @param mu
   * @return */
  public static RnHermite1Subdivision of(Scalar lambda, Scalar mu) {
    Tensor AMP = Tensors.of( //
        Tensors.of(RationalScalar.HALF, lambda.negate()), //
        Tensors.of(mu.subtract(RealScalar.ONE).multiply(RationalScalar.HALF), mu.multiply(RationalScalar.of(1, 4))));
    Tensor AMQ = Tensors.of( //
        Tensors.of(RationalScalar.HALF, lambda), //
        Tensors.of(RealScalar.ONE.subtract(mu).multiply(RationalScalar.HALF), mu.multiply(RationalScalar.of(1, 4))));
    return new RnHermite1Subdivision(AMP, AMQ);
  }

  /***************************************************/
  /** Example 3.8, eq. 29, p. 572
   * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
   * by Byeongseon Jeong, Jungho Yoon
   * lambda == -1/8, mu == -1/2 */
  private static final RnHermite1Subdivision INSTANCE = of(RationalScalar.of(-1, 8), RationalScalar.of(-1, 2));

  public static RnHermite1Subdivision instance() {
    return INSTANCE;
  }
}
