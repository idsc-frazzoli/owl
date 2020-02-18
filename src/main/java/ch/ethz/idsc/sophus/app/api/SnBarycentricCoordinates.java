// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.sn.SnAffineCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnMetricSquared;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;

public enum SnBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  INVERSE_DISTANCE(SnInverseDistanceCoordinate.INSTANCE), //
  INVERSE_DISTANCE2(SnInverseDistanceCoordinate.SQUARED), //
  AFFINE(SnAffineCoordinate.INSTANCE), //
  SHEPARD(InverseDistanceWeighting.of(SnMetric.INSTANCE)), //
  SHEPARD2(InverseDistanceWeighting.of(SnMetricSquared.INSTANCE)), //
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
