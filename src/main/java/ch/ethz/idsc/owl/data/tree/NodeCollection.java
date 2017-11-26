// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;

public interface NodeCollection<T extends Node> {
  /** @param node to insert to collection */
  void insert(T node);

  /** @param end
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition terminating at end */
  Collection<T> nearTo(Tensor end, int k_nearest);

  /** @param start
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition from start */
  Collection<T> nearFrom(Tensor start, int k_nearest);

  /** @return number of nodes in this collection */
  int size();
}
