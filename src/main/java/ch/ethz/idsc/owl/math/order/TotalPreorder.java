// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum TotalPreorder {
  ;
  public static <T> TotalPreorderComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new TotalPreorderComparator<T>() {
      @Override
      public TotalPreorderComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          return TotalPreorderComparison.INDIFFERENT;
        if (xRy)
          return TotalPreorderComparison.LESS_EQUALS_ONLY;
        if (yRx)
          return TotalPreorderComparison.GREATER_EQUALS_ONLY;
        throw new RuntimeException();
      }
    };
  }
}
