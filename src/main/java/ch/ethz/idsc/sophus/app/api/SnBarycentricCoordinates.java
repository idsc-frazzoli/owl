// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBiinvariantCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnAffineCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnMetricSquared;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;

public enum SnBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  BI_LINEAR(HsBiinvariantCoordinate.linear(SnManifold.INSTANCE)), //
  BI_SMOOTH(HsBiinvariantCoordinate.smooth(SnManifold.INSTANCE)), //
  ID_LINEAR(HsBarycentricCoordinate.linear(SnManifold.INSTANCE)), //
  ID_SMOOTH(HsBarycentricCoordinate.smooth(SnManifold.INSTANCE)), //
  AFFINE1(SnAffineCoordinate.INSTANCE), //
  AFFINE2(HsBarycentricCoordinate.affine(SnManifold.INSTANCE)), //
  IW_LINEAR(InverseDistanceWeighting.of(SnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(SnMetricSquared.INSTANCE)), //
  ;

  private final BarycentricCoordinate barycentricCoordinate;

  private SnBarycentricCoordinates(BarycentricCoordinate barycentricCoordinate) {
    this.barycentricCoordinate = barycentricCoordinate;
  }

  @Override
  public BarycentricCoordinate get() {
    return barycentricCoordinate;
  }
}
