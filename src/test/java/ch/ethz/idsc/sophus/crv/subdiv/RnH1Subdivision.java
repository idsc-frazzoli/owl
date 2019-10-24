// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * reproduces polynomials up to degree 3
 * 
 * implementation for R^n
 * 
 * Reference 1:
 * "Dual Hermite subdivision schemes of de Rham-type", 2014
 * by Conti, Merrien, Romani
 * 
 * Reference 2:
 * "A note on spectral properties of Hermite subdivision operators"
 * Example 14, p. 13
 * by Moosmueller, 2018
 * 
 * @see BSpline3CurveSubdivision */
/* package */ class RnH1Subdivision {
  private static final Tensor AMP = Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}");
  private static final Tensor AMQ = Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}");

  /** "Construction of Hermite subdivision schemes reproducing polynomials", 2017
   * Example 3.7, eq. 28, p. 572
   * by Byeongseon Jeong, Jungho Yoon
   * 
   * @param theta
   * @param omega */
  public static HermiteSubdivision of(Scalar theta, Scalar omega) {
    Tensor ARP = Tensors.of( //
        Tensors.of(theta, theta.multiply(RationalScalar.HALF)), //
        Tensors.of(omega.multiply(RationalScalar.of(+3, 2)), omega.multiply(RationalScalar.HALF)));
    Tensor ARQ = DiagonalMatrix.of( //
        RealScalar.ONE.subtract(theta.add(theta)), //
        RationalScalar.HALF.add(omega.add(omega)));
    Tensor ARR = Tensors.of( //
        Tensors.of(theta, theta.multiply(RationalScalar.HALF).negate()), //
        Tensors.of(omega.multiply(RationalScalar.of(-3, 2)), omega.multiply(RationalScalar.HALF)));
    return new RnHermite3Subdivision(AMP, AMQ, ARP, ARQ, ARR);
  }

  /** default with theta == 1/128 and omega == -1/16 */
  public static HermiteSubdivision common() {
    return of( //
        RationalScalar.of(+1, 128), //
        RationalScalar.of(-1, 16));
  }
}
