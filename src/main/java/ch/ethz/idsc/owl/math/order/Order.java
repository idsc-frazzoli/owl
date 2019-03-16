// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum Order {
  ;
  /** @param binaryRelation reflexive and transitive
   * @return */
  public static <T> OrderComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new OrderComparator<T>() {
      @Override
      public OrderComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          return OrderComparison.INDIFFERENT;
        if (xRy)
          return OrderComparison.STRICTLY_PRECEDES;
        if (yRx)
          return OrderComparison.STRICTLY_SUCCEEDS;
        return OrderComparison.INCOMPARABLE;
      }
    };
  }
}
