// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;

/** Example:
 * for width == 1 the limit mask is {1/6, 2/3, 1/6} */
public enum BSplineLimitMask implements Function<Integer, Tensor> {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Integer extent) {
    int odd = 2 * extent + 1;
    return Range.of(extent + 1, odd + extent + 1) //
        .map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, odd, UnitVector.of(2 * odd + 1, odd)));
  }
}
