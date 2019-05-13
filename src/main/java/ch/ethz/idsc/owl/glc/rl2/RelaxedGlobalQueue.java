// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedGlobalQueue extends RelaxedPriorityQueue {
  /** holds the node which have not yet been expanded */
  public RelaxedGlobalQueue(Tensor slacks) {
    super(slacks);
  }

  /** Adds single node to global queue. */
  @Override // from RelaxedPriorityQueue
  public Collection<GlcNode> add(GlcNode glcNode) {
    addSingle(glcNode);
    return Collections.emptyList();
  }

  @Override // from RelaxedPriorityQueue
  public GlcNode peekBest() {
    LexicographicSemiorderMinTracker<GlcNode> minTracker = LexicographicSemiorderMinTracker.withList(slacks);
    Iterator<GlcNode> iterator = iterator();
    while (iterator.hasNext()) {
      GlcNode currentGlcNode = iterator.next();
      minTracker.digest(currentGlcNode, VectorScalars.vector(currentGlcNode.merit()));
    }
    return minTracker.peekBestKey();
  }

  @Override // from RelaxedPriorityQueue
  protected GlcNode pollBest() {
    GlcNode glcNode = peekBest();
    remove(glcNode);
    return glcNode;
  }
}
