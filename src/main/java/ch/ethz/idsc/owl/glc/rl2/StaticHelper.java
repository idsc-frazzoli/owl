// code by yn, jph
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum StaticHelper {
  ;
  private static final Chop CHOP = Chop._09;

  /** Checks whether the merit of node next is numerically close any merit of any other node within the domain queue.
   * This shall not be confused with the slack margin.
   * 
   * @param next
   * @param relaxedPriorityQueue
   * @return true if any match for closeness */
  static boolean isSimilar(GlcNode next, RelaxedPriorityQueue relaxedPriorityQueue) {
    Scalar nextMerit = next.merit();
    return relaxedPriorityQueue.collection().stream() //
        .map(GlcNode::merit) //
        .anyMatch(merit -> CHOP.isClose(merit, nextMerit)); //
  }

  /** Returns number of nodes with similar merits to best merit within domain queue.
   * 
   * @param relaxedPriorityQueue
   * @return long */
  static int numberEquals(RelaxedPriorityQueue relaxedPriorityQueue) {
    Scalar bestMerit = relaxedPriorityQueue.peekBest().merit();
    return (int) relaxedPriorityQueue.collection().stream() //
        .map(GlcNode::merit) //
        .filter(merit -> CHOP.isClose(merit, bestMerit)) //
        .count();
  }
}
