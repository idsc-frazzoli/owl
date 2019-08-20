// code by ob
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.flt.CausalFilter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicIIRnFilter {
  ;
  /** @param extrapolation
   * @param binaryAverage
   * @param radius
   * @param alpha
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator extrapolation, BinaryAverage binaryAverage, int radius, Scalar alpha) {
    return CausalFilter.of(() -> new GeodesicIIRn(extrapolation, binaryAverage, radius, alpha));
  }
}