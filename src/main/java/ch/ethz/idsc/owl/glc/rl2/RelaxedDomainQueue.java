// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedDomainQueue extends RelaxedPriorityQueue {
  /** @param glcNode
   * @param slacks Tensor of slack parameters
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

  // ---
  private final LexicographicSemiorderMinTracker<GlcNode> domainMinTracker;

  private RelaxedDomainQueue(Tensor slacks) {
    super(slacks);
    this.domainMinTracker = LexicographicSemiorderMinTracker.withList(slacks);
  }

  @Override // from RelaxedPriorityQueue
  public Collection<GlcNode> add(GlcNode glcNode) {
    Collection<GlcNode> discardedNodes = domainMinTracker.digest(glcNode, VectorScalars.vector(glcNode.merit()));
    if (!discardedNodes.contains(glcNode))
      addSingle(glcNode);
    removeAll(discardedNodes);
    return discardedNodes;
  }

  @Override // from RelaxedPriorityQueue
  protected GlcNode pollBest() {
    GlcNode glcNode = domainMinTracker.pollBestKey();
    remove(glcNode);
    return glcNode;
  }

  @Override // from RelaxedPriorityQueue
  public final GlcNode peekBest() {
    return domainMinTracker.peekBestKey();
  }

  // TODO function is not used
  /* package */ Optional<Tensor> getMinValues() {
    return StaticHelper.entrywiseMin(collection().stream());
  }
}
