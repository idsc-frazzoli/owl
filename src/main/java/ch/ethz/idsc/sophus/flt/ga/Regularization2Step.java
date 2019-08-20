// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** geodesic average between 3 points for symmetric weight mask
 * {factor/2, 1-factor, factor/2}
 * implemented in 2-steps
 * 
 * a factor of 0 results in the identity operator
 * typically the factor is in the interval [0, 1] */
public abstract class Regularization2Step implements TensorUnaryOperator {
  /** @param splitInterface
   * @param factor for instance 2/3 */
  public static TensorUnaryOperator cyclic(SplitInterface splitInterface, Scalar factor) {
    return new Regularization2StepCyclic(splitInterface, factor);
  }

  /** @param splitInterface
   * @param factor for instance 2/3 */
  public static TensorUnaryOperator string(SplitInterface splitInterface, Scalar factor) {
    return new Regularization2StepString(splitInterface, factor);
  }

  // ---
  private final SplitInterface splitInterface;
  private final Scalar factor;

  /* package */ Regularization2Step(SplitInterface splitInterface, Scalar factor) {
    this.splitInterface = splitInterface;
    this.factor = factor;
  }

  final Tensor average(Tensor prev, Tensor curr, Tensor next) {
    return splitInterface.split(curr, splitInterface.midpoint(prev, next), factor);
  }
}
