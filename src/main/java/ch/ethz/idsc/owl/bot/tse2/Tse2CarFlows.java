// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class Tse2CarFlows implements FlowsInterface, Serializable {
  /** @param rate_max with unit [m^-1], i.e. the amount of rotation [] performed per distance [m^-1]
   * @param accelerations vector with unit [m*s^-2]
   * @return */
  public static FlowsInterface of(Scalar rate_max, Tensor accelerations) {
    return new Tse2CarFlows(rate_max, accelerations);
  }

  /***************************************************/
  private final Scalar rate_max;
  private final Tensor accelerations;

  private Tse2CarFlows(Scalar rate_max, Tensor accelerations) {
    this.rate_max = rate_max;
    this.accelerations = VectorQ.require(accelerations);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    resolution += resolution & 1;
    List<Tensor> list = new ArrayList<>();
    for (Tensor rate : Subdivide.of(rate_max.negate(), rate_max, resolution))
      for (Tensor acc : accelerations)
        list.add(Tse2CarHelper.singleton((Scalar) rate, (Scalar) acc));
    return Collections.unmodifiableList(list);
  }
}
