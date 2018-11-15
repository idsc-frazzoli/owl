// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** Pendulum Swing-up controls */
/* package */ enum PsuControls {
  ;
  /** @param amplitude maximum absolute radial acceleration of pendulum
   * @param num
   * @return */
  public static Collection<Flow> createControls(double amplitude, int num) {
    List<Flow> list = new ArrayList<>();
    for (Tensor u : Partition.of(Subdivide.of(-amplitude, amplitude, num), 1))
      list.add(StateSpaceModels.createFlow(PsuStateSpaceModel.INSTANCE, u));
    return list;
  }
}
