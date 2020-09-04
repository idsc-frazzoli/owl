// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.order.BinaryRelation;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** Preorder for non-zero scalars in exact precision */
public enum DivisibilityPreorder {
  ;
  public static final OrderComparator<Scalar> INSTANCE = new Order<>( //
      (BinaryRelation<Scalar> & Serializable) //
      Scalars::divides);
}
