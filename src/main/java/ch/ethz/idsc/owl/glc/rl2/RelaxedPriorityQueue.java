// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.ethz.idsc.owl.glc.core.GlcNode;

/** all implemented methods are final */
public abstract class RelaxedPriorityQueue implements Iterable<GlcNode>, Serializable {
  /** holds the node which have not yet been expanded */
  private final Set<GlcNode> openSet = new HashSet<>();

  /** @param glcNode */
  public abstract Collection<GlcNode> add(GlcNode glcNode);

  /** Polls the GlcNode with current best merit from the queue.
   * 
   * @return GlcNode with currently best merit
   * @throws Exception if this queue is empty */
  protected abstract GlcNode pollBest();

  /** @return GlcNode with current best merit without polling it from the queue, or
   * null if this queue is empty */
  protected abstract GlcNode peekBest();

  /** Adds a single node to the queue.
   * 
   * @param glcNode to be added to the queue
   * @return whether given glcNode was added to the queue */
  protected final boolean addSingle(GlcNode glcNode) {
    return openSet.add(glcNode);
  }

  /** removes the GlcNode from the queue, if it exists.
   * 
   * @param glcNode
   * @return whether glcNode was removed */
  public final boolean remove(GlcNode glcNode) {
    return openSet.remove(glcNode);
  }

  /** Any glcNode contained in the collection will be removed from the queue if it is element of the queue.
   * 
   * @param collection of GlcNodes
   * @return True if the queue has been changed. */
  public final boolean removeAll(Collection<GlcNode> collection) {
    return openSet.removeAll(collection);
  }

  /** Gives an unmodifiable view of the current nodes in the queue
   * 
   * @return unmodifiable set of GlcNodes */
  public final Set<GlcNode> collection() {
    return Collections.unmodifiableSet(openSet);
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return openSet.iterator();
  }
}
