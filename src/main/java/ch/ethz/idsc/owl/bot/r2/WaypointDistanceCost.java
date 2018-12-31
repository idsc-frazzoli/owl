// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** TODO JPH test */
public enum WaypointDistanceCost {
  ;
  public static ImageCostFunction of(Tensor waypoints, Tensor range, float pathWidth, Dimension resolution, boolean closed) {
    return new SparseImageCostFunction( //
        ImageFormat.from(WaypointDistanceImage.of(waypoints, range, pathWidth, resolution, closed)), //
        range, //
        RealScalar.of(WaypointDistanceImage.OFF_PATH_COST));
  }
}
