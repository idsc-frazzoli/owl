// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/** controls for position and velocity */
class Rice2Controls implements FlowsInterface, Serializable {
  /** @param mu coefficient, any real number
   * @param num amplitude resolution
   * @return */
  public static Collection<Flow> create1d(Scalar mu, int num) {
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    List<Flow> list = new ArrayList<>();
    for (Tensor u : Subdivide.of(-1.0, 1.0, num))
      list.add(StateSpaceModels.createFlow(stateSpaceModel, Tensors.of(u)));
    return list;
  }

  /** radial
   * 
   * @param mu coefficient, any real number
   * @param seg amplitude resolution (0, 1]
   * @param num angular resolution
   * @return */
  public static FlowsInterface create2d(Scalar mu, int seg) {
    return new Rice2Controls(mu, seg);
  }

  // ---
  private final StateSpaceModel stateSpaceModel;
  private final int seg;

  private Rice2Controls(Scalar mu, int seg) {
    stateSpaceModel = Rice2StateSpaceModel.of(mu);
    this.seg = seg;
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    Collection<Flow> collection = new HashSet<>();
    collection.add(StateSpaceModels.createFlow(stateSpaceModel, Array.zeros(2)));
    for (Tensor amp : Subdivide.of(0, 1, seg).extract(1, seg + 1))
      for (Tensor u : CirclePoints.of(resolution))
        collection.add(StateSpaceModels.createFlow(stateSpaceModel, u.multiply(amp.Get())));
    return collection;
  }
}
