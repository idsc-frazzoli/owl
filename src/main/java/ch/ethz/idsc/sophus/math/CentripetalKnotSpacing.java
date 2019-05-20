// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

// B-Spline Interpolation and Approximation
// Hongxin Zhang and Jieqing Feng
// http://www.cad.zju.edu.cn/home/zhx/GM/009/00-bsia.pdf
public class CentripetalKnotSpacing implements TensorUnaryOperator {
  private final ScalarUnaryOperator power;
  private final TensorMetric tensorMetric;

  /** @param exponent in the interval [0, 1]
   * @param tensorMetric for instance Se2ParametricDistance::of */
  public CentripetalKnotSpacing(Scalar exponent, TensorMetric tensorMetric) {
    power = Power.function(exponent);
    this.tensorMetric = tensorMetric;
  }

  @Override
  public Tensor apply(Tensor control) {
    if (control.length() <= 0)
      throw TensorRuntimeException.of(control);
    Tensor knots = Tensors.vector(0);
    for (int index = 1; index < control.length(); ++index)
      knots.append(power.apply(tensorMetric.distance(control.get(index - 1), control.get(index))));
    return Accumulate.of(knots);
  }
}
