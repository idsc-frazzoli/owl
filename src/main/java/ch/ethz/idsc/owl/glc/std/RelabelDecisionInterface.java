// code by ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.data.tree.StateCostNode;

public interface RelabelDecisionInterface<T extends StateCostNode> {
  /** @param newNode
   * @param formerNode
   * @return */
  boolean doRelabel(T newNode, T formerNode);
}
