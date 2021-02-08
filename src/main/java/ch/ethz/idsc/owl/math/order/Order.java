// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.function.BiPredicate;

/** See Chapter 2.2 in "Multi-Objective Optimization Using Preference Structures" */
public final class Order<T> implements OrderComparator<T>, Serializable {
  private final BiPredicate<T, T> binaryRelation;

  /** @param binaryRelation reflexive and transitive
   * @return */
  public Order(BiPredicate<T, T> binaryRelation) {
    this.binaryRelation = binaryRelation;
  }

  @Override // from OrderComparator
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
}
