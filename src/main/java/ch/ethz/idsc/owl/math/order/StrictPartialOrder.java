// code by astoll
package ch.ethz.idsc.owl.math.order;

/** a strict partial order is derived from a binary relation that is
 * 
 * irreflexive: for all x then not xRx
 * asymmetric: for all x and y in X, if xRy then not yRx
 * transitive: for all x, y and z in X it holds that if xRy and yRz then xRz
 * 
 * source: https://en.wikipedia.org/wiki/Binary_relation */
public enum StrictPartialOrder {
  ;
  public static <T> StrictPartialComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new StrictPartialComparator<T>() {
      @Override // from StrictPartialComparator
      public StrictPartialComparison compare(T a, T b) {
        boolean a_b = binaryRelation.test(a, b);
        boolean b_a = binaryRelation.test(b, a);
        if (a_b)
          return StrictPartialComparison.LESS_THAN;
        if (b_a)
          return StrictPartialComparison.GREATER_THAN;
        return StrictPartialComparison.INCOMPARABLE;
      }
    };
  }
}
