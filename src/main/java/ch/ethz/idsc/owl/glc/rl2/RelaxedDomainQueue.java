// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.EboTracker;
import ch.ethz.idsc.owl.math.order.SingleEboTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedDomainQueue extends RelaxedPollingQueue {
  /** @param glcNode
   * @param slacks vector of slack parameters
   * @return relaxed lexicographic domain queue that contains given GlcNode as single element */
  public static RelaxedPriorityQueue singleton(GlcNode glcNode, Tensor slacks) {
    RelaxedPriorityQueue relaxedPriorityQueue = new RelaxedDomainQueue(slacks);
    relaxedPriorityQueue.add(glcNode);
    return relaxedPriorityQueue;
  }

  /** @param slacks Tensor of slack parameters
   * @return empty queue of nodes */
  public static RelaxedPriorityQueue empty(Tensor slacks) {
    return new RelaxedDomainQueue(slacks);
  }

  /***************************************************/
  private final EboTracker<GlcNode> eboTracker;

  private RelaxedDomainQueue(Tensor slacks) {
    this.eboTracker = SingleEboTracker.withSet(slacks);
  }

  /** Checks whether glcNode's merit precedes or is equally good than any other. If yes, it will be added to the domain map and all
   * nodes whose merits is preceded by the glcNode's merit will be discarded.
   * @param glcNode node which is potentially add
   * @return empty nodes which were discarded due to insertion of glcNode */
  @Override // from RelaxedPriorityQueue
  public Collection<GlcNode> add(GlcNode glcNode) {
    if (StaticHelper.isSimilar(glcNode, this))
      return Collections.singleton(glcNode);
    // ---
    Collection<GlcNode> discardedNodes = eboTracker.digest(glcNode, VectorScalars.vector(glcNode.merit()));
    if (!discardedNodes.contains(glcNode))
      addSingle(glcNode);
    removeAll(discardedNodes);
    return discardedNodes;
  }

  @Override // from RelaxedPriorityQueue
  public final GlcNode peekBest() {
    return eboTracker.peekBestKey();
  }

  @Override // from RelaxedBaseQueue
  protected GlcNode getNodeToPoll() {
    return eboTracker.pollBestKey();
  }
}
