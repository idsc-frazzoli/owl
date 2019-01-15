package ch.ethz.idsc.owl.math.order;

import java.util.Optional;

public interface PartialOrdering<T> {
  /** Compares to elements of a proset.
   * For two elements a & b in a proset one of the following holds:
   * a = b,
   * a < b,
   * a > b, or
   * a & b are incomparable.
   * @param o1
   * @param o2
   * @return */
  Optional<Integer> compare(T o1, T o2);
}
