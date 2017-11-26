// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.Optional;

import ch.ethz.idsc.owl.data.tree.StateCostNode;

public interface ExpandInterface<T extends StateCostNode> {
  /** retrieves next node and removes the node from the queue
   * 
   * @return next node for expansion, or Optional.empty() if no such node exists */
  Optional<T> pollNext();

  /** performs expansion at given node
   * 
   * @param node */
  void expand(T node);

  /** @return best node in goal region, or Optional.empty() if no such node has been identified yet */
  Optional<T> getBest();
}
