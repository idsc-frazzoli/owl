// code by jph, astoll
package ch.ethz.idsc.owl.math.order;

public enum Preorder {
  ;
  /** @param binaryRelation reflexive and transitive
   * @return */
  public static <T> PreorderComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new PreorderComparator<T>() {
      @Override
      public PreorderComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          return PreorderComparison.LESS_AND_GREATER_EQUALS;
        if (xRy)
          return PreorderComparison.ONLY_LESS_EQUALS;
        if (yRx)
          return PreorderComparison.ONLY_GREATER_EQUALS;
        return PreorderComparison.INCOMPARABLE;
      }
    };
  }
}
