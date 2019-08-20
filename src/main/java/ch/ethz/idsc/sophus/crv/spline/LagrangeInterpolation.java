// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.Interpolation;

public enum LagrangeInterpolation {
  ;
  /** @param binaryAverage
   * @param tensor
   * @return */
  public static Interpolation of(BinaryAverage binaryAverage, Tensor tensor) {
    return new GeodesicNeville(binaryAverage, Range.of(0, tensor.length()), tensor);
  }
}
