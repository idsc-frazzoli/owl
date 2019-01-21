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
      public StrictPartialComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          throw new RuntimeException("binary relation is not irreflexive!");
        if (xRy)
          return StrictPartialComparison.LESS_THAN;
        if (yRx)
          return StrictPartialComparison.GREATER_THAN;
        return StrictPartialComparison.INCOMPARABLE;
      }
    };
  }
}
