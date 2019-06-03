// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;

public final class Order<T> implements OrderComparator<T>, Serializable {
  private final BinaryRelation<T> binaryRelation;

  /** @param binaryRelation reflexive and transitive
   * @return */
  public Order(BinaryRelation<T> binaryRelation) {
    this.binaryRelation = binaryRelation;
  }

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
}
