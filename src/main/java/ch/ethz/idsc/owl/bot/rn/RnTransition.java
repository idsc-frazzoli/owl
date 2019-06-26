// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

/** Transition as straight line connecting given start and end point in Euclidean space.
 * The length of the transition is Euclidean distance between given start and end point. */
// TODO GJOEL/JPH remove class if not needed anymore
// @Deprecated
/* package */ class RnTransition extends AbstractTransition {
  public RnTransition(Tensor start, Tensor end) {
    super(RnTransitionSpace.INSTANCE, start, end);
  }

  @Override // from Transition
  public Tensor sampled(Scalar ofs, Scalar dt) {
    // RnGeodesic.INSTANCE.curve(start(), end());
    // TODO JPH implementation not efficient
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    Scalar length = RnTransitionSpace.INSTANCE.distance(this);
    Tensor tensor = Tensors.empty();
    while (Scalars.lessThan(ofs, length)) {
      Tensor x = start().multiply(length.subtract(ofs).divide(length)) //
          .add(end().multiply(ofs.divide(length)));
      tensor.append(x);
      ofs = ofs.add(dt);
    }
    return tensor;
  }
}
