// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class R2ImageRegionWrap {
  public static final int TTL = 15; // TODO magic const!
  // ---
  private final ImageRegion imageRegion;
  private final CostFunction costFunction;
  private final Tensor range;
  private final Tensor cost;
  private CostFunction gradientCostFunction;

  public R2ImageRegionWrap(Tensor tensor, Tensor range) {
    imageRegion = new ImageRegion(tensor, range, false);
    this.range = range;
    // ---
    cost = FloodFill2D.of(RealScalar.of(TTL), tensor);
    costFunction = new ImageCostFunction(cost.divide(DoubleScalar.of(TTL)), range, RealScalar.ZERO);
  }

  public ImageRegion imageRegion() {
    return imageRegion;
  }

  public CostFunction costFunction() {
    return costFunction;
  }

  public CostFunction gradientCostFunction() {
    if (Objects.isNull(gradientCostFunction)) {
      ImageGradient imageGradient = ImageGradient.linear(cost, range, DoubleScalar.of(1.0));
      gradientCostFunction = new ImageGradientCostFunction(imageGradient);
    }
    return gradientCostFunction;
  }
}
