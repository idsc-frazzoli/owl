// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

/** agents moves with unit speed, i.e.
 * Euclidean length of line segment equals the time required to traverse */
/* package */ class RnTransition extends AbstractTransition {
  private final Scalar length;

  public RnTransition(Tensor start, Tensor end) {
    super(start, end);
    length = Norm._2.between(start(), end());
  }

  @Override // from Transition
  public Scalar length() {
    return length;
  }

  @Override // from Transition
  public Tensor sampled(Scalar ofs, Scalar dt) {
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    Tensor tensor = Tensors.empty();
    while (Scalars.lessThan(ofs, length)) {
      Tensor x = start().multiply(length.subtract(ofs).divide(length)) //
          .add(end().multiply(ofs.divide(length)));
      tensor.append(x);
      ofs = ofs.add(dt);
    }
    return tensor;
  }

  @Override // from Transition
  public Tensor splitAt(Scalar t1) {
    // not yet implemented, use RnGeodesic
    throw new RuntimeException();
  }
}
