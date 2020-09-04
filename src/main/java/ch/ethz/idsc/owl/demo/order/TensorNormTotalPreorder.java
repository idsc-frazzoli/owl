// code by jph
package ch.ethz.idsc.owl.demo.order;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.order.BinaryRelation;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** Total preorder for tensor norms.
 * 
 * a mapping from any set to the reals results in a preorder
 * using Scalars.lessEquals as a binary relation
 * 
 * binary relation that is reflexive and transitive, but not antisymmetric */
public class TensorNormTotalPreorder implements BinaryRelation<Tensor>, Serializable {
  private final Norm norm;

  public TensorNormTotalPreorder(Norm norm) {
    this.norm = norm;
  }

  @Override // from BinaryRelation
  public boolean test(Tensor x, Tensor y) {
    return Scalars.lessEquals(norm.of(x), norm.of(y));
  }

  /** @return total preorder comparator that never returns INCOMPARABLE */
  public OrderComparator<Tensor> comparator() {
    return new Order<>(this);
  }
}
