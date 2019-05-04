// code by jph
// adapted from https://github.com/tdebatty/java-graphs/blob/master/src/main/java/info/debatty/java/util/BoundedPriorityQueue.java
package ch.ethz.idsc.owl.data;

import java.util.Comparator;
import java.util.PriorityQueue;

/* package */ abstract class BoundedPriorityQueue<T> extends PriorityQueue<T> {
  private final int capacity;

  BoundedPriorityQueue(int capacity, Comparator<? super T> comparator) {
    super(capacity, comparator);
    this.capacity = capacity;
  }

  /** when the queue is full, adds the element if it is larger than the
   * smallest element already in the queue.
   *
   * @param element
   * @return true if element was added
   * @throws Exception */
  @Override // from PriorityQueue
  public final boolean offer(final T element) {
    if (size() < capacity)
      return super.offer(element);
    @SuppressWarnings("unchecked")
    Comparable<T> comparable = (Comparable<T>) element;
    if (isFavored(comparable, peek())) {
      poll();
      return super.offer(element);
    }
    return false;
  }

  /** @param comparable candidate
   * @param peek from queue
   * @return true if comparable candidate strictly precedes than peek */
  protected abstract boolean isFavored(Comparable<T> comparable, T peek);
}
