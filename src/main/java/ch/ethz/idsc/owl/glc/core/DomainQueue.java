// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.PriorityQueue;

public class DomainQueue extends PriorityQueue<GlcNode> {
  public DomainQueue() {
    super(NodeMeritComparator.INSTANCE);
  }
}
