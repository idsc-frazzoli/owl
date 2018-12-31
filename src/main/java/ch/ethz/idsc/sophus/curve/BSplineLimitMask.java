// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.owl.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;

/** Example:
 * for width == 1 the limit mask is {1/6, 2/3, 1/6} */
public enum BSplineLimitMask implements IntegerTensorFunction {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Integer width) {
    int odd = 2 * width + 1;
    return Range.of(width + 1, odd + width + 1) //
        .map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, odd, UnitVector.of(2 * odd + 1, odd)));
  }
}
