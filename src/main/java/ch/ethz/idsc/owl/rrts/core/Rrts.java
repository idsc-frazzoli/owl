// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

/** Rapidly exploring random trees
 * 
 * Implementation based on
 * Sertac Karaman and Emilio Frazzoli, 2011:
 * Sampling-based algorithms for optimal motion planning
 * Algorithm 6, p.855 */
public interface Rrts {
  /** @param state
   * @param k_nearest
   * @return */
  Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest);

  /** @param rrtsNode
   * @param k_nearest */
  void rewireAround(RrtsNode rrtsNode, int k_nearest);

  /** @return number of times re-wiring was effective, i.e.
   * altered the parent of an existing node */
  int rewireCount();
}
