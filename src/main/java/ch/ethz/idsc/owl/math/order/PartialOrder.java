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
      public PartialComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          return PartialComparison.EQUALS;
        if (xRy)
          return PartialComparison.LESS_THAN;
        if (yRx)
          return PartialComparison.GREATER_THAN;
        return PartialComparison.INCOMPARABLE;
      }
    };
  }
}
