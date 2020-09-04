// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.io.Serializable;
import java.util.function.BiPredicate;

import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public enum ScalarTotalOrder {
  ;
  @SuppressWarnings("unchecked")
  public static final OrderComparator<Scalar> INSTANCE = new Order<>( //
      (BiPredicate<Scalar, Scalar> & Serializable) //
      Scalars::lessEquals);
}
