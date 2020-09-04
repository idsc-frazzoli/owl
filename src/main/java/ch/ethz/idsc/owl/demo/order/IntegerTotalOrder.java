// code by astoll, jph
package ch.ethz.idsc.owl.demo.order;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.order.BinaryRelation;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;

public enum IntegerTotalOrder {
  ;
  public static final OrderComparator<Integer> INSTANCE = new Order<>( //
      (BinaryRelation<Integer> & Serializable) //
      (x, y) -> x <= y);
}
