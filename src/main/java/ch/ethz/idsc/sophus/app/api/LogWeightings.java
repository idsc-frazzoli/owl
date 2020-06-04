// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.BarycentricCoordinate;
import ch.ethz.idsc.sophus.gbc.Relative0Coordinate;
import ch.ethz.idsc.sophus.gbc.Relative1Coordinate;
import ch.ethz.idsc.sophus.gbc.Relative2Coordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.ShepardWeighting;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LogWeightings implements LogWeighting {
  ID_STANDARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate projectedCoordinate = AbsoluteCoordinate.of(vectorLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point);
    }
  },
  /***************************************************/
  BI_DIAGONAL() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate projectedCoordinate = Relative0Coordinate.of(vectorLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point);
    }
  },
  BI_STANDARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      BarycentricCoordinate projectedCoordinate = Relative1Coordinate.of(vectorLogManifold, variogram);
      return point -> projectedCoordinate.weights(sequence, point);
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
      WeightingInterface weightingInterface = ShepardWeighting.absolute(vectorLogManifold, variogram);
      return point -> weightingInterface.weights(sequence, point);
    }
  },
  BI_SHEPARD() {
    @Override
    public TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      WeightingInterface weightingInterface = ShepardWeighting.relative(vectorLogManifold, variogram);
      return point -> weightingInterface.weights(sequence, point);
    }
  }, //
  ;

  public static List<LogWeighting> biinvariant() {
    return Arrays.asList( //
        BI_STANDARD, //
        BI_DIAGONAL, //
        BI_SHEPARD);
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
