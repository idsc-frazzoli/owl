// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;

public enum Se2PointsVsRegions {
  ;
  /** @param x_coords
   * @param region
   * @return */
  public static Se2PointsVsRegion line(Tensor x_coords, Region<Tensor> region) {
    return new Se2PointsVsRegion( //
        Transpose.of(Tensors.of(x_coords, Array.zeros(x_coords.length()))), region);
  }
}
