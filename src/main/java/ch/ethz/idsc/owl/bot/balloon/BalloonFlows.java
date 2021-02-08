// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class BalloonFlows implements FlowsInterface, Serializable {
  /** @param u_max with units [K * s^-1]
   * @return new ApFlows instance */
  public static FlowsInterface of(Scalar u_max) {
    return new BalloonFlows(u_max);
  }

  /***************************************************/
  private final Scalar u_max;

  private BalloonFlows(Scalar u_max) {
    this.u_max = u_max;
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor u : Subdivide.of(u_max.negate(), u_max, 1 + resolution))
      collection.add(N.DOUBLE.of(Tensors.of(u)));
    return collection;
  }
}
