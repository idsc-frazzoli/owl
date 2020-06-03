// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.ProjectedCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.ShepardWeighting;
import ch.ethz.idsc.sophus.math.WeightingInterface;
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
    public ProjectedCoordinate from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return RelativeCoordinate.diagonal(flattenLogManifold, variogram);
    }
  },
  BI_STANDARD() {
    @Override
    public ProjectedCoordinate from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram) {
      return RelativeCoordinate.of(vectorLogManifold, variogram);
    }
  },
  /***************************************************/
  ID_SHEPARD() {
    @Override
    public WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return ShepardWeighting.absolute(flattenLogManifold, variogram);
    }
  },
  BI_SHEPARD() {
    @Override
    public WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram) {
      return ShepardWeighting.relative(flattenLogManifold, variogram);
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
