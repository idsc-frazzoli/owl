// code by jph
package ch.ethz.idsc.owl.data;

import java.util.Queue;

/** bounded max queue only keeps the n largest elements */
public class BoundedMaxQueue<T> extends BoundedPriorityQueue<T> {
  /** @param capacity
   * @return bounded priority queue with given maximum capacity */
  public static <T> Queue<T> of(int capacity) {
    return new BoundedMaxQueue<>(capacity);
  }

  // ---
  private BoundedMaxQueue(int capacity) {
    super(capacity, null);
  }

  @Override // from BoundedPriorityQueue
  protected boolean isFavored(Comparable<T> comparable, T peek) {
    return 0 < comparable.compareTo(peek);
  }
}