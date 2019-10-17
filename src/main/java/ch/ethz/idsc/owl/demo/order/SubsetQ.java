// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.util.Collection;

import ch.ethz.idsc.owl.math.order.BinaryRelation;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SubsetQ.html">SubsetQ</a> */
public enum SubsetQ implements BinaryRelation<Collection<?>> {
  RELATION;
  // ---
  @Override // from BinaryRelation
  public boolean test(Collection<?> subset, Collection<?> set) {
    return of(set, subset);
  }

  /** Remark:
   * API conforms with Mathematica
   * 
   * @param set
   * @param subset
   * @return whether subset is contained in set */
  public static boolean of(Collection<?> set, Collection<?> subset) {
    return set.containsAll(subset);
  }
}
