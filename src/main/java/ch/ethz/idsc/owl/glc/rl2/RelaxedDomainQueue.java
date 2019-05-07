// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedDomainQueue extends RelaxedGlobalQueue {
  LexicographicSemiorderMinTracker<GlcNode> domainMinTracker;

  /** @param glcNode
   * @param slacks Tensor of slack parameters
   * @return Relaxed lexicographic domain queue that contains given GlcNode as single element */
  public static RelaxedDomainQueue singleton(GlcNode glcNode, Tensor slacks) {
    RelaxedDomainQueue domainQueue = new RelaxedDomainQueue(slacks);
    domainQueue.add(glcNode);
    return domainQueue;
  }

  /** @param slacks Tensor of slack parameters
   * @return Empty queue of nodes **/
  public static RelaxedDomainQueue empty(Tensor slacks) {
    return new RelaxedDomainQueue(slacks);
  }

  // ---
  private RelaxedDomainQueue(Tensor slacks) {
    super(slacks);
    this.domainMinTracker = LexicographicSemiorderMinTracker.withList(slacks);
  }

  @Override // from RelaxedGlobalQueue
  public void add(GlcNode glcNode) {
    Collection<GlcNode> discardedNodes = domainMinTracker.digest(glcNode, glcNode.merit());
    if (!discardedNodes.contains(glcNode))
      openSet.add(glcNode);
    openSet.removeAll(discardedNodes);
  }

  @Override // from RelaxedGlobalQueue
  public final GlcNode poll() {
    GlcNode best = domainMinTracker.getBestKey();
    openSet.remove(best);
    // FIXME ANDRE remove from Mintracker
    return best;
  }

  @Override // from RelaxedGlobalQueue
  public final GlcNode peek() {
    return domainMinTracker.getBestKey();
  }
  // /* package */ Optional<Tensor> getMinValues() {
  // return StaticHelper.entrywiseMin(stream());
  // }
}
