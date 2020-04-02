// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum PolygonNormalize {
  ;
  /** @param polygon
   * @param area
   * @return */
  public static Tensor of(Tensor polygon, Scalar area) {
    Scalar factor = Sqrt.FUNCTION.apply(area.divide(PolygonArea.FUNCTION.apply(polygon)));
    Tensor shift = PolygonCentroid.FUNCTION.apply(polygon).negate();
    return Tensor.of(polygon.stream().map(shift::add)).multiply(factor);
  }
}
