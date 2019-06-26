// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

public class ClothoidTransitionSpace extends AbstractTransitionSpace implements Se2TransitionSpace {
  public static final TransitionSpace INSTANCE = new ClothoidTransitionSpace();

  private ClothoidTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
      @Override
      public Tensor sampled(Scalar ofs, Scalar ds) {
        if (Scalars.lessThan(ds, ofs))
          throw TensorRuntimeException.of(ofs, ds);
        Scalar length = distance(this);
        Tensor tensor = Tensors.empty();
        while (Scalars.lessThan(ofs, length)) {
          tensor.append(Clothoid1.INSTANCE.split(start(), end(), ofs.divide(length)));
          ofs = ofs.add(ds);
        }
        return tensor;
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return PseudoClothoidDistance.INSTANCE.distance(start, end);
  }
}
