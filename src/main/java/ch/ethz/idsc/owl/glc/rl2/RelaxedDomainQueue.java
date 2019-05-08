// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

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

  private RelaxedDomainQueue(Tensor slacks) {
    super(slacks);
    this.domainMinTracker = LexicographicSemiorderMinTracker.withList(slacks);
  }

  @Override // from RelaxedGlobalQueue
  public void add(GlcNode glcNode) {
    Collection<GlcNode> discardedNodes = domainMinTracker.digest(glcNode, VectorScalars.vector(glcNode.merit()));
    if (!discardedNodes.contains(glcNode))
      openSet.add(glcNode);
    openSet.removeAll(discardedNodes);
  }

  @Override // from RelaxedGlobalQueue
  public final GlcNode poll() {
    // retrieves current best GlcNode and deletes it from the domainMinTracker
    GlcNode best = domainMinTracker.extractBestKey();
    openSet.remove(best);
    return best;
  }

  @Override // from RelaxedGlobalQueue
  public final GlcNode peek() {
    return domainMinTracker.getBestKey();
  }
  
  public static void main(String[] args) {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    RelaxedDomainQueue queue = RelaxedDomainQueue.singleton(node1, slacks);
    System.out.println(queue.openSet);
  }
}
