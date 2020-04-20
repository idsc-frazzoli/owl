// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorMetricSquared;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;

public enum MetricWeightings implements MetricWeighting {
  IW_LINEAR() {
    @Override
    public WeightingInterface from(TensorMetric tensorMetric) {
      return InverseDistanceWeighting.of(tensorMetric);
    }
  },
  IW_SMOOTH() {
    @Override
    public WeightingInterface from(TensorMetric tensorMetric) {
      return InverseDistanceWeighting.of(TensorMetricSquared.of(tensorMetric));
    }
  }, //
  ;

  public static List<MetricWeighting> list() {
    return Arrays.asList(values());
  }
}
