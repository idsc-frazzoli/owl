// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicMeanFilter {
  ;
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, int radius) {
    return new GeodesicCenterFilter(GeodesicMean.of(geodesicInterface), radius);
  }
}
