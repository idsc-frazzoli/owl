// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicMeanFilter {
  ;
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, int radius) {
    return GeodesicCenterFilter.of(GeodesicMean.of(geodesicInterface), radius);
  }
}
