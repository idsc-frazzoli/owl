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
  public final boolean add(final T element) {
    if (size() < capacity)
      return super.add(element);
    @SuppressWarnings("unchecked")
    Comparable<T> comparable = (Comparable<T>) element;
    if (isFavored(comparable, peek())) {
      poll();
      return super.add(element);
    }
    return false;
  }

  protected abstract boolean isFavored(Comparable<T> comparable, T peek);
}
