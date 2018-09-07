// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;

public interface RrtsNodeCollection {
  /** @param rrtsNode to insert to collection */
  void insert(RrtsNode rrtsNode);

  /** @return number of nodes in this collection */
  int size();

  /** @param end
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition terminating at end */
  Collection<RrtsNode> nearTo(Tensor end, int k_nearest);

  /** @param start
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition from start */
  Collection<RrtsNode> nearFrom(Tensor start, int k_nearest);
}
