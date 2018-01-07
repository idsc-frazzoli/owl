// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.PriorityQueue;

public class DomainQueue extends PriorityQueue<GlcNode> {
  public static DomainQueue singleton(GlcNode glcNode) {
    DomainQueue domainQueue = new DomainQueue();
    domainQueue.add(glcNode);
    return domainQueue;
  }

  // ---
  private DomainQueue() {
    super(NodeMeritComparator.INSTANCE);
  }
}
