// code by jph
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum GeodesicMeanFilter {
  ;
  public static TensorUnaryOperator of(SplitInterface splitInterface, int radius) {
    return CenterFilter.of(GeodesicMean.of(splitInterface), radius);
  }
}
