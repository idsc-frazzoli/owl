// code by jph
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement
 * 
 * finite impulse response */
public enum GeodesicFIR2 {
  ;
  private static final Scalar TWO = RealScalar.of(2);

  /** @param splitInterface
   * @param alpha
   * @return */
  public static TensorUnaryOperator of(SplitInterface splitInterface, Scalar alpha) {
    TensorUnaryOperator geodesicExtrapolation = tensor -> splitInterface.split(tensor.get(0), tensor.get(1), TWO);
    return new GeodesicFIRn(geodesicExtrapolation, splitInterface, 2, alpha);
  }
}
