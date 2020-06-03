// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.AffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public BarycentricCoordinate from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return R2BarycentricCoordinate.of(Barycenter.WACHSPRESS);
    }
  },
  MEAN_VALUE() {
    @Override
    public BarycentricCoordinate from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE);
    }
  },
  DISCRETE_HARMONIC() {
    @Override
    public BarycentricCoordinate from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC);
    }
  },
  AFFINE() {
    @Override
    public BarycentricCoordinate from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return AffineCoordinate.INSTANCE;
    }
  },
  RBF() {
    @Override
    public WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create(RnManifold.INSTANCE, r -> r));
    }
  }, //
  // TODO variogram
  RBF_INV_MULTI() {
    @Override
    public WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create( //
          flattenLogManifold, variogram) //
      );
    }
  },
  KR_ABSOLUTE() {
    @Override
    public WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return PseudoDistances.ABSOLUTE.weighting(flattenLogManifold, variogram);
    }
  }, //
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
    list.add(KR_ABSOLUTE);
    return list;
  }
}
