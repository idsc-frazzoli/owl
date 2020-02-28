// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringAffineCoordinate;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantCoordinate;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;

public enum Se2CoveringBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  BIINVARIANT(Se2CoveringBiinvariantCoordinate.INSTANCE), //
  BIINVARIANT2(Se2CoveringBiinvariantCoordinate.SQUARED), //
  AFFINE(Se2CoveringAffineCoordinate.INSTANCE), //
  AD_INVAR(Se2CoveringInverseDistanceCoordinate.AD_INVAR), //
  INVERSE_DISTANCE(Se2CoveringInverseDistanceCoordinate.INSTANCE), //
  INVERSE_DISTANCE2(Se2CoveringInverseDistanceCoordinate.SQUARED), //
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
