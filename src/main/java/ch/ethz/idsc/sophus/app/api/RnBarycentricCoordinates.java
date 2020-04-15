// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.itp.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.itp.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnNorm;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.WeightingInterface;
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
  AFFINE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AffineCoordinate.INSTANCE;
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
      // TODO
      return new RadialBasisFunctionWeighting(new ThinPlateSplineNorm(RealScalar.of(5)));
    }
  }, //
  RBF_GAUSS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      // TODO
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

  public static List<LogMetricWeighting> list() {
    List<LogMetricWeighting> list = new ArrayList<>();
    list.addAll(LogMetricWeightings.list());
    list.addAll(Arrays.asList(values()));
    return list;
  }

  public static List<LogMetricWeighting> scattered() { //
    List<LogMetricWeighting> list = new ArrayList<>();
    list.addAll(LogMetricWeightings.list());
    list.add(AFFINE);
    list.add(RBF);
    list.add(RBF_INV_MULTI);
    list.add(RBF_TPS);
    list.add(RBF_GAUSS);
    list.add(KR_LOGNORM);
    list.add(KR_PROJECT);
    return list;
  }
}
