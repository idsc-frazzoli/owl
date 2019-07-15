// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class DirectedTransition extends AbstractTransition {
  public final boolean isForward;

  public DirectedTransition(Tensor start, Tensor end, Scalar length, boolean isForward) {
    super(start, end, length);
    this.isForward = isForward;
  }
}
