// code by jph
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.math.order.BinaryRelation;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** Total preorder for tensor norms. */
public class TensorNormTotalPreorder implements BinaryRelation<Tensor> {
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
