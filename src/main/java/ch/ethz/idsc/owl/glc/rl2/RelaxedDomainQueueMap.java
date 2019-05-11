// code by astoll, ynager
package ch.ethz.idsc.owl.glc.rl2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RelaxedDomainQueueMap implements Serializable {
  /** map from domain keys to queues of nodes */
  private final Map<Tensor, RelaxedPriorityQueue> map = new HashMap<>();
  private final Tensor slacks;

  public RelaxedDomainQueueMap(Tensor slacks) {
    this.slacks = slacks;
  }

  /** add given glcNode to domain queue at the location determined by domain_key.
   * a new domain queue is allocated if no other nodes were inserted there prior.
   * 
   * @param domain_key
   * @param glcNode */
  public Collection<GlcNode> addToDomainMap(Tensor domain_key, GlcNode glcNode) {
    if (containsKey(domain_key)) // has another node has already reached this domain ?
      return getQueue(domain_key).add(glcNode); // add node to existing relaxedDomainQueue
    map.put(domain_key, RelaxedDomainQueue.singleton(glcNode, slacks)); // create a new domain queue with single entry
    return Collections.emptyList();
  }

  public void removeFromDomainMap(Tensor domain_key, GlcNode glcNode) {
    if (!containsKey(domain_key)) {
      throw new RuntimeException("Key does not exists in map!");
    }
    getQueue(domain_key).remove(glcNode);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Tensor domain_key) {
    return map.containsKey(domain_key);
  }

  public RelaxedPriorityQueue getQueue(Tensor domain_key) {
    return map.get(domain_key);
  }

  public Stream<Entry<Tensor, RelaxedPriorityQueue>> mapEntrySetStream() {
    return map.entrySet().stream();
  }

  public Map<Tensor, RelaxedPriorityQueue> getMap() {
    return Collections.unmodifiableMap(map);
  }
}
