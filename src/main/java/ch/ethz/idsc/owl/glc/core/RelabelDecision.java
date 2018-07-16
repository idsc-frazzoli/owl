// code by ynager
package ch.ethz.idsc.owl.glc.core;

public interface RelabelDecision {
  /** @param newNode
   * @param oldNode
   * @return */
  boolean doRelabel(GlcNode newNode, GlcNode oldNode);
}
