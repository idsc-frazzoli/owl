// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
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
  private static final Scalar NUMERIC_HALF = DoubleScalar.of(0.5);

  /** @param geodesicInterface
   * @param factor */
  public static TensorUnaryOperator cyclic(GeodesicInterface geodesicInterface, Scalar factor) {
    return new Regularization2StepCyclic(geodesicInterface, factor);
  }

  /** @param geodesicInterface
   * @param factor */
  public static TensorUnaryOperator string(GeodesicInterface geodesicInterface, Scalar factor) {
    return new Regularization2StepString(geodesicInterface, factor);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar factor;
  private final Scalar half;

  /* package */ Regularization2Step(GeodesicInterface geodesicInterface, Scalar factor) {
    this.geodesicInterface = geodesicInterface;
    this.factor = factor;
    this.half = ExactScalarQ.of(factor) //
        ? RationalScalar.HALF
        : NUMERIC_HALF;
  }

  Tensor average(Tensor prev, Tensor curr, Tensor next) {
    return geodesicInterface.split(curr, geodesicInterface.split(prev, next, half), factor);
  }
}
