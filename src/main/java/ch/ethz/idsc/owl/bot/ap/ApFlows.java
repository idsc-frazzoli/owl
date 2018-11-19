// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.N;

public class ApFlows implements FlowsInterface, Serializable {
  /** @param aoa_max with unit [rad],
   * @param thrusts vector with unit [N]
   * @return */
  public static FlowsInterface of(StateSpaceModel stateSpaceModel, Scalar aoa_max, Tensor thrusts) {
    return new ApFlows(stateSpaceModel, aoa_max, thrusts);
  }

  // ---
  private final StateSpaceModel stateSpaceModel;
  private final Scalar aoa_max;
  private final Tensor thrusts;

  private ApFlows(StateSpaceModel stateSpaceModel, Scalar aoa_max, Tensor thrusts) {
    this.stateSpaceModel = stateSpaceModel;
    this.aoa_max = aoa_max;
    this.thrusts = VectorQ.require(thrusts);
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    if (resolution % 2 == 1)
      ++resolution;
    Collection<Flow> collection = new ArrayList<>();
    for (Tensor aoa : Subdivide.of(RealScalar.ZERO, aoa_max, resolution))
      for (Tensor thrust : thrusts) {
        collection.add(StateSpaceModels.createFlow(stateSpaceModel, N.DOUBLE.of(Tensors.of(aoa.Get(), thrust.Get()))));
      }
    return collection;
  }

  public static void main(String[] args) {
    System.out.println(N.DOUBLE.of(Tensors.of(RealScalar.of(1), RealScalar.of(2))));
  }
}
