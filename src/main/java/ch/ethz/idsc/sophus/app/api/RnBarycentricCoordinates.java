// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionWeighting;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnAffineCoordinate;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum RnBarycentricCoordinates implements LogWeighting {
  WACHSPRESS() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return wrap(R2BarycentricCoordinate.of(Barycenter.WACHSPRESS), sequence);
    }

    @Override
    public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  MEAN_VALUE() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return wrap(R2BarycentricCoordinate.of(Barycenter.MEAN_VALUE), sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  DISCRETE_HARMONIC() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return wrap(R2BarycentricCoordinate.of(Barycenter.DISCRETE_HARMONIC), sequence);
    }

    @Override
    public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  AFFINE() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return RnAffineCoordinate.of(sequence); // precomputation
    }

    @Override
    public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  RBF_RN() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return wrap(RadialBasisFunctionWeighting.of(biinvariant.distances(RnManifold.INSTANCE, sequence)), sequence);
    }

    @Override
    public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence,
        Tensor values) {
      // TODO Auto-generated method stub
      return null;
    }
  },
  RBF_VL() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return wrap(RadialBasisFunctionWeighting.of(biinvariant.distances(vectorLogManifold, sequence)), sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
        Tensor sequence, Tensor values) {
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

  private static TensorUnaryOperator wrap(WeightingInterface weightingInterface, Tensor sequence) {
    Objects.requireNonNull(weightingInterface);
    Objects.requireNonNull(sequence);
    return point -> weightingInterface.weights(sequence, point);
  }
}
