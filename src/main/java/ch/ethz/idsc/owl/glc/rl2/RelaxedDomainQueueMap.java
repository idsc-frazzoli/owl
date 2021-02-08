// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

public final class RelaxedDomainQueueMap implements Serializable {
  /** map from domain keys to queues of nodes */
  private final Map<Tensor, RelaxedPriorityQueue> map = new HashMap<>();
  private final Tensor slacks;

  public RelaxedDomainQueueMap(Tensor slacks) {
    this.slacks = slacks;
  }

  /** Add given glcNode to domain queue at the location determined by domainKey.
   * A new domain queue is allocated if no other nodes were inserted there prior.
   * 
   * @param domainKey
   * @param glcNode
   * @return Collection of discarded nodes */
  public Collection<GlcNode> addToDomainMap(Tensor domainKey, GlcNode glcNode) {
    RelaxedPriorityQueue relaxedPriorityQueue = getQueue(domainKey);
    if (Objects.nonNull(relaxedPriorityQueue)) // has another node has already reached this domain ?
      return relaxedPriorityQueue.add(glcNode); // potentially add node to existing relaxedDomainQueue
    map.put(domainKey, RelaxedDomainQueue.singleton(glcNode, slacks)); // create a new domain queue with single entry
    return Collections.emptyList();
  }

  /** Removes given glcNode from domain queue at the location determined by domainKey.
   * @throws RunTimeException if domain queue does not exist at the location determined by domainKey
   * @param domainKey
   * @param glcNode
   * @return Collection of discarded nodes
   * @throws NullPointerException if domainKey is not associated to any queue */
  public boolean removeFromDomainMap(Tensor domainKey, GlcNode glcNode) {
    RelaxedPriorityQueue relaxedPriorityQueue = getQueue(domainKey);
    return relaxedPriorityQueue.remove(glcNode);
  }

  /** @return True if map is empty */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /** @return domain queue at the location determined by domainKey,
   * or null if given domain key is not associated to a queue */
  public RelaxedPriorityQueue getQueue(Tensor domainKey) {
    return map.get(domainKey);
  }

  /** @return Unmodifiable view of domain map */
  public Map<Tensor, RelaxedPriorityQueue> getMap() {
    return Collections.unmodifiableMap(map);
  }
}
