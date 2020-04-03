// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.itp.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.itp.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.itp.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.lie.rn.RnNorm;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.RealScalar;

public enum RnBarycentricCoordinates implements Supplier<WeightingInterface> {
  WACHSPRESS(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS)), //
  MEAN_VALUE(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE)), //
  DISCRETE_HARMONIC(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  BI_LINEAR(RelativeCoordinate.linear(RnManifold.INSTANCE)), //
  BI_SMOOTH(RelativeCoordinate.smooth(RnManifold.INSTANCE)), //
  BIINVARIANTD1(RelativeCoordinate.diagonal_linear(RnManifold.INSTANCE)), //
  BIINVARIANTD2(RelativeCoordinate.diagonal_smooth(RnManifold.INSTANCE)), //
  ID_LINEAR(AbsoluteCoordinate.linear(RnManifold.INSTANCE)), //
  ID_SMOOTH(AbsoluteCoordinate.smooth(RnManifold.INSTANCE)), //
  AFFINE(AffineCoordinate.INSTANCE), //
  IW_LINEAR(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(RnMetricSquared.INSTANCE)), //
  RBF(new RadialBasisFunctionWeighting(RnNorm.INSTANCE)), //
  RBF_INV_MULTI(new RadialBasisFunctionWeighting(new InverseMultiquadricNorm(RealScalar.of(5)))), //
  RBF_TPS(new RadialBasisFunctionWeighting(new ThinPlateSplineNorm(RealScalar.of(5)))), //
  RBF_GAUSS(new RadialBasisFunctionWeighting(new GaussianRadialBasisFunction(RealScalar.of(5)))), //
  KR_LOGNORM(Krigings.ABSOLUTE.weighting(RnManifold.INSTANCE, s -> s)), // TODO variogram
  KR_PROJECT(Krigings.RELATIVE.weighting(RnManifold.INSTANCE, s -> s)), // TODO variogram
  ;

  public static final RnBarycentricCoordinates[] SCATTERED = { //
      BI_LINEAR, BI_SMOOTH, //
      BIINVARIANTD1, BIINVARIANTD2, //
      ID_LINEAR, ID_SMOOTH, //
      AFFINE, IW_LINEAR, IW_SMOOTH, //
      RBF, //
      RBF_INV_MULTI, RBF_TPS, RBF_GAUSS, //
      KR_LOGNORM, KR_PROJECT };
  private final WeightingInterface weightingInterface;

  private RnBarycentricCoordinates(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
