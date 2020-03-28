// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBiinvariantCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnAffineCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnMetricSquared;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;

public enum SnBarycentricCoordinates implements Supplier<WeightingInterface> {
  BI_LINEAR(HsBiinvariantCoordinate.linear(SnManifold.INSTANCE)), //
  BI_SMOOTH(HsBiinvariantCoordinate.smooth(SnManifold.INSTANCE)), //
  ID_LINEAR(HsBarycentricCoordinate.linear(SnManifold.INSTANCE)), //
  ID_SMOOTH(HsBarycentricCoordinate.smooth(SnManifold.INSTANCE)), //
  AFFINE1(SnAffineCoordinate.INSTANCE), //
  AFFINE2(HsBiinvariantCoordinate.affine(SnManifold.INSTANCE)), //
  IW_LINEAR(InverseDistanceWeighting.of(SnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(SnMetricSquared.INSTANCE)), //
  ;

  private final WeightingInterface weightingInterface;

  private SnBarycentricCoordinates(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
