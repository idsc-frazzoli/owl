// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.HsBiinvariantCoordinate;
import ch.ethz.idsc.sophus.itp.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.itp.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.itp.KrigingWeighting;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.itp.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.lie.rn.RnNorm;
import ch.ethz.idsc.sophus.math.win.AffineCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.sophus.math.win.WeightingInterface;
import ch.ethz.idsc.tensor.RealScalar;

public enum RnBarycentricCoordinates implements Supplier<WeightingInterface> {
  WACHSPRESS(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS)), //
  MEAN_VALUE(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE)), //
  DISCRETE_HARMONIC(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  BI_LINEAR(HsBiinvariantCoordinate.linear(RnManifold.INSTANCE)), //
  BI_SMOOTH(HsBiinvariantCoordinate.smooth(RnManifold.INSTANCE)), //
  BIINVARIANTD1(HsBiinvariantCoordinate.diagonal_linear(RnManifold.INSTANCE)), //
  BIINVARIANTD2(HsBiinvariantCoordinate.diagonal_smooth(RnManifold.INSTANCE)), //
  ID_LINEAR(HsBarycentricCoordinate.linear(RnManifold.INSTANCE)), //
  ID_SMOOTH(HsBarycentricCoordinate.smooth(RnManifold.INSTANCE)), //
  AFFINE(AffineCoordinate.INSTANCE), //
  IW_LINEAR(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  IW_SMOOTH(InverseDistanceWeighting.of(RnMetricSquared.INSTANCE)), //
  RBF(new RadialBasisFunctionWeighting(RnNorm.INSTANCE)), //
  RBF_INV_MULTI(new RadialBasisFunctionWeighting(new InverseMultiquadricNorm(RealScalar.of(5)))), //
  RBF_TPS(new RadialBasisFunctionWeighting(new ThinPlateSplineNorm(RealScalar.of(5)))), //
  RBF_GAUSS(new RadialBasisFunctionWeighting(new GaussianRadialBasisFunction(RealScalar.of(5)))), //
  KRIGING(new KrigingWeighting(s -> s)), // TODO variogram
  ;

  public static final RnBarycentricCoordinates[] SCATTERED = { //
      BI_LINEAR, BI_SMOOTH, //
      BIINVARIANTD1, BIINVARIANTD2, //
      ID_LINEAR, ID_SMOOTH, //
      AFFINE, IW_LINEAR, IW_SMOOTH, //
      RBF, //
      RBF_INV_MULTI, RBF_TPS, RBF_GAUSS, //
      KRIGING };
  private final WeightingInterface weightingInterface;

  private RnBarycentricCoordinates(WeightingInterface weightingInterface) {
    this.weightingInterface = weightingInterface;
  }

  @Override
  public WeightingInterface get() {
    return weightingInterface;
  }
}
