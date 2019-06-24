// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicIIRnFilter {
  ;
  /** @param extrapolation
   * @param splitInterface
   * @param radius
   * @param alpha
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator extrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    return CausalFilter.of(() -> new GeodesicIIRn(extrapolation, splitInterface, radius, alpha));
  }
}