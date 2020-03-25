// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantCoordinates;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringInverseDistanceCoordinates;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;

public enum Se2CoveringBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  BIINVARIANT(Se2CoveringBiinvariantCoordinates.LINEAR), //
  BIINVARIANT2(Se2CoveringBiinvariantCoordinates.SMOOTH), //
  AFFINE(Se2CoveringBiinvariantCoordinates.AFFINE), //
  AD_INVAR(Se2CoveringInverseDistanceCoordinates.AD_INVAR), //
  INVERSE_DISTANCE(Se2CoveringInverseDistanceCoordinates.LINEAR), //
  INVERSE_DISTANCE2(Se2CoveringInverseDistanceCoordinates.SMOOTH), //
  ;

  private final BarycentricCoordinate barycentricCoordinate;

  private Se2CoveringBarycentricCoordinates(BarycentricCoordinate barycentricCoordinate) {
    this.barycentricCoordinate = barycentricCoordinate;
  }

  @Override
  public BarycentricCoordinate get() {
    return barycentricCoordinate;
  }
}
