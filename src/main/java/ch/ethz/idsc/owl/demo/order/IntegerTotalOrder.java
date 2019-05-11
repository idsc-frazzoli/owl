// code by astoll, jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;

public enum IntegerTotalOrder {
  ;
  public static final OrderComparator<Integer> INSTANCE = new Order<>((x, y) -> x <= y);
}
