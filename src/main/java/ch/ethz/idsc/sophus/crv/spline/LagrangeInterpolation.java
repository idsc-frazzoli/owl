// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.Interpolation;

public enum LagrangeInterpolation {
  ;
  /** @param geodesicInterface
   * @param tensor
   * @return */
  public static Interpolation of(GeodesicInterface geodesicInterface, Tensor tensor) {
    return new GeodesicNeville(geodesicInterface, Range.of(0, tensor.length()), tensor);
  }
}
