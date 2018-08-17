// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Collection;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface RrtsNode extends StateCostNode {
  /** @param state
   * @param cost
   * @return new node holding state and cost with parent == null */
  static RrtsNode createRoot(Tensor state, Scalar cost) {
    return new RrtsNodeImpl(state, cost);
  }

  // ---
  @Override // from Node
  RrtsNode parent();

  @Override // from Node
  Collection<? extends RrtsNode> children();

  /** @param state
   * @param costFromRoot
   * @return new node holding state and cost with parent == this */
  RrtsNode connectTo(Tensor state, Scalar costFromRoot);

  /** replace parent of given child with this as new parent,
   * and update all costs in the subtree of child
   * 
   * @param child
   * @param costFromParent cost of transition between this and child */
  void rewireTo(RrtsNode child, Scalar costFromParent);
}
