// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RLDomainQueueMap {
  /** map from domain keys to queues of nodes */
  private final Map<Tensor, RLDomainQueue> map = new HashMap<>();
  private final Tensor slacks;

  public RLDomainQueueMap(Tensor slacks) {
    this.slacks = slacks;
  }

  /** add given glcNode to domain queue at the location determined by domain_key.
   * a new domain queue is allocated if no other nodes were inserted there prior.
   * 
   * @param domain_key
   * @param glcNode */
  public void addToDomainMap(Tensor domain_key, GlcNode glcNode) {
    if (map.containsKey(domain_key)) // has another node has already reached this domain ?
      map.get(domain_key).add(glcNode); // add node to existing queue
    else
      map.put(domain_key, RLDomainQueue.singleton(glcNode, slacks)); // create a new queue with single entry
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Tensor domain_key) {
    return map.containsKey(domain_key);
  }

  public RLDomainQueue getQueue(Tensor domain_key) {
    return map.get(domain_key);
  }

  public Stream<Entry<Tensor, RLDomainQueue>> mapEntrySetStream() {
    return map.entrySet().stream();
  }
}
