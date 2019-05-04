// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public enum ScalarTotalOrder {
  ;
  public static final OrderComparator<Scalar> INSTANCE = Order.comparator(Scalars::lessEquals);
}
