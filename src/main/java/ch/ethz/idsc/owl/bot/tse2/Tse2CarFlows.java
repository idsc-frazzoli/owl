// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class Tse2CarFlows implements FlowsInterface, Serializable {
  /** @param accelerations vector with unit [m*s^-2]
   * @param rate_max with unit [rad*m^-1], i.e. the amount of rotation [rad] performed per distance [m^-1]
   * @return */
  public static FlowsInterface of(Tensor accelerations, Scalar rate_max) {
    return new Tse2CarFlows(accelerations, rate_max);
  }
  // ---

  private final Tensor accelerations;
  private final Scalar rate_max;

  private Tse2CarFlows(Tensor accelerations, Scalar rate_max) {
    this.accelerations = VectorQ.require(accelerations);
    this.rate_max = rate_max;
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    if (resolution % 2 == 1)
      ++resolution;
    List<Flow> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(rate_max.negate(), rate_max, resolution))
      for (Tensor acc : accelerations)
        list.add(Tse2CarHelper.singleton(acc.Get(), angle));
    return Collections.unmodifiableList(list);
  }
}
