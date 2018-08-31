// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Optional;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

/** priority queue with ordering defined by {@link NodeMeritComparator} */
/* package */ class RLDomainQueue extends RLQueue {
  /** @param glcNode
   * @param slacks
   * @return relaxed lexicographic domain queue that contains given glcNode as single element */
  public static RLDomainQueue singleton(GlcNode glcNode, Tensor slacks) {
    RLDomainQueue domainQueue = new RLDomainQueue(slacks);
    domainQueue.add(glcNode);
    return domainQueue;
  }

  /** @param slacks
   * @return */
  public static RLDomainQueue empty(Tensor slacks) {
    return new RLDomainQueue(slacks);
  }

  // ---
  private RLDomainQueue(Tensor slacks) {
    super(slacks);
  }

  /* package */ Optional<Tensor> getMinValues() {
    return StaticHelper.entrywiseMin(stream());
  }
}
