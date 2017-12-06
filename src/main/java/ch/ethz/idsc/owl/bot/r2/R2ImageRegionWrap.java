// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Set;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class R2ImageRegionWrap {
  public static final int TTL = 15;
  // ---
  private final ImageRegion imageRegion;
  private final CostFunction costFunction;

  public R2ImageRegionWrap(Tensor tensor, Tensor range) {
    imageRegion = new ImageRegion(tensor, range, false);
    // ---
    Set<Tensor> seeds = FloodFill2D.seeds(tensor);
    Tensor cost = FloodFill2D.of(seeds, RealScalar.of(TTL), tensor);
    costFunction = new ImageCostFunction(cost.divide(DoubleScalar.of(TTL)), range, RealScalar.ZERO);
  }

  public ImageRegion imageRegion() {
    return imageRegion;
  }

  public CostFunction costFunction() {
    return costFunction;
  }
}
