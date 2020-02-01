// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** slightly different from {@link ImageCostFunction}
 * because evaluation only happens at last state of trajectory */
public final class SparseImageCostFunction extends ImageCostFunction {
  public SparseImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    super(image, range, outside);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return flipYXTensorInterp.at(Lists.getLast(trajectory).state());
  }
}
