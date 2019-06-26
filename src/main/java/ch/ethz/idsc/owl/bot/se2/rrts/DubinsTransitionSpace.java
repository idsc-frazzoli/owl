// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Optional;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class DubinsTransitionSpace extends AbstractTransitionSpace implements Se2TransitionSpace {
  private static final DubinsTransitionSpace INSTANCE = new DubinsTransitionSpace();

  public static DubinsTransitionSpace withRadius(Scalar radius) {
    DubinsTransitionSpace.INSTANCE.radius = radius;
    return DubinsTransitionSpace.INSTANCE;
  }

  // ---
  private Scalar radius;

  private DubinsTransitionSpace() {
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
      DubinsPath dubinsPath = FixedRadiusDubins.of(start, end, radius).allValid().min(DubinsPathComparator.length()).get();

      @Override
      public Tensor sampled(Scalar ofs, Scalar ds) {
        if (Scalars.lessThan(ds, ofs))
          throw TensorRuntimeException.of(ofs, ds);
        ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
        Scalar length = dubinsPath.length();
        Tensor tensor = Tensors.empty();
        while (Scalars.lessThan(ofs, length)) {
          tensor.append(scalarTensorFunction.apply(ofs));
          ofs = ofs.add(ds);
        }
        return tensor;
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return dubinsPath(start, end).map(DubinsPath::length).get();
  }

  /** @param start
   * @param end
   * @return dubins path with minimal length if any */
  public Optional<DubinsPath> dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).allValid().min(DubinsPathComparator.length());
  }
}
