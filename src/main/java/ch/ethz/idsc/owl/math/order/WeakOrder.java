// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum WeakOrder {
  ;
  public static <T> WeakOrderComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new WeakOrderComparator<T>() {
      @Override
      public WeakOrderComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy) {
          if (yRx) {
            return WeakOrderComparison.INDIFFERENT;
          }
          return WeakOrderComparison.LESS_EQUALS_ONLY;
        }
        return WeakOrderComparison.GREATER_EQUALS_ONLY;
      }
    };
  }
}
