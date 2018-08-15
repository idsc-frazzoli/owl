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

  /** rewire around given RrtsNode "parent" which would
   * become the new parent to nodes that benefit from the connection
   * typically, rewire is invoked on the most recently inserted node
   * 
   * @param parent
   * @param k_nearest */
  void rewireAround(RrtsNode parent, int k_nearest);

  /** @return number of times re-wiring was effective, i.e.
   * altered the parent of an existing node */
  int rewireCount();
}
