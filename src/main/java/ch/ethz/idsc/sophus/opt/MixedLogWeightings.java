// code by jph
package ch.ethz.idsc.sophus.opt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.RadialBasisFunctionInterpolation;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum MixedLogWeightings implements LogWeighting {
  RADIAL_BASIS() {
    @Override
    public TensorUnaryOperator operator( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, //
        ScalarUnaryOperator variogram, Tensor sequence) {
      return RadialBasisFunctionInterpolation.of( //
          biinvariant.var_dist(vectorLogManifold, variogram, sequence), //
          sequence);
    }

    @Override
    public TensorScalarFunction function( //
        Biinvariant biinvariant, VectorLogManifold vectorLogManifold, //
        ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = RadialBasisFunctionInterpolation.of( //
          biinvariant.var_dist(vectorLogManifold, variogram, sequence), //
          sequence, values);
      return tensor -> (Scalar) tensorUnaryOperator.apply(tensor);
    }
  },;

  public static List<LogWeighting> scattered() { //
    List<LogWeighting> list = new ArrayList<>();
    list.addAll(LogWeightings.list());
    list.addAll(Arrays.asList(values()));
    return list;
  }
}
