// code by ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.core.GlcNode;

public interface RelabelDecision { // jan removed template arguments. can be reintroduced as soon as needed
  /** @param newNode
   * @param formerNode
   * @return */
  boolean doRelabel(GlcNode newNode, GlcNode formerNode);
}
