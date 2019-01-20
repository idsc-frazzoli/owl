// code by jph
package ch.ethz.idsc.owl.math.order;

import java.util.function.BiPredicate;

/** a binary relation has a single function test(a, b) that returns whether a and b satisfy the relation
 * 
 * @param <T> */
@FunctionalInterface
public interface BinaryRelation<T> extends BiPredicate<T, T> {
  // ---
}
