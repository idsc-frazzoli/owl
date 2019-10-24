// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

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
 * implementation for R^n */
/* package */ class RnHermite3A1Subdivision {
  private static final Tensor AMP = Tensors.fromString("{{1/2, +1/16}, {-15/16, -7/32}}");
  private static final Tensor AMQ = Tensors.fromString("{{1/2, -1/16}, {+15/16, -7/32}}");
  // ---
  private static final Tensor ARP = Tensors.fromString("{{1/128, -7/256}, {0, 1/16}}");
  private static final Tensor ARQ = Tensors.fromString("{{63/64, 0}, {0, 3/8}}");
  private static final Tensor ARR = Tensors.fromString("{{1/128, +7/256}, {0, 1/16}}");
  // ---
  private static final HermiteSubdivision HERMITE_SUBDIVISION = //
      new RnHermite3Subdivision(AMP, AMQ, ARP, ARQ, ARR);

  /** @return */
  public static HermiteSubdivision instance() {
    return HERMITE_SUBDIVISION;
  }
}
