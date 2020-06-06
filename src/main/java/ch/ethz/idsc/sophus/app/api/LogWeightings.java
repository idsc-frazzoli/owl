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
  ID_STANDARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(AbsoluteCoordinate.of(vectorLogManifold, variogram), sequence);
    }
  },
  /***************************************************/
  BI_STANDARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return LogWeighting.wrap(Relative1Coordinate.of(vectorLogManifold, variogram), sequence);
    }
  },
  BI_GRASSMAN() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return new Relative2Coordinate(vectorLogManifold, variogram, sequence);
    }
  },
  /***************************************************/
  ID_SHEPARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return PseudoDistances.ABSOLUTE.affine(vectorLogManifold, variogram, sequence);
    }
  },
  BI_SHEPARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return PseudoDistances.RELATIVE1.affine(vectorLogManifold, variogram, sequence);
    }
  }, //
  ;

  public static List<LogWeighting> biinvariant() {
    return Arrays.asList( //
        BI_STANDARD, //
        BI_SHEPARD);
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
