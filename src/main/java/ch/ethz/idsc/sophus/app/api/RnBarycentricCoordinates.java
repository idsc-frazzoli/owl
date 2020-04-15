// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.itp.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.itp.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.lie.rn.RnNorm;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.RealScalar;

public enum RnBarycentricCoordinates implements LogMetricWeighting {
  WACHSPRESS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return R2BarycentricCoordinate.of(Barycenter.WACHSPRESS);
    }
  }, //
  MEAN_VALUE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE);
    }
  }, //
  DISCRETE_HARMONIC() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC);
    }
  }, //
  BI_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.linear(flattenLogManifold);
    }
  }, //
  BI_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.smooth(flattenLogManifold);
    }
  }, //
  BIINVARIANTD1() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.diagonal_linear(flattenLogManifold);
    }
  }, //
  BIINVARIANTD2() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.diagonal_smooth(flattenLogManifold);
    }
  }, //
  ID_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AbsoluteCoordinate.linear(flattenLogManifold);
    }
  }, //
  ID_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AbsoluteCoordinate.smooth(flattenLogManifold);
    }
  }, //
  AFFINE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AffineCoordinate.INSTANCE;
    }
  }, //
  IW_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return InverseDistanceWeighting.of(tensorMetric);
    }
  }, //
  IW_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      // TODO
      return InverseDistanceWeighting.of(RnMetricSquared.INSTANCE);
    }
  }, //
  RBF() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return new RadialBasisFunctionWeighting(RnNorm.INSTANCE);
    }
  }, //
  RBF_INV_MULTI() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      // TODO
      return new RadialBasisFunctionWeighting(new InverseMultiquadricNorm(RealScalar.of(5)));
    }
  }, //
  RBF_TPS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return new RadialBasisFunctionWeighting(new ThinPlateSplineNorm(RealScalar.of(5)));
    }
  }, //
  RBF_GAUSS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return new RadialBasisFunctionWeighting(new GaussianRadialBasisFunction(RealScalar.of(5)));
    }
  }, //
  KR_LOGNORM() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return Krigings.ABSOLUTE.weighting(flattenLogManifold, s -> s);
    }
  }, // TODO variogram
  KR_PROJECT() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return Krigings.RELATIVE.weighting(flattenLogManifold, s -> s);
    }
  }, // TODO variogram
  ;

  public static final RnBarycentricCoordinates[] SCATTERED = { //
      BI_LINEAR, BI_SMOOTH, //
      BIINVARIANTD1, BIINVARIANTD2, //
      ID_LINEAR, ID_SMOOTH, //
      AFFINE, IW_LINEAR, IW_SMOOTH, //
      RBF, //
      RBF_INV_MULTI, RBF_TPS, RBF_GAUSS, //
      KR_LOGNORM, KR_PROJECT };
}
