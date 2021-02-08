// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/** controls for position and velocity */
/* package */ class Rice2Controls implements FlowsInterface, Serializable {
  /** @param mu coefficient, any real number
   * @param num amplitude resolution
   * @return */
  public static Collection<Tensor> create1d(int num) {
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Subdivide.of(-1.0, 1.0, num))
      list.add(Tensors.of(u));
    return list;
  }

  /** radial
   * 
   * @param mu coefficient, any real number
   * @param seg amplitude resolution (0, 1]
   * @param num angular resolution
   * @return */
  public static FlowsInterface create2d(int seg) {
    return new Rice2Controls(seg);
  }

  /***************************************************/
  private final int seg;

  private Rice2Controls(int seg) {
    this.seg = seg;
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new HashSet<>();
    collection.add(Array.zeros(2));
    for (Tensor amp : Subdivide.of(0, 1, seg).extract(1, seg + 1))
      for (Tensor u : CirclePoints.of(resolution))
        collection.add(u.multiply((Scalar) amp));
    return collection;
  }
}
