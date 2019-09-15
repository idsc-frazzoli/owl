// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;

public interface RrtsNodeCollection {
  /** @param rrtsNode to insert to collection */
  void insert(RrtsNode rrtsNode);

  /** @return number of nodes in this collection */
  int size();

  /** Originally, "nearTo" acts as a heuristic to provide the rewiring step with nodes
   * that are likely to reduce the cost from root. However, the algorithm and API does
   * not require the k returned nodes to be related to the cost function.
   * 
   * If given parameter end is also a state of a node in the collection, the node may
   * be contained in the returned set.
   * 
   * Typically, the insert function is called after the rewire step so as not to include
   * the given state end.
   * 
   * @param end
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition terminating at end */
  Collection<RrtsNode> nearTo(Tensor end, int k_nearest);

  /** @param start
   * @param k_nearest
   * @return k_nearest nodes according to the cost of their transition from start */
  Collection<RrtsNode> nearFrom(Tensor start, int k_nearest);
}
