// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.UnitVector;

/** function defined for positive odd integers
 * 
 * Example:
 * BSplineLimitMask[ 1 ] == {1}
 * BSplineLimitMask[ 3 ] == {1/6, 2/3, 1/6}
 * BSplineLimitMask[ 5 ] == {1/120, 13/60, 11/20, 13/60, 1/120} */
public enum BSplineLimitMask implements Function<Integer, Tensor> {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Integer degree) {
    if (degree % 2 == 0)
      throw new IllegalArgumentException("" + degree);
    int extent = (degree - 1) / 2;
    return Range.of(extent + 1, degree + extent + 1) //
        .map(GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, UnitVector.of(2 * degree + 1, degree)));
  }
}
