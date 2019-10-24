// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * @see BSpline2CurveSubdivision */
/* package */ enum RnHermite2Subdivisions {
  ;
  private static final HermiteSubdivision A1 = //
      new RnHermite2Subdivision( //
          Tensors.fromString("{{27/4, +9/8}, {-9/2, 3/4}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{5/4, -3/8}, {+9/2, -5/4}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{5/4, +3/8}, {-9/2, -5/4}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{27/4, -9/8}, {+9/2, 3/4}}").divide(RealScalar.of(8)));

  /** Reference:
   * "Increasing the smoothness of vector and Hermite subdivision schemes"
   * Example 45, p. 25
   * by Moosmueller, Dyn, 2017
   * 
   * @return */
  public static HermiteSubdivision a1() {
    return A1;
  }

  private static final HermiteSubdivision A2 = //
      new RnHermite2Subdivision( //
          Tensors.fromString("{{152/25, +31/25}, {-29/50, 277/100}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{48/25, -29/25}, {+29/50, 13/20}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{48/25, +29/25}, {-29/50, 13/20}}").divide(RealScalar.of(8)), //
          Tensors.fromString("{{152/25, -31/25}, {+29/50, 277/100}}").divide(RealScalar.of(8)));

  /** Reference:
   * "Hermite subdivision on manifolds via parallel transport"
   * Example 1, p. 1063
   * by Moosmueller, 2017
   * 
   * @return */
  public static HermiteSubdivision a2() {
    return A2;
  }
}
