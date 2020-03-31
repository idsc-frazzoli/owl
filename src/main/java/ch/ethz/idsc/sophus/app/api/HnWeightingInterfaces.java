// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBiinvariantCoordinate;
import ch.ethz.idsc.sophus.hs.hn.HnManifold;
import ch.ethz.idsc.sophus.hs.hn.HnMetric;
import ch.ethz.idsc.sophus.hs.hn.HnMetricSquared;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;

public enum HnWeightingInterfaces implements Supplier<WeightingInterface> {
  BI_LINEAR(HsBiinvariantCoordinate.linear(HnManifold.INSTANCE)), //
  BI_SMOOTH(HsBiinvariantCoordinate.smooth(HnManifold.INSTANCE)), //
  ID_LINEAR(HsBarycentricCoordinate.linear(HnManifold.INSTANCE)), //
  ID_SMOOTH(HsBarycentricCoordinate.smooth(HnManifold.INSTANCE)), //
  IW_LINEAR(InverseDistanceWeighting.of(HnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(HnMetricSquared.INSTANCE)), //
  ;

  private final WeightingInterface weightingInterface;

  private HnWeightingInterfaces(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
