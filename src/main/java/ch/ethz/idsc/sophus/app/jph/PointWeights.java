// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;

/* package */ enum PointWeights {
  INVERSE_DISTANCE(RnInverseDistanceCoordinate.INSTANCE), //
  INVERSE_DISTANCE2(RnInverseDistanceCoordinate.SQUARED), //
  // AFFINE() {
  // @Override
  // public TensorUnaryOperator span(Tensor polygon) {
  // return AffineCoordinates.of(polygon);
  // }
  // }, //
  SHEPARD(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  SHEPARD2(InverseDistanceWeighting.of(RnMetricSquared.INSTANCE)), //
  ;

  public final BarycentricCoordinate idc;

  private PointWeights(BarycentricCoordinate idc) {
    this.idc = idc;
  }
}
