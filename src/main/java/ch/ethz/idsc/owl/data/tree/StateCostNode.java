// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** base functionality for a node in a tree
 * that has associated state and cost */
public interface StateCostNode extends Node {
  @Override // from Node
  StateCostNode parent();

  @Override // from Node
  Collection<? extends StateCostNode> children();

  /** @return state associated to this node */
  Tensor state();

  /** @return cost from root to this node */
  Scalar costFromRoot();
}
