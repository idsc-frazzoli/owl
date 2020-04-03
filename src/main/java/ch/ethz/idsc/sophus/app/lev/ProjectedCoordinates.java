// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.function.Function;

import ch.ethz.idsc.sophus.gbc.AbsoluteCoordinate;
import ch.ethz.idsc.sophus.gbc.ProjectedCoordinate;
import ch.ethz.idsc.sophus.gbc.RelativeCoordinate;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;

public enum ProjectedCoordinates {
  ABSOLUTE_LINEAR(AbsoluteCoordinate::linear), //
  ABSOLUTE_SMOOTH(AbsoluteCoordinate::smooth), //
  RELATIVE_LINEAR(RelativeCoordinate::linear), //
  RELATIVE_SMOOTH(RelativeCoordinate::smooth), //
  RELATIVE_AFFINE(RelativeCoordinate::affine), //
  ;

  private final Function<FlattenLogManifold, ProjectedCoordinate> function;

  private ProjectedCoordinates(Function<FlattenLogManifold, ProjectedCoordinate> function) {
    this.function = function;
  }

  public ProjectedCoordinate provide(FlattenLogManifold flattenLogManifold) {
    return function.apply(flattenLogManifold);
  }
}
