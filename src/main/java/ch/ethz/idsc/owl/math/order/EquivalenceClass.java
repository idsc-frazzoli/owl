// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Interface for generating equivalence classes for a given equivalence relation.
 * 
 * @author Andre
 *
 * @param <T> A set on which an equivalence relation is applied and equivalence classes shall be generated */
public interface EquivalenceClass<T> {
  T generateEquivalenceClass(T a);

  PartialComparison condensedOrdering(T a);
}