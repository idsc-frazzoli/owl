// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.ProjectedCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.krg.ShepardWeighting;
import ch.ethz.idsc.sophus.math.WeightingInterface;

public enum LogWeightings implements LogWeighting {
  ID_LINEAR() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return AbsoluteCoordinate.linear(flattenLogManifold);
    }
  },
  ID_SMOOTH() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return AbsoluteCoordinate.smooth(flattenLogManifold);
    }
  },
  /***************************************************/
  BI_LINEAR() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return RelativeCoordinate.linear(flattenLogManifold);
    }
  },
  BI_SMOOTH() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return RelativeCoordinate.smooth(flattenLogManifold);
    }
  },
  BI_DIAGONAL_LINEAR() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return RelativeCoordinate.diagonal_linear(flattenLogManifold);
    }
  },
  BI_DIAGONAL_SMOOTH() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return RelativeCoordinate.diagonal_smooth(flattenLogManifold);
    }
  },
  BI_AFFINE() {
    @Override
    public ProjectedCoordinate from(FlattenLogManifold flattenLogManifold) {
      return RelativeCoordinate.affine(flattenLogManifold);
    }
  },
  /***************************************************/
  ID_S_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return ShepardWeighting.absolute(flattenLogManifold, 1);
    }
  },
  ID_S_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return ShepardWeighting.absolute(flattenLogManifold, 2);
    }
  },
  BI_S_LINEAR() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return ShepardWeighting.relative(flattenLogManifold, 1);
    }
  },
  BI_S_SMOOTH() {
    @Override
    public WeightingInterface from(FlattenLogManifold flattenLogManifold) {
      return ShepardWeighting.relative(flattenLogManifold, 2);
    }
  }, //
  ;

  public static List<LogWeighting> biinvariant() {
    return Arrays.asList( //
        BI_LINEAR, //
        BI_SMOOTH, //
        BI_DIAGONAL_LINEAR, //
        BI_DIAGONAL_SMOOTH, //
        BI_AFFINE, //
        BI_S_LINEAR, //
        BI_S_SMOOTH);
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }
}
