// code by jph, astoll
package ch.ethz.idsc.owl.math.order;

import java.util.function.BiPredicate;

/** A binary relation has a single function test(x, y)
 * that returns whether x and y satisfy the relation.
 * 
 * <p>The input parameters x and y are of the same type.
 * Therefore BinaryRelation is a special example of a
 * {@link BiPredicate}.
 * 
 * @param <T>
 * @return true or false */
@FunctionalInterface
public interface BinaryRelation<T> extends BiPredicate<T, T> {
  // ---
}
