// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class ApFlows implements FlowsInterface, Serializable {
  /** @param aoa_max with unit [rad],
   * @param thrusts vector with unit [N]
   * @return */
  public static FlowsInterface of(Scalar aoa_max, Tensor thrusts) {
    return new ApFlows(aoa_max, thrusts);
  }

  // ---
  private final Scalar aoa_max;
  private final Tensor thrusts;

  private ApFlows(Scalar aoa_max, Tensor thrusts) {
    this.aoa_max = aoa_max;
    this.thrusts = VectorQ.require(thrusts);
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    if (resolution % 2 == 1)
      ++resolution;
    List<Flow> list = new ArrayList<>();
    for (Tensor aoa : Subdivide.of(RealScalar.ZERO, aoa_max, resolution))
      for (Tensor thrust : thrusts)
        list.add(ApHelper.singleton(aoa.Get(), thrust.Get()));
    return Collections.unmodifiableList(list);
  }
}
