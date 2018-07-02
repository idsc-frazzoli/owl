// code by ynager
package ch.ethz.idsc.owl.glc.std;

import ch.ethz.idsc.owl.glc.core.GlcNode;

public interface RelabelDecision {
  /** @param newNode
   * @param oldNode
   * @return */
  boolean doRelabel(GlcNode newNode, GlcNode oldNode);
}
