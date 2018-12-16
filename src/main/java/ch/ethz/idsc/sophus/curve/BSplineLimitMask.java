// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.owl.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;

public enum BSplineLimitMask implements IntegerTensorFunction {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Integer width) {
    int odd = 2 * width + 1;
    int next = (odd - 1) / 2;
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, odd, UnitVector.of(2 * odd + 1, odd));
    return Range.of(next + 1, odd + next + 1).map(geodesicBSplineFunction);
  }
}
