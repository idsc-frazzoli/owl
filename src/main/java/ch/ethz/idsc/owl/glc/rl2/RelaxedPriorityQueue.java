// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

public abstract class RelaxedPriorityQueue implements Iterable<GlcNode> {
  /** holds the node which have not yet been expanded */
  private final Set<GlcNode> openSet = new HashSet<>();
  protected final Tensor slacks;

  protected RelaxedPriorityQueue(Tensor slacks) {
    this.slacks = slacks;
  }

  /** @param glcNode */
  public abstract Collection<GlcNode> add(GlcNode glcNode);

  /** @return */
  protected abstract GlcNode pollBest();

  /** @return */
  protected abstract GlcNode peekBest();

  protected final void addSingle(GlcNode glcNode) {
    openSet.add(glcNode);
  }

  public final boolean remove(GlcNode glcNode) {
    return openSet.remove(glcNode);
    // TODO syserr if not exists
  }

  public final boolean removeAll(Collection<GlcNode> collection) {
    return openSet.removeAll(collection);
    // TODO syserr if not exists
  }

  public final Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(openSet);
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return openSet.iterator();
  }
}
