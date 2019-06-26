// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public class RnTransitionSpace extends AbstractTransitionSpace {
  public static final TransitionSpace INSTANCE = new RnTransitionSpace();

  private RnTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
      @Override
      public Tensor sampled(Scalar ofs, Scalar ds) {
        // RnGeodesic.INSTANCE.curve(start(), end());
        // TODO JPH implementation not efficient
        if (Scalars.lessThan(ds, ofs))
          throw TensorRuntimeException.of(ofs, ds);
        Scalar length = RnTransitionSpace.INSTANCE.distance(this);
        Tensor tensor = Tensors.empty();
        while (Scalars.lessThan(ofs, length)) {
          Tensor x = start().multiply(length.subtract(ofs).divide(length)) //
              .add(end().multiply(ofs.divide(length)));
          tensor.append(x);
          ofs = ofs.add(ds);
        }
        return tensor;
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Norm._2.between(start, end);
  }
}
