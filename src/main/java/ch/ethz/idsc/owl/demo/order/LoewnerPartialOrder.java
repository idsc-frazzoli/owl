// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.order.BinaryRelation;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.PositiveSemidefiniteMatrixQ;

/** Reference:
 * Hauke, Markiewicz, 1994 */
public enum LoewnerPartialOrder {
  ;
  public static final OrderComparator<Tensor> INSTANCE = new Order<>( //
      (BinaryRelation<Tensor> & Serializable) //
      (x, y) -> PositiveSemidefiniteMatrixQ.ofHermitian(x.subtract(y)));
}
