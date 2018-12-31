// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** utility class that generates from a given image
 * 1) {@link ImageRegion}, and
 * 2) {@link CostFunction} with given radius */
public class R2ImageRegionWrap {
  private final ImageRegion imageRegion;
  private final CostFunction costFunction;

  /** @param image
   * @param range
   * @param ttl time to live */
  public R2ImageRegionWrap(Tensor image, Tensor range, int ttl) {
    imageRegion = new ImageRegion(image, range, false);
    Tensor cost = FloodFill2D.of(image, ttl);
    costFunction = new DenseImageCostFunction(cost.divide(DoubleScalar.of(ttl)), range, RealScalar.ZERO);
  }

  public ImageRegion imageRegion() {
    return imageRegion;
  }

  public CostFunction costFunction() {
    return costFunction;
  }
}
