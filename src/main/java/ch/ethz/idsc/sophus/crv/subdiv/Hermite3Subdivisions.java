// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public enum Hermite3Subdivisions {
  ;
  /** Reference:
   * "Noninterpolatory Hermite subdivision schemes"
   * by Han, Yu, Xue, 2004, p. 1358
   * 
   * "Construction of Hermite subdivision schemes reproducing polynomials", 2017
   * Example 3.7, eq. 28, p. 572
   * by Byeongseon Jeong, Jungho Yoon
   * 
   * "Stirling numbers and Gregory coefficients for the factorization of Hermite
   * subdivision operators"
   * Example 35, p. 26
   * by Moosmueller, Huening, Conti, 2019
   * 
   * <p>Hint:
   * For theta == 0 and omega == 0, the scheme reduces to Hermite1Subdivision
   * 
   * <p>Quote:
   * "it is proved that the scheme reproduces polynomials up to degree 3 and
   * thus it satisfies the spectral condition up to order 3"
   * 
   * <p>Quote:
   * "H1 with theta = 1/32 provides an example of an Hermite scheme which does not
   * reproduce polynomials of degree 4, but satisfies the spectral condition of order 4.
   * To the best of our knowledge, this is the first time it is observed that the
   * spectral condition is not equivalent to the reproduction of polynomials."
   * 
   * <p>Quote:
   * "Computations show that the Hermite scheme is C4 for omega in [-0.12, -0.088]
   * 
   * @param lieGroup
   * @param lieExponential
   * @param theta
   * @param omega
   * @return */
  public static Hermite3SubdivisionBuilder _of(LieGroup lieGroup, LieExponential lieExponential, Scalar theta, Scalar omega) {
    return new Hermite3SubdivisionBuilder(lieGroup, lieExponential, //
        Tensors.of(theta, RealScalar.ONE.subtract(theta.add(theta)), theta), //
        RationalScalar.of(-1, 8), RationalScalar.of(3, 4), RationalScalar.of(-1, 8), //
        RationalScalar.of(-1, 2).multiply(theta), //
        RationalScalar.of(-3, 2).multiply(omega), //
        Tensors.of(RationalScalar.HALF.multiply(omega), RationalScalar.HALF.add(omega.add(omega)), RationalScalar.HALF.multiply(omega)));
  }

  /** @param lieGroup
   * @param lieExponential
   * @param theta
   * @param omega
   * @return */
  public static HermiteSubdivision of( //
      LieGroup lieGroup, LieExponential lieExponential, Scalar theta, Scalar omega) {
    return _of(lieGroup, lieExponential, theta, omega).create();
  }

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @param theta
   * @param omega
   * @return */
  public static HermiteSubdivision of( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
      Scalar theta, Scalar omega) {
    return _of(lieGroup, lieExponential, theta, omega).create(biinvariantMean);
  }

  /** default with theta == 1/128 and omega == -1/16
   * 
   * @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @throws Exception if either parameters is null */
  public static HermiteSubdivision of( //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return _of(lieGroup, lieExponential, RationalScalar.of(+1, 128), RationalScalar.of(-1, 16)) //
        .create(biinvariantMean);
  }

  /***************************************************/
  /** C3
   * 
   * References:
   * "Noninterpolatory Hermite subdivision schemes"
   * by Han, Yu, Xue, 2004, p. 1358
   * 
   * "A note on spectral properties of Hermite subdivision operators"
   * Example 14, p. 13
   * by Moosmueller, 2018
   * 
   * Quote:
   * "It is proved there that these scheme satisfy the special sum rule of
   * order 7. We show that the spectral condition up to order 2 is satisfied,
   * but higher spectral conditions are not satisfied."
   * 
   * @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @return */
  private static Hermite3SubdivisionBuilder _a1(LieGroup lieGroup, LieExponential lieExponential) {
    return new Hermite3SubdivisionBuilder(lieGroup, lieExponential, //
        Tensors.fromString("{1/128, 63/64, 1/128}"), //
        RationalScalar.of(-1, 16), RationalScalar.of(15, 16), RationalScalar.of(-7, 32), //
        RationalScalar.of(+7, 256), //
        RealScalar.ZERO, //
        Tensors.fromString("{1/16, 3/8, 1/16}"));
  }

  public static HermiteSubdivision a1(LieGroup lieGroup, LieExponential lieExponential) {
    return _a1(lieGroup, lieExponential).create();
  }

  public static HermiteSubdivision a1(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return _a1(lieGroup, lieExponential).create(biinvariantMean);
  }

  /***************************************************/
  /** C5
   * 
   * References:
   * "Noninterpolatory Hermite subdivision schemes"
   * by Han, Yu, Xue, 2004, p. 1358
   * 
   * "A note on spectral properties of Hermite subdivision operators"
   * Example 14, p. 13
   * by Moosmueller, 2018
   * 
   * <p>Quote:
   * "It is proved there that these scheme satisfy the special sum rule of
   * order 7. We show that the spectral condition up to order 2 is satisfied,
   * but higher spectral conditions are not satisfied."
   * 
   * @param lieGroup
   * @param lieExponential
   * @return */
  private static Hermite3SubdivisionBuilder _a2(LieGroup lieGroup, LieExponential lieExponential) {
    return new Hermite3SubdivisionBuilder(lieGroup, lieExponential, //
        Tensors.fromString("{7/96, 41/48, 7/96}"), //
        RationalScalar.of(-5, 56), RationalScalar.of(7, 12), RationalScalar.of(-1, 24), //
        RationalScalar.of(-25, 1344), //
        RationalScalar.of(77, 384), //
        Tensors.fromString("{-19/384, 19/96, -19/384}"));
  }

  /** @param lieGroup
   * @param lieExponential
   * @return */
  public static HermiteSubdivision a2(LieGroup lieGroup, LieExponential lieExponential) {
    return _a2(lieGroup, lieExponential).create();
  }

  /** @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @return */
  public static HermiteSubdivision a2(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    return _a2(lieGroup, lieExponential).create(biinvariantMean);
  }

  /***************************************************/
  /** "Noninterpolatory Hermite subdivision schemes"
   * by Han, Yu, Xue, 2004, p. 1358
   * 
   * @param lieGroup
   * @param lieExponential
   * @param biinvariantMean
   * @return */
  public static HermiteSubdivision a3(LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
    throw new UnsupportedOperationException();
  }
}
