// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Optional;

public interface PartialOrdering<T> {
  /** Compares to elements of a proset.
   * For two elements a & b in a proset one of the following holds:
   * <p><tt>a = b</tt>,
   * <tt>a < b</tt>,
   * a > b, or
   * a & b are incomparable.
   * @param o1
   * @param o2
   * @return Optional[0] if a = b, Optional[-1] if a < b, Optional[1] if a > b or Optional.empty() if incomparable */
  Optional<Integer> compare(T o1, T o2);
}
