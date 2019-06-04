// code by jph
package ch.ethz.idsc.owl.math.order;

public enum InverseOrderComparison {
  ;
  private static final OrderComparison[] LOOKUP = { //
      OrderComparison.STRICTLY_SUCCEEDS, //
      OrderComparison.INDIFFERENT, //
      OrderComparison.STRICTLY_PRECEDES, //
      OrderComparison.INCOMPARABLE };

  /** @param orderComparison
   * @return toggles STRICTLY_SUCCEEDS <-> STRICTLY_PRECEDES, and leaves invariant INDIFFERENT, and INCOMPARABLE */
  public static OrderComparison of(OrderComparison orderComparison) {
    return LOOKUP[orderComparison.ordinal()];
  }
}
