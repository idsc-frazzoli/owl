// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LogWeightings implements LogWeighting {
  COORDINATE() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return pseudoDistances.coordinate(vectorLogManifold, variogram, sequence);
    }

    @Override
    public TensorScalarFunction build( //
        PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          pseudoDistances.coordinate(vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  },
  /***************************************************/
  NORMALIZED() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return pseudoDistances.normalized(vectorLogManifold, variogram, sequence);
    }

    @Override
    public TensorScalarFunction build( //
        PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = CrossAveraging.of( //
          pseudoDistances.normalized(vectorLogManifold, variogram, sequence), //
          RnBiinvariantMean.INSTANCE, values);
      return point -> tensorUnaryOperator.apply(point).Get();
    }
  },
  /***************************************************/
  DISTANCE() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return pseudoDistances.weighting(vectorLogManifold, variogram, sequence);
    }

    @Override
    public TensorScalarFunction build( //
        PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      throw new UnsupportedOperationException();
    }
  },
  /***************************************************/
  KRIGING() {
    @Override
    public TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      TensorUnaryOperator tensorUnaryOperator = pseudoDistances.weighting(vectorLogManifold, variogram, sequence);
      Kriging kriging = Kriging.barycentric(tensorUnaryOperator, sequence);
      return kriging::estimate;
    }

    @Override
    public TensorScalarFunction build( //
        PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
      TensorUnaryOperator tensorUnaryOperator = //
          pseudoDistances.weighting(vectorLogManifold, variogram, sequence);
      Kriging kriging = Kriging.interpolation(tensorUnaryOperator, sequence, values);
      return point -> (Scalar) kriging.estimate(point);
    }
  }, //
  ;

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
