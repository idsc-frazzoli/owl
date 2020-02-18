// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.math.win.AffineCoordinate;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;

public enum RnBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  WACHSPRESS(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS)), //
  MEAN_VALUE(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE)), //
  DISCRETE_HARMONIC(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  INVERSE_DISTANCE(RnInverseDistanceCoordinate.INSTANCE), //
  INVERSE_DISTANCE2(RnInverseDistanceCoordinate.SQUARED), //
  AFFINE(AffineCoordinate.INSTANCE), //
  SHEPARD(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  SHEPARD2(InverseDistanceWeighting.of(RnMetricSquared.INSTANCE)), //
  ;

  public static final RnBarycentricCoordinates[] SCATTERED = { //
      INVERSE_DISTANCE, INVERSE_DISTANCE2, AFFINE, SHEPARD, SHEPARD2 };
  private final BarycentricCoordinate barycentricCoordinate;

  private RnBarycentricCoordinates(BarycentricCoordinate barycentricCoordinate) {
    this.barycentricCoordinate = barycentricCoordinate;
  }

  @Override
  public BarycentricCoordinate get() {
    return barycentricCoordinate;
  }
}
