// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Dimension;

import ch.ethz.idsc.owl.bot.r2.ImageCostFunction;
import ch.ethz.idsc.owl.bot.r2.SparseImageCostFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** slightly different from {@link ImageCostFunction}
 * because evaluation only happens at last state of trajectory */
public enum WaypointDistanceCost {
  ;
  public static ImageCostFunction linear(Tensor waypoints, Tensor range, float pathWidth, Dimension resolution) {
    return new SparseImageCostFunction( //
        ImageFormat.from(WaypointDistanceImages.linear(waypoints, range, pathWidth, resolution)), //
        range, //
        RealScalar.of(WaypointDistanceImages.OFF_PATH_COST));
  }
}
