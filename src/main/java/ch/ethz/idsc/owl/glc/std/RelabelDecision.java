// code by ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.core.GlcNode;

public interface RelabelDecision {
  /** @param newNode
   * @param formerNode
   * @return */
  boolean doRelabel(GlcNode newNode, GlcNode formerNode);
}
