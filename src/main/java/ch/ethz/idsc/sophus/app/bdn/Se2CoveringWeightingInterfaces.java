// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnNormSquared;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringTarget;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseNorm;
import ch.ethz.idsc.tensor.RealScalar;

public enum Se2CoveringWeightingInterfaces implements Supplier<WeightingInterface> {
  REL_LINEAR(RelativeCoordinate.linear(Se2CoveringManifold.INSTANCE)), //
  REL_SMOOTH(RelativeCoordinate.smooth(Se2CoveringManifold.INSTANCE)), //
  AFFINE(RelativeCoordinate.affine(Se2CoveringManifold.INSTANCE)), //
  AD_INVAR(AbsoluteCoordinate.custom( //
      Se2CoveringManifold.INSTANCE, //
      InverseNorm.of(new Se2CoveringTarget(RnNormSquared.INSTANCE, RealScalar.ONE)))), //
  ABS_LINEAR(AbsoluteCoordinate.linear(Se2CoveringManifold.INSTANCE)), //
  ABS_SMOOTH(AbsoluteCoordinate.smooth(Se2CoveringManifold.INSTANCE)), //
  ;

  private final WeightingInterface weightingInterface;

  private Se2CoveringWeightingInterfaces(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
