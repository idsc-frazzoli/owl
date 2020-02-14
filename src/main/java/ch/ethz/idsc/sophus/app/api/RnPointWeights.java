// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public enum RnPointWeights {
  INVERSE_DISTANCE_WEIGHTING(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  INVERSE_DISTANCE_WEIGHTING2(InverseDistanceWeighting.of(Norm2Squared::between)), //
  INVERSE_DISTANCE_COORDINATES(RnInverseDistanceCoordinate.INSTANCE), //
  ;

  public final BarycentricCoordinate idc;

  private RnPointWeights(BarycentricCoordinate idc) {
    this.idc = idc;
  }
}
