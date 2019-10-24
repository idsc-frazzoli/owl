// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/** Reference 1:
 * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
 * Example 3.7, eq. 28, p. 572
 * by Byeongseon Jeong, Jungho Yoon
 * 
 * Reference 2:
 * "Stirling numbers and Gregory coefficients for the factorization of Hermite
 * subdivision operators"
 * Example 35, p. 26
 * by Moosmueller, Huening, Conti, 2019
 * 
 * Hint:
 * For theta == 0 and omega == 0, the scheme reduces to Hermite1Subdivision
 * 
 * Quote from [2]:
 * "it is proved that H1 reproduces polynomials up to degree 3 and
 * thus it satisfies the spectral condition up to order 3"
 * 
 * Quote from [2]:
 * "H1 with theta = 1/32 provides an example of an Hermite scheme which does not
 * reproduce polynomials of degree 4, but satisfies the spectral condition of order 4.
 * To the best of our knowledge, this is the first time it is observed that the
 * spectral condition is not equivalent to the reproduction of polynomials."
 * 
 * Quote from [2]:
 * "Computations show that the Hermite scheme H1 is C4 for omega in [-0.12, -0.088] */
public enum Hermite3SubdivisionGauge {
  ;
  /** midpoint group element contribution from vectors
   * factor to multiply the difference GV:=A*(qv-pv) in position (1, 2) of matrices A(-1) A(1) */
  private static final Scalar MGV = RationalScalar.of(-1, 8);
  /** midpoint vector contribution from logarithm of group element p^-1.q
   * in position (2, 1) of matrices A(-1) A(1)
   * with alternating sign multiplied by 2 */
  private static final Scalar MVG = RationalScalar.of(3, 4);
  /** midpoint vector contribution from vectors
   * factor to multiply the sum VV:=A*(pv+qv) in position (2, 2) of matrices A(-1) A(1)
   * with identical sign multiplied by 2 */
  private static final Scalar MVV = RationalScalar.of(-1, 8);

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @param theta
   * @param omega
   * @return */
  public static HermiteSubdivision of( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
      Scalar theta, Scalar omega) {
    return new Hermite3Subdivision(lieGroup, lieExponential, biinvariantMean, //
        MGV, MVG, MVV, //
        Tensors.of(theta, RealScalar.ONE.subtract(theta.add(theta)), theta), //
        RationalScalar.of(-1, 2).multiply(theta), //
        RationalScalar.of(-3, 2).multiply(omega), //
        Tensors.of(RationalScalar.HALF.multiply(omega), RationalScalar.HALF.add(omega.add(omega)), RationalScalar.HALF.multiply(omega)));
  }

  /** default with theta == 1/128 and omega == -1/16
   * 
   * @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @throws Exception if either parameters is null */
  public static HermiteSubdivision of( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return Hermite3SubdivisionGauge.of(lieGroup, lieExponential, biinvariantMean, //
        RationalScalar.of(+1, 128), //
        RationalScalar.of(-1, 16));
  }
}
