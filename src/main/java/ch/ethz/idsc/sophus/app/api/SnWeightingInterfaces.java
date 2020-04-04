// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnMetricSquared;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;

public enum SnWeightingInterfaces implements Supplier<WeightingInterface> {
  BI_LINEAR(RelativeCoordinate.linear(SnManifold.INSTANCE)), //
  BI_SMOOTH(RelativeCoordinate.smooth(SnManifold.INSTANCE)), //
  BI_AFFINE(RelativeCoordinate.affine(SnManifold.INSTANCE)), //
  ID_LINEAR(AbsoluteCoordinate.linear(SnManifold.INSTANCE)), //
  ID_SMOOTH(AbsoluteCoordinate.smooth(SnManifold.INSTANCE)), //
  IW_LINEAR(InverseDistanceWeighting.of(SnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(SnMetricSquared.INSTANCE)), //
  ;

  private final WeightingInterface weightingInterface;

  private SnWeightingInterfaces(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
