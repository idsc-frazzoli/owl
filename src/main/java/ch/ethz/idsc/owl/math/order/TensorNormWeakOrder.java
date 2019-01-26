// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** TODO implement this abstractly:
 * a mapping from any set to the reals results in a preorder
 * using Scalars.lessEquals as a binary relation
 * 
 * binary relation that is reflexive and transitive, but not antisymmetric */
public class TensorNormWeakOrder implements BinaryRelation<Tensor> {
  private final Norm norm;

  public TensorNormWeakOrder(Norm norm) {
    this.norm = norm;
  }

  @Override // from BinaryRelation
  public boolean test(Tensor x, Tensor y) {
    return Scalars.lessEquals(norm.of(x), norm.of(y));
  }

  /** @return preorder comparator that never returns INCOMPARABLE */
  public WeakOrderComparator<Tensor> comparator() {
    return WeakOrder.comparator(this);
  }
}
