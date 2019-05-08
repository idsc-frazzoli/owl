// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedGlobalQueue implements Iterable<GlcNode> {
  /** holds the node which have not yet been expanded */
  private final Set<GlcNode> openSet = new HashSet<>();
  private final Tensor slacks;

  public RelaxedGlobalQueue(Tensor slacks) {
    this.slacks = slacks;
  }

  public void add(GlcNode glcNode) {
    openSet.add(glcNode);
  }

  public final GlcNode poll() {
    GlcNode best = getBest();
    openSet.remove(best);
    return best;
  }

  public GlcNode peekBest() {
    return getBest();
  }

  public final boolean remove(GlcNode glcNode) {
    return openSet.remove(glcNode);
  }

  public final boolean removeAll(Collection<GlcNode> collection) {
    return openSet.removeAll(collection);
  }

  public final Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(openSet);
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return openSet.iterator();
  }

  protected GlcNode getBest() {
    LexicographicSemiorderMinTracker<GlcNode> minTracker = LexicographicSemiorderMinTracker.withList(slacks);
    Iterator<GlcNode> iterator = openSet.iterator();
    while (iterator.hasNext()) {
      GlcNode currentGlcNode = iterator.next();
      minTracker.digest(currentGlcNode, VectorScalars.vector(currentGlcNode.merit()));
    }
    return minTracker.peekBestKey();
  }
}
