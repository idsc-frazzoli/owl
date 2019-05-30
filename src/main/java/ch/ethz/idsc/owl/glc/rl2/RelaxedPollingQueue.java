// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

/** class implements method {@link #pollBest()} */
/* package */ abstract class RelaxedPollingQueue extends RelaxedPriorityQueue {
  /** @param slacks */
  public RelaxedPollingQueue(Tensor slacks) {
    super(slacks);
  }

  @Override // from RelaxedPriorityQueue
  protected final GlcNode pollBest() {
    GlcNode glcNode = getNodeToPoll();
    boolean removed = remove(glcNode);
    if (!removed)
      throw new RuntimeException("node was not removed from queue");
    return glcNode;
  }

  /** @return node that will be removed from this collection */
  protected abstract GlcNode getNodeToPoll();
}
