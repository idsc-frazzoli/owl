// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.PriorityQueue;

/** priority queue with ordering defined by {@link NodeMeritComparator} */
public class DomainQueue extends PriorityQueue<GlcNode> {
  /** @param glcNode
   * @return domain queue that contains given glcNode as single element */
  public static DomainQueue singleton(GlcNode glcNode) {
    DomainQueue domainQueue = new DomainQueue();
    domainQueue.add(glcNode);
    return domainQueue;
  }

  /***************************************************/
  private DomainQueue() {
    super(NodeMeritComparator.INSTANCE);
  }
}
