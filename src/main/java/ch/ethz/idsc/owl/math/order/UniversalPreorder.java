// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum UniversalPreorder {
  ;
  /** @param binaryRelation reflexive and transitive
   * @return */
  public static <T> UniversalComparator<T> comparator(BinaryRelation<T> binaryRelation) {
    return new UniversalComparator<T>() {
      @Override
      public UniversalComparison compare(T x, T y) {
        boolean xRy = binaryRelation.test(x, y);
        boolean yRx = binaryRelation.test(y, x);
        if (xRy && yRx)
          return UniversalComparison.INDIFFERENT;
        if (xRy)
          return UniversalComparison.STRICTLY_PRECEDES;
        if (yRx)
          return UniversalComparison.STRICTLY_SUCCEDES;
        return UniversalComparison.INCOMPARABLE;
      }
    };
  }
}
