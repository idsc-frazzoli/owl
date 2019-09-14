// code by Eric Simonton
// adapted by jph and clruch
package ch.ethz.idsc.owl.data.nd;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class NdCluster<V> implements Serializable {
  private final NdCenterInterface ndCenter;
  /* package */ final Tensor center;
  private final int limit;
  private final Queue<NdEntry<V>> queue;
  private int considered = 0;

  /** @param collection
   * @param ndCenter
   * @param limit positive */
  /* package */ NdCluster(Collection<NdPair<V>> collection, NdCenterInterface ndCenter, int limit) {
    this.ndCenter = ndCenter;
    this.center = ndCenter.center();
    this.limit = limit;
    queue = collection.stream() //
        .map(ndPair -> new NdEntry<>(ndPair, ndCenter.ofVector(ndPair.location))) //
        .sorted(NdEntryComparators.INCREASING) //
        .limit(limit) //
        .collect(Collectors.toCollection(LinkedList::new));
    considered = collection.size();
  }

  /** @param ndCenter
   * @param limit positive */
  /* package */ NdCluster(NdCenterInterface ndCenter, int limit) {
    this.ndCenter = ndCenter;
    this.center = ndCenter.center();
    this.limit = limit;
    queue = new PriorityQueue<>(NdEntryComparators.DECREASING);
  }

  /* package */ void consider(NdPair<V> ndPair) {
    ++considered;
    NdEntry<V> ndEntry = new NdEntry<>(ndPair, ndCenter.ofVector(ndPair.location));
    if (queue.size() < limit)
      queue.add(ndEntry);
    else //
    if (Scalars.lessThan(ndEntry.distance(), queue.peek().distance())) {
      queue.poll();
      queue.add(ndEntry);
    }
  }

  /* package */ boolean isViable(NdBounds ndBounds) {
    if (queue.size() < limit)
      return true;
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenter.ofVector(test), queue.peek().distance());
  }

  /** @return number of points visited in order to build the cluster */
  public int considered() {
    return considered;
  }

  public Stream<NdEntry<V>> stream() {
    return queue.stream();
  }

  public Collection<NdEntry<V>> collection() {
    return Collections.unmodifiableCollection(queue);
  }

  public int size() {
    return queue.size();
  }
}