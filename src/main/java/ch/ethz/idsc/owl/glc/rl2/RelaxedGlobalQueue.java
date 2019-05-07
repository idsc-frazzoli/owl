// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.order.LexicographicSemiorderMinTracker;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedGlobalQueue implements Iterable<GlcNode> {
  // holds the node which have not yet been expanded
  protected final Set<GlcNode> openSet = new HashSet<>();
  private final Tensor slack;

  public RelaxedGlobalQueue(Tensor slack) {
    this.slack = slack;
  }

  public void add(GlcNode glcNode) {
    openSet.add(glcNode);
  }

  public GlcNode poll() {
    GlcNode best = getBest();
    openSet.remove(best);
    return best;
  }

  public GlcNode peek() {
    return getBest();
  }

  public final boolean removeAll(Collection<GlcNode> collection) {
    return openSet.removeAll(collection);
  }

  public final boolean isEmpty() {
    return openSet.isEmpty();
  }

  public final Stream<GlcNode> stream() {
    return openSet.stream();
  }

  public final Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(openSet);
  }

  public final int size() {
    return openSet.size();
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return openSet.iterator();
  }

  private GlcNode getBest() {
    LexicographicSemiorderMinTracker<GlcNode> minTracker = LexicographicSemiorderMinTracker.withList(slack);
    Iterator<GlcNode> iterator = openSet.iterator();
    while (iterator.hasNext()) {
      GlcNode currentGlcNode = iterator.next();
      minTracker.digest(currentGlcNode, currentGlcNode.merit());
    }
    return minTracker.getBestKey();
  }
}
