// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.Relative1Coordinate;
import ch.ethz.idsc.sophus.gbc.Relative2Coordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LogWeightings implements LogWeighting {
  COORDS_ABS() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(AbsoluteCoordinate.of(vectorLogManifold, variogram), sequence);
    }
  },
  COORDS_REL1() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(Relative1Coordinate.of(vectorLogManifold, variogram), sequence);
    }
  },
  COORDS_REL2() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return Relative2Coordinate.of(vectorLogManifold, variogram, sequence);
    }
  },
  /***************************************************/
  WEIGHTING_ABS() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return PseudoDistances.ABSOLUTE.normalized(vectorLogManifold, variogram, sequence);
    }
  },
  WEIGHTING_REL1() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return PseudoDistances.RELATIVE1.normalized(vectorLogManifold, variogram, sequence);
    }
  }, //
  WEIGHTING_REL2() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return PseudoDistances.RELATIVE2.normalized(vectorLogManifold, variogram, sequence);
    }
  }, //
  ;

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
