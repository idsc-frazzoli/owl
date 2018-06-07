package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.data.tree.StateCostNode;

public interface RelabelDecisionInterface {
  // ---
  boolean doRelabel(StateCostNode newNode, StateCostNode formerNode);
}
