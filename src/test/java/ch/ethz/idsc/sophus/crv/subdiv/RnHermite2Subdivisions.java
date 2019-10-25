// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.red.Times;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * @see BSpline2CurveSubdivision */
/* package */ enum RnHermite2Subdivisions {
  ;
  private static final Scalar _1_8 = RationalScalar.of(1, 8);

  /** Reference:
   * "An algebraic approach to polynomial reproduction of hermite subdivision schemes"
   * by Conti, Huening, 2018, p.14
   * 
   * @param lambda
   * @param mu
   * @return */
  public static RnHermite2Subdivision of(Scalar lambda, Scalar mu) {
    Scalar an2_11 = RealScalar.of(2).add(Times.of(RealScalar.of(4), lambda, RealScalar.ONE.subtract(mu)));
    Scalar an2_12 = Times.of(RealScalar.of(2), lambda, RealScalar.of(2).add(mu));
    Scalar an2_21 = Series.of(Tensors.vector(4, -2, -2)).apply(mu);
    Scalar an2_22 = mu.multiply(mu).add(Times.of(RealScalar.of(8), lambda, RealScalar.ONE.subtract(mu)));
    Tensor ALQ = Tensors.of(Tensors.of(an2_11, an2_12), Tensors.of(an2_21, an2_22)).multiply(_1_8);
    Tensor AHP = Tensors.of(Tensors.of(an2_11, an2_12.negate()), Tensors.of(an2_21.negate(), an2_22)).multiply(_1_8);
    // ---
    Scalar an1_11 = RealScalar.of(6).subtract(Times.of(RealScalar.of(4), lambda, RealScalar.ONE.subtract(mu)));
    Scalar an1_12 = Times.of(RealScalar.of(-2), lambda, RealScalar.of(-4).add(mu));
    Scalar an1_21 = an2_21;
    Scalar an1_22 = mu.multiply(mu).subtract(Times.of(RealScalar.of(8), lambda, RealScalar.ONE.subtract(mu))).add(mu).add(mu);
    Tensor AHQ = Tensors.of(Tensors.of(an1_11, an1_12), Tensors.of(an1_21, an1_22)).multiply(_1_8);
    Tensor ALP = Tensors.of(Tensors.of(an1_11, an1_12.negate()), Tensors.of(an1_21.negate(), an1_22)).multiply(_1_8);
    return new RnHermite2Subdivision(ALP, ALQ, AHP, AHQ);
  }

  /***************************************************/
  private static final RnHermite2Subdivision A1 = of(RationalScalar.of(-1, 8), RationalScalar.of(-1, 2));

  /** lambda == -1/8, mu == -1/2
   * 
   * <p>Reference:
   * "Increasing the smoothness of vector and Hermite subdivision schemes"
   * Example 45, p. 25
   * by Moosmueller, Dyn, 2017
   * 
   * @return */
  public static RnHermite2Subdivision a1() {
    return A1;
  }

  /***************************************************/
  private static final RnHermite2Subdivision A2 = of(RationalScalar.of(-1, 5), RationalScalar.of(9, 10));

  /** lambda == -1/5, mu == 9/10
   * 
   * <p>Reference:
   * "Hermite subdivision on manifolds via parallel transport"
   * Example 1, p. 1063
   * by Moosmueller, 2017
   * 
   * @return */
  public static RnHermite2Subdivision a2() {
    return A2;
  }
}
