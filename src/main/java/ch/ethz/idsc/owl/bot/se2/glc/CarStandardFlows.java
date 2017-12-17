// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** controls allow to drive forward and backward */
public class CarStandardFlows extends CarFlows {
  private final Scalar speed;
  private final Scalar rate_max;

  /** @param speed with unit [m*s^-1]
   * @param rate_max with unit [rad*m^-1], i.e. the amount of
   * rotation [rad] performed per distance [m^-1] */
  public CarStandardFlows(Scalar speed, Scalar rate_max) {
    this.speed = speed;
    this.rate_max = rate_max;
  }

  @Override
  public Collection<Flow> getFlows(int resolution) {
    List<Flow> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(rate_max.negate(), rate_max, resolution)) {
      list.add(singleton(speed, angle));
      list.add(singleton(speed.negate(), angle));
    }
    return list;
  }
}
