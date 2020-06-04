// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnAffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS), sequence);
    }
  },
  MEAN_VALUE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE), sequence);
    }
  },
  DISCRETE_HARMONIC() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC), sequence);
    }
  },
  AFFINE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return RnAffineCoordinate.of(sequence); // precomputation
    }
  },
  RBF_RN() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap( //
          RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create(RnManifold.INSTANCE, variogram)), sequence);
    }
  },
  RBF_VL() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap( //
          RadialBasisFunctionWeighting.of(PseudoDistances.ABSOLUTE.create(vectorLogManifold, variogram)), sequence);
    }
  },
  KR_ABSOLUTE() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(PseudoDistances.ABSOLUTE.weighting(vectorLogManifold, variogram), sequence);
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
    list.add(RBF_RN);
    list.add(RBF_VL);
    list.add(KR_ABSOLUTE);
    return list;
  }
}
