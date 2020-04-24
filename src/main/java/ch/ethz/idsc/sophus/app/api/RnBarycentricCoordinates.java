// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.krg.GaussianRadialBasisFunction;
import ch.ethz.idsc.sophus.krg.InverseMultiquadricNorm;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.krg.ThinPlateSplineNorm;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
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
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.of(RnManifold.INSTANCE, r -> r));
    }
  }, //
  RBF_INV_MULTI() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.of( //
          flattenLogManifold, new InverseMultiquadricNorm(RealScalar.of(5))) //
      );
    }
  }, //
  RBF_TPS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.of( //
          flattenLogManifold, ThinPlateSplineNorm.of(RealScalar.of(5))));
    }
  }, //
  RBF_GAUSS() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.of( //
          flattenLogManifold, GaussianRadialBasisFunction.of(RealScalar.of(5))));
    }
  }, //
  KR_ABSOLUTE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return PseudoDistances.ABSOLUTE.weighting(flattenLogManifold, s -> s);
    }
  }, // TODO variogram
  KR_RELATIVE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return PseudoDistances.RELATIVE.weighting(flattenLogManifold, s -> s);
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
    list.add(KR_ABSOLUTE);
    list.add(KR_RELATIVE);
    return list;
  }
}
