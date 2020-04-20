// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
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
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.RealScalar;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public BarycentricCoordinate from(FlattenLogManifold flattenLogManifold) {
      return R2BarycentricCoordinate.of(Barycenter.WACHSPRESS);
    }
  }, //
  MEAN_VALUE() {
    @Override
    public BarycentricCoordinate from(FlattenLogManifold flattenLogManifold) {
      return R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE);
    }
  }, //
  DISCRETE_HARMONIC() {
    @Override
    public BarycentricCoordinate from(FlattenLogManifold flattenLogManifold) {
      return R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC);
    }
  }, //
  AFFINE() {
    @Override
    public BarycentricCoordinate from(FlattenLogManifold flattenLogManifold) {
      return AffineCoordinate.INSTANCE;
    }
  }, //
  RBF() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return RadialBasisFunctionWeighting.of(RnNorm.INSTANCE);
    }
  }, //
  RBF_INV_MULTI() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      // TODO non generic
      return RadialBasisFunctionWeighting.of(new InverseMultiquadricNorm(RealScalar.of(5)));
    }
  }, //
  RBF_TPS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      // TODO non generic
      return RadialBasisFunctionWeighting.of(ThinPlateSplineNorm.of(RealScalar.of(5)));
    }
  }, //
  RBF_GAUSS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      // TODO
      return RadialBasisFunctionWeighting.of(GaussianRadialBasisFunction.of(RealScalar.of(5)));
    }
  }, //
  KR_LOGNORM() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return Krigings.ABSOLUTE.weighting(flattenLogManifold, s -> s);
    }
  }, // TODO variogram
  KR_PROJECT() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return Krigings.RELATIVE.weighting(flattenLogManifold, s -> s);
    }
  }, // TODO variogram
  ;

  public static List<LogWeighting> list() {
    List<LogWeighting> list = new ArrayList<>();
    list.addAll(LogWeightings.list());
    list.addAll(Arrays.asList(values()));
    return list;
  }

  public static List<LogWeighting> scattered() { //
    List<LogWeighting> list = new ArrayList<>();
    list.addAll(LogWeightings.list());
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
