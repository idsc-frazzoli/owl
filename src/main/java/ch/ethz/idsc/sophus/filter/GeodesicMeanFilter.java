// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicMeanFilter {
  ;
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, int radius) {
    return CenterFilter.of(GeodesicMean.of(geodesicInterface), radius);
  }
}
