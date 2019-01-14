// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.sophus.dubins.DubinsPath;
import ch.ethz.idsc.sophus.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class Se2Transition extends AbstractTransition {
  private final DubinsPath dubinsPath;

  public Se2Transition(Tensor start, Tensor end, Scalar radius) {
    super(start, end);
    dubinsPath = FixedRadiusDubins.of(start, end, radius).allValid().min(DubinsPathComparator.length()).get();
  }

  @Override // from Transition
  public Scalar length() {
    return dubinsPath.length();
  }

  @Override // from Transition
  public Tensor sampled(Scalar ofs, Scalar dt) {
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
    Scalar length = length();
    Tensor tensor = Tensors.empty();
    while (Scalars.lessThan(ofs, length)) {
      tensor.append(scalarTensorFunction.apply(ofs));
      ofs = ofs.add(dt);
    }
    return tensor;
  }

  public DubinsPath dubinsPath() {
    return dubinsPath;
  }
}
