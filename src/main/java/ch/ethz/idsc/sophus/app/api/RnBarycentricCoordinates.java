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
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS), sequence);
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  MEAN_VALUE() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE), sequence);
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  DISCRETE_HARMONIC() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC), sequence);
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  AFFINE() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return RnAffineCoordinate.of(sequence); // precomputation
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  RBF_RN() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap( //
          RadialBasisFunctionWeighting.of(pseudoDistances.weighting(RnManifold.INSTANCE, variogram, sequence)), sequence);
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  RBF_VL() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap( //
          RadialBasisFunctionWeighting.of(pseudoDistances.weighting(vectorLogManifold, variogram, sequence)), sequence);
    }

    @Override
    public TensorScalarFunction build(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
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
    return list;
  }
}
