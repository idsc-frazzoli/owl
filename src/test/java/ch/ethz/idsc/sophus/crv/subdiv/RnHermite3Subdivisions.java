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
 * implementation for R^n
 * 
 * @see BSpline3CurveSubdivision */
/* package */ enum RnHermite3Subdivisions {
  ;
  private static final Tensor AMP = Tensors.fromString("{{1/2, +1/8}, {-3/4, -1/8}}");
  private static final Tensor AMQ = Tensors.fromString("{{1/2, -1/8}, {+3/4, -1/8}}");

  /** "Construction of Hermite subdivision schemes reproducing polynomials", 2017
   * Example 3.7, eq. 28, p. 572
   * by Byeongseon Jeong, Jungho Yoon
   * 
   * @param theta
   * @param omega */
  public static RnHermite3Subdivision of(Scalar theta, Scalar omega) {
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
  public static RnHermite3Subdivision common() {
    return of( //
        RationalScalar.of(+1, 128), //
        RationalScalar.of(-1, 16));
  }

  private static final HermiteSubdivision A1 = //
      new RnHermite3Subdivision( //
          Tensors.fromString("{{1/2, +1/16}, {-15/16, -7/32}}"), //
          Tensors.fromString("{{1/2, -1/16}, {+15/16, -7/32}}"), //
          Tensors.fromString("{{1/128, -7/256}, {0, 1/16}}"), //
          Tensors.fromString("{{63/64, 0}, {0, 3/8}}"), //
          Tensors.fromString("{{1/128, +7/256}, {0, 1/16}}"));

  /** C3
   * 
   * Reference:
   * "A note on spectral properties of Hermite subdivision operators"
   * Example 14, p. 13
   * by Moosmueller, 2018
   * 
   * Quote:
   * "It is proved there that these scheme satisfy the special sum rule of
   * order 7. We show that the spectral condition up to order 2 is satisfied,
   * but higher spectral conditions are not satisfied."
   * 
   * @return */
  public static HermiteSubdivision a1() {
    return A1;
  }

  /***************************************************/
  private static final HermiteSubdivision A2 = //
      new RnHermite3Subdivision( //
          Tensors.fromString("{{1/2, +5/56}, {-7/12, -1/24}}"), //
          Tensors.fromString("{{1/2, -5/56}, {+7/12, -1/24}}"), //
          Tensors.fromString("{{7/96, +25/1344}, {-77/384, -19/384}}"), //
          Tensors.fromString("{{41/48, 0}, {0, 19/96}}"), //
          Tensors.fromString("{{7/96, -25/1344}, {+77/384, -19/384}}"));

  /** C5
   * 
   * Reference:
   * "A note on spectral properties of Hermite subdivision operators"
   * Example 14, p. 13
   * by Moosmueller, 2018
   * 
   * Quote:
   * "It is proved there that these scheme satisfy the special sum rule of
   * order 7. We show that the spectral condition up to order 2 is satisfied,
   * but higher spectral conditions are not satisfied."
   * 
   * @return */
  public static HermiteSubdivision a2() {
    return A2;
  }
}
