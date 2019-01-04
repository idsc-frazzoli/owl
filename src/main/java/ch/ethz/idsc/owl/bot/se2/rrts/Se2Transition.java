// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.sophus.dubins.DubinsPath;
import ch.ethz.idsc.sophus.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.dubins.DubinsPathLengthComparator;
import ch.ethz.idsc.sophus.dubins.FixedRadiusDubins;
import ch.ethz.idsc.sophus.group.Se2CoveringGroupElement;
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
    DubinsPathGenerator dubinsPathGenerator = //
        new FixedRadiusDubins(new Se2CoveringGroupElement(start).inverse().combine(end), radius);
    dubinsPath = dubinsPathGenerator.allValid() //
        .min(DubinsPathLengthComparator.INSTANCE).get();
  }

  @Override
  public Scalar length() {
    return dubinsPath.length();
  }

  public DubinsPath dubinsPath() {
    return dubinsPath;
  }

  @Override
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

  @Override
  public Tensor splitAt(Scalar scalar) {
    return dubinsPath.sampler(start()).apply(scalar);
  }
}
