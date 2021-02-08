// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import ch.ethz.idsc.owl.glc.core.GlcNode;

/** all implemented methods are final */
public abstract class RelaxedPriorityQueue implements Iterable<GlcNode>, Serializable {
  /** holds the node which have not yet been expanded */
  private final Collection<GlcNode> collection = new HashSet<>();

  /** @param glcNode
   * @return collection of nodes that were removed from this queue subsequent to the addition of given glcNode */
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
    return collection.add(glcNode);
  }

  /** removes the GlcNode from the queue, if it exists.
   * 
   * @param glcNode
   * @return whether glcNode was removed */
  public final boolean remove(GlcNode glcNode) {
    return collection.remove(glcNode);
  }

  /** Any glcNode contained in the collection will be removed from the queue if it is element of the queue.
   * 
   * @param collection of GlcNodes
   * @return True if the queue has been changed. */
  public final boolean removeAll(Collection<GlcNode> collection) {
    return this.collection.removeAll(collection);
  }

  /** Gives an unmodifiable view of the current nodes in the queue
   * 
   * @return unmodifiable set of GlcNodes */
  public final Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(collection);
  }

  public final int size() {
    return collection.size();
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return collection.iterator();
  }
}
