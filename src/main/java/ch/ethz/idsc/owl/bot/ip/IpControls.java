// code by jph
package ch.ethz.idsc.owl.bot.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ enum IpControls {
  ;
  /** @param stateSpaceModel
   * @param amplitude maximum absolute radial acceleration of pendulum
   * @param num
   * @return */
  public static Collection<Tensor> createControls(double amplitude, int num) {
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Partition.of(Subdivide.of(-amplitude, amplitude, num), 1))
      list.add(u);
    return list;
  }
}
