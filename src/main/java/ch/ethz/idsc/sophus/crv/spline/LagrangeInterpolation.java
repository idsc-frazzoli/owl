// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.Interpolation;

public enum LagrangeInterpolation {
  ;
  /** @param splitInterface
   * @param tensor
   * @return */
  public static Interpolation of(SplitInterface splitInterface, Tensor tensor) {
    return new GeodesicNeville(splitInterface, Range.of(0, tensor.length()), tensor);
  }
}
