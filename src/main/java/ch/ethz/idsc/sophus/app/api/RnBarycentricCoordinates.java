// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.function.Supplier;

import ch.ethz.idsc.sophus.itp.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.itp.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.itp.KrigingWeighting;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.itp.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.lie.rn.RnNorm;
import ch.ethz.idsc.sophus.math.win.AffineCoordinate;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.RealScalar;

public enum RnBarycentricCoordinates implements Supplier<BarycentricCoordinate> {
  WACHSPRESS(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS)), //
  MEAN_VALUE(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE)), //
  DISCRETE_HARMONIC(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  BIINVARIANT1(RnBiinvariantCoordinate.INSTANCE), //
  BIINVARIANT2(RnBiinvariantCoordinate.SQUARED), //
  BIINVARIANTD1(RnBiinvariantCoordinate.DIAGONAL), //
  BIINVARIANTD2(RnBiinvariantCoordinate.DIAGONAL_SQUARED), //
  INVERSE_DISTANCE1(RnInverseDistanceCoordinate.INSTANCE), //
  INVERSE_DISTANCE2(RnInverseDistanceCoordinate.SQUARED), //
  AFFINE(AffineCoordinate.INSTANCE), //
  SHEPARD1(InverseDistanceWeighting.of(RnMetric.INSTANCE)), //
  SHEPARD2(InverseDistanceWeighting.of(RnMetricSquared.INSTANCE)), //
  RBF(new RadialBasisFunctionWeighting(RnNorm.INSTANCE)), //
  RBF_INV_MULTI(new RadialBasisFunctionWeighting(new InverseMultiquadricNorm(RealScalar.of(5)))), //
  RBF_TPS(new RadialBasisFunctionWeighting(new ThinPlateSplineNorm(RealScalar.of(5)))), //
  RBF_GAUSS(new RadialBasisFunctionWeighting(new GaussianRadialBasisFunction(RealScalar.of(5)))), //
  KRIGING(new KrigingWeighting(RnNorm.INSTANCE)), //
  ;

  public static final RnBarycentricCoordinates[] SCATTERED = { //
      BIINVARIANT1, BIINVARIANT2, //
      BIINVARIANTD1, BIINVARIANTD2, //
      INVERSE_DISTANCE1, INVERSE_DISTANCE2, //
      AFFINE, SHEPARD1, SHEPARD2, //
      RBF, //
      RBF_INV_MULTI, RBF_TPS, RBF_GAUSS, //
      KRIGING };
  private final BarycentricCoordinate barycentricCoordinate;

  private RnBarycentricCoordinates(BarycentricCoordinate barycentricCoordinate) {
    this.barycentricCoordinate = barycentricCoordinate;
    // KrigingInterpolation.barycentric(RnNorm.INSTANCE, sequence);
  }

  @Override
  public BarycentricCoordinate get() {
    return barycentricCoordinate;
  }
}
