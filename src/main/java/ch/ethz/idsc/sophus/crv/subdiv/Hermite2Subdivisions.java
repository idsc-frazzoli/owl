// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.red.Times;

public enum Hermite2Subdivisions {
  ;
  private static final Scalar _1_8 = RationalScalar.of(1, 8);

  public static HermiteSubdivision of(LieGroup lieGroup, LieExponential lieExponential, Scalar lambda, Scalar mu) {
    Scalar an2_11 = RealScalar.of(2).add(Times.of(RealScalar.of(4), lambda, RealScalar.ONE.subtract(mu)));
    Scalar an2_12 = Times.of(RealScalar.of(2), lambda, RealScalar.of(2).add(mu));
    Scalar an2_21 = Series.of(Tensors.vector(4, -2, -2)).apply(mu);
    Scalar an2_22 = mu.multiply(mu).add(Times.of(RealScalar.of(8), lambda, RealScalar.ONE.subtract(mu)));
    Tensor ALQ = Tensors.of(Tensors.of(an2_11, an2_12), Tensors.of(an2_21, an2_22)).multiply(_1_8);
    // ---
    Scalar an1_11 = RealScalar.of(6).subtract(Times.of(RealScalar.of(4), lambda, RealScalar.ONE.subtract(mu)));
    Scalar an1_12 = Times.of(RealScalar.of(-2), lambda, RealScalar.of(-4).add(mu));
    Scalar an1_21 = an2_21;
    Scalar an1_22 = mu.multiply(mu).subtract(Times.of(RealScalar.of(8), lambda, RealScalar.ONE.subtract(mu))).add(mu).add(mu);
    Tensor ALP = Tensors.of(Tensors.of(an1_11, an1_12.negate()), Tensors.of(an1_21.negate(), an1_22)).multiply(_1_8);
    return new Hermite2Subdivision(lieGroup, lieExponential, //
        ALQ.Get(0, 0), // lgg
        ALP.Get(0, 1), // lgv
        ALQ.Get(0, 1), // hgv
        ALQ.Get(1, 0), // hvg
        Tensors.of(ALP.Get(1, 1), ALQ.Get(1, 1))); // vpq
  }

  /***************************************************/
  /** lambda == -1/8, mu == -1/2
   * 
   * <p>Reference:
   * "Increasing the smoothness of vector and Hermite subdivision schemes"
   * Example 45, p. 25
   * by Moosmueller, Dyn, 2017
   * 
   * @return */
  public static HermiteSubdivision a1(LieGroup lieGroup, LieExponential lieExponential) {
    return of(lieGroup, lieExponential, //
        RationalScalar.of(-1, 8), RationalScalar.of(-1, 2));
    // new Hermite2Subdivision(lieGroup, lieExponential, //
    // RationalScalar.of(5, 32), //
    // RationalScalar.of(+9, 64), //
    // RationalScalar.of(-3, 64), //
    // RationalScalar.of(9, 16), //
    // Tensors.of(RationalScalar.of(3, 32), RationalScalar.of(-5, 32)));
  }

  /***************************************************/
  /** Reference:
   * "Increasing the smoothness of vector and Hermite subdivision schemes"
   * by Moosmueller, Dyn, 2017
   * 
   * @param lieGroup
   * @param lieExponential
   * @return */
  public static HermiteSubdivision a2(LieGroup lieGroup, LieExponential lieExponential) {
    return of(lieGroup, lieExponential, //
        RationalScalar.of(-1, 5), RationalScalar.of(9, 10));
    // RationalScalar.of(6, 25), //
    // RationalScalar.of(+31, 200), //
    // RationalScalar.of(-29, 200), //
    // RationalScalar.of(29, 400), //
    // Tensors.of(RationalScalar.of(277, 800), RationalScalar.of(13, 160)));
  }
}
