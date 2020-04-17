// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.app.api.LogMetricWeightings;
import ch.ethz.idsc.sophus.krg.Krigings;

public enum FunctionTypes {
  ;
  public static final List<Object> LIST = new ArrayList<>();
  static {
    LIST.addAll(Arrays.asList(Krigings.values()));
    LIST.addAll(Arrays.asList(LogMetricWeightings.values()));
  }
}
