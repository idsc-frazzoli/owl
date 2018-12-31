// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** TODO JPH test */
public enum WaypointDistanceCost {
  ;
  public static ImageCostFunction linear(Tensor waypoints, Tensor range, float pathWidth, Dimension resolution) {
    return new SparseImageCostFunction( //
        ImageFormat.from(WaypointDistanceImage.linear(waypoints, range, pathWidth, resolution)), //
        range, //
        RealScalar.of(WaypointDistanceImage.OFF_PATH_COST));
  }
}
