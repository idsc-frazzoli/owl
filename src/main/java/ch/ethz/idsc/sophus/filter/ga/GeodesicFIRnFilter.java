// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicFIRnFilter {
  ;
  /** @param geodesicExtrapolation
   * @param splitInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    return CausalFilter.of(() -> new GeodesicFIRn(geodesicExtrapolation, splitInterface, radius, alpha));
  }
}