// code by jph
package ch.ethz.idsc.owl.bot.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ enum IpControls {
  ;
  /** @param stateSpaceModel
   * @param amplitude maximum absolute radial acceleration of pendulum
   * @param num
   * @return */
  public static Collection<Flow> createControls( //
      StateSpaceModel stateSpaceModel, double amplitude, int num) {
    List<Flow> list = new ArrayList<>();
    for (Tensor u : Partition.of( //
        Subdivide.of(DoubleScalar.of(-amplitude), DoubleScalar.of(amplitude), num), 1))
      list.add(StateSpaceModels.createFlow(stateSpaceModel, u));
    return list;
  }
}
