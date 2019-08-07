// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** utility class that generates from a given image
 * 1) {@link ImageRegion}, and
 * 2) {@link CostFunction} with given radius */
public class R2ImageRegionWrap {
  private final Region<Tensor> imageRegion;
  private final CostFunction costFunction;
  private final Tensor range;

  /** @param image
   * @param range
   * @param ttl time to live */
  public R2ImageRegionWrap(BufferedImage bufferedImage, Tensor range, int ttl) {
    imageRegion = ImageRegion.of(bufferedImage, range, false);
    Tensor cost = FloodFill2D.of(ImageFormat.from(bufferedImage), ttl);
    costFunction = new DenseImageCostFunction(cost.divide(DoubleScalar.of(ttl)), range, RealScalar.ZERO);
    this.range = range.unmodifiable();
  }

  // TODO JPH OWL 050 rename
  public Region<Tensor> imageRegion() {
    return imageRegion;
  }

  public Tensor range() {
    return range;
  }

  public CostFunction costFunction() {
    return costFunction;
  }

  public Tensor origin() {
    return Array.zeros(2);
  }
}
