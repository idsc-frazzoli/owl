// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator}
 * 
 * Creates an enumeration for a equivalence relation.
 * <p>By definition, a binary relation is called an equivalence relation if it is transitive, reflexive and symmetric.
 * <p>Two elements in set are either equivalent or not. */
@FunctionalInterface
public interface EquivalenceComparator<T> {
  /** Compares to elements in a set for equivalence.
   * 
   * For two elements a and b in a set one of the following holds:
   * <p>a & b are equivalent or they are not.
   * @param a left hand side of preordered comparison
   * @param b right hand side of preordered comparison
   * @return true if a and b are equivalent, else false */
  boolean equivalent(T a, T b);
}
