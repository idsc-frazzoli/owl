// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Set;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.img.FloodFill2D;
import ch.ethz.idsc.owl.img.ImageCostFunction;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class R2ImageRegionWrap {
  private final ImageRegion imageRegion;
  private final CostFunction costFunction;

  public R2ImageRegionWrap(Tensor tensor, Tensor range) {
    imageRegion = new ImageRegion(tensor, range, false);
    // ---
    Set<Tensor> seeds = FloodFill2D.seeds(tensor);
    final int ttl = 15; // magic const
    Tensor cost = FloodFill2D.of(seeds, RealScalar.of(ttl), tensor);
    costFunction = new ImageCostFunction(cost.divide(DoubleScalar.of(ttl)), range, RealScalar.ZERO);
  }

  public ImageRegion imageRegion() {
    return imageRegion;
  }

  public CostFunction costFunction() {
    return costFunction;
  }
}
