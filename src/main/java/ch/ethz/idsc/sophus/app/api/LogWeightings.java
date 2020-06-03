// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.GrCoordinate;
import ch.ethz.idsc.sophus.gbc.ProjectedCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.ShepardWeighting;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LogWeightings implements LogWeighting {
  ID_STANDARD() {
    @Override
    public ProjectedCoordinate from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return AbsoluteCoordinate.of(vectorLogManifold, variogram);
    }
  },
  /***************************************************/
  BI_DIAGONAL() {
    @Override
    public ProjectedCoordinate from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return RelativeCoordinate.diagonal(vectorLogManifold, variogram);
    }
  },
  BI_STANDARD() {
    @Override
    public ProjectedCoordinate from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return RelativeCoordinate.of(vectorLogManifold, variogram);
    }
  },
  BI_GRASSMAN() {
    @Override
    public WeightingInterface from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TensorUnaryOperator ops(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
      return new GrCoordinate(vectorLogManifold, variogram, sequence);
    }
  },
  /***************************************************/
  ID_SHEPARD() {
    @Override
    public WeightingInterface from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return ShepardWeighting.absolute(vectorLogManifold, variogram);
    }
  },
  BI_SHEPARD() {
    @Override
    public WeightingInterface from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return ShepardWeighting.relative(vectorLogManifold, variogram);
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
