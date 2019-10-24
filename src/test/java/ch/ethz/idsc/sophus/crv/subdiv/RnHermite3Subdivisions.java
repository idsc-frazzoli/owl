// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensors;

/** implementation for R^n */
/* package */ enum RnHermite3Subdivisions {
  ;
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
