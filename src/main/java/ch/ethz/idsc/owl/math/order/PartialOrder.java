// code by jph
package ch.ethz.idsc.owl.math.order;

/** a partial order is derived from a binary relation that is
 * 
 * reflexive: for all x then xRx
 * antisymmetric: for all x and y in X, if xRy and yRx then x = y
 * transitive: for all x, y and z in X it holds that if xRy and yRz then xRz
 * 
 * source: https://en.wikipedia.org/wiki/Binary_relation */
public enum PartialOrder {
  ;
  public static <T> PartialComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new PartialComparator<T>() {
      @Override // from PartialComparator
      public PartialComparison compare(T a, T b) {
        boolean a_b = binaryRelation.test(a, b);
        boolean b_a = binaryRelation.test(b, a);
        if (a_b && b_a)
          return PartialComparison.EQUALS;
        if (a_b)
          return PartialComparison.LESS_THAN;
        if (b_a)
          return PartialComparison.GREATER_THAN;
        return PartialComparison.INCOMPARABLE;
      }
    };
  }
}
