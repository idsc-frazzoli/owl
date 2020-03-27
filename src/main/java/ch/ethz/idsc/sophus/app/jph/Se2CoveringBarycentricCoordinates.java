// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBiinvariantCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnNormSquared;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringTarget;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseNorm;
import ch.ethz.idsc.tensor.RealScalar;

public enum Se2CoveringBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  BI_LINEAR(HsBiinvariantCoordinate.linear(Se2CoveringManifold.INSTANCE)), //
  BI_SMOOTH(HsBiinvariantCoordinate.smooth(Se2CoveringManifold.INSTANCE)), //
  AFFINE(HsBarycentricCoordinate.affine(Se2CoveringManifold.INSTANCE)), //
  AD_INVAR(HsBarycentricCoordinate.custom( //
      Se2CoveringManifold.INSTANCE, //
      InverseNorm.of(new Se2CoveringTarget(RnNormSquared.INSTANCE, RealScalar.ONE)))), //
  ID_LINEAR(HsBarycentricCoordinate.linear(Se2CoveringManifold.INSTANCE)), //
  ID_SMOOTH(HsBarycentricCoordinate.smooth(Se2CoveringManifold.INSTANCE)), //
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
