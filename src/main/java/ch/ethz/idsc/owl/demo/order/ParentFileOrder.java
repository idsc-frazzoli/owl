// code by jph 
package ch.ethz.idsc.owl.demo.order;

import java.io.File;

import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;

// TODO not sure if partial order, or preorder
public enum ParentFileOrder {
  ;
  public static final OrderComparator<File> INSTANCE = new Order<>(ParentFileRelation.INSTANCE);
}
