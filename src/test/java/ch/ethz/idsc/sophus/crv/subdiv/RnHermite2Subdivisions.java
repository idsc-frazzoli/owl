// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensors;

/** Merrien interpolatory Hermite subdivision scheme of order two
 * implementation for R^n
 * 
 * @see BSpline2CurveSubdivision */
/* package */ enum RnHermite2Subdivisions {
  ;
  private static final HermiteSubdivision A1 = //
      new RnHermite2Subdivision( //
          Tensors.fromString("{{27/32, +9/64}, {-9/16,  3/32}}"), //
          Tensors.fromString("{{ 5/32, -3/64}, {+9/16, -5/32}}"), //
          Tensors.fromString("{{ 5/32, +3/64}, {-9/16, -5/32}}"), //
          Tensors.fromString("{{27/32, -9/64}, {+9/16,  3/32}}"));

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
          Tensors.fromString("{{19/25, +31/200}, {-29/400, 277/800}}"), //
          Tensors.fromString("{{ 6/25, -29/200}, {+29/400,  65/800}}"), //
          Tensors.fromString("{{ 6/25, +29/200}, {-29/400,  65/800}}"), //
          Tensors.fromString("{{19/25, -31/200}, {+29/400, 277/800}}"));

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
