// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorMetricSquared;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.sophus.math.id.InverseDistanceWeighting;

public enum LogMetricWeightings implements LogMetricWeighting {
  BI_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.linear(flattenLogManifold);
    }
  },
  BI_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.smooth(flattenLogManifold);
    }
  },
  BI_DIAGONAL_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.diagonal_linear(flattenLogManifold);
    }
  },
  BI_DIAGONAL_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.diagonal_smooth(flattenLogManifold);
    }
  },
  BI_AFFINE() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return RelativeCoordinate.affine(flattenLogManifold);
    }
  },
  /***************************************************/
  ID_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AbsoluteCoordinate.linear(flattenLogManifold);
    }
  },
  ID_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return AbsoluteCoordinate.smooth(flattenLogManifold);
    }
  },
  /***************************************************/
  IW_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return InverseDistanceWeighting.of(tensorMetric);
    }
  },
  IW_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric) {
      return InverseDistanceWeighting.of(TensorMetricSquared.of(tensorMetric));
    }
  }, //
  ;

  public static List<LogMetricWeighting> biinvariant() {
    return Arrays.asList( //
        BI_LINEAR, //
        BI_SMOOTH, //
        BI_DIAGONAL_LINEAR, //
        BI_DIAGONAL_SMOOTH, //
        BI_AFFINE);
  }

  public static List<LogMetricWeighting> barycentric() {
    return Arrays.asList( //
        BI_LINEAR, //
        BI_SMOOTH, //
        BI_DIAGONAL_LINEAR, //
        BI_DIAGONAL_SMOOTH, //
        BI_AFFINE, //
        ID_LINEAR, //
        ID_SMOOTH);
  }

  public static List<LogMetricWeighting> list() {
    return Arrays.asList(values());
  }
}
