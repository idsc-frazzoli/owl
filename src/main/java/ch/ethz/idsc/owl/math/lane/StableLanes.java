// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

public enum StableLanes {
  ;
  /** @param controlPoints in SE2
   * @param tensorUnaryOperator for instance
   * LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1)::string
   * @param level non-negative
   * @param halfWidth
   * @return */
  public static LaneInterface of( //
      Tensor controlPoints, TensorUnaryOperator tensorUnaryOperator, int level, Scalar halfWidth) {
    return StableLane.of( //
        controlPoints, //
        Nest.of(tensorUnaryOperator, controlPoints, level).unmodifiable(), //
        halfWidth);
  }
}
