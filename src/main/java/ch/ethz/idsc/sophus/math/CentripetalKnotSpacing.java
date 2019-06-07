// code by ob, jph
package ch.ethz.idsc.sophus.math;

import java.util.Objects;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.FoldList;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference:
 * B-Spline Interpolation and Approximation
 * Hongxin Zhang and Jieqing Feng
 * http://www.cad.zju.edu.cn/home/zhx/GM/009/00-bsia.pdf */
public class CentripetalKnotSpacing implements TensorUnaryOperator {
  /** @param tensorMetric for instance Se2ParametricDistance::of
   * @param exponent in the interval [0, 1] */
  public static TensorUnaryOperator of(TensorMetric tensorMetric, Scalar exponent) {
    return new CentripetalKnotSpacing(Objects.requireNonNull(tensorMetric), Clips.unit().requireInside(exponent));
  }

  public static TensorUnaryOperator uniform(TensorMetric tensorMetric) {
    return of(tensorMetric, RealScalar.ZERO);
  }

  public static TensorUnaryOperator chordal(TensorMetric tensorMetric) {
    return of(tensorMetric, RealScalar.ONE);
  }

  // ---
  private final ScalarUnaryOperator power;
  private final TensorMetric tensorMetric;

  private CentripetalKnotSpacing(TensorMetric tensorMetric, Scalar exponent) {
    this.tensorMetric = tensorMetric;
    power = Power.function(exponent);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor control) {
    Tensor knots = Unprotect.empty(control.length() - 1);
    Tensor prev = control.get(0);
    for (int index = 1; index < control.length(); ++index)
      knots.append(power.apply(tensorMetric.distance(prev, prev = control.get(index))));
    return FoldList.of(Tensor::add, RealScalar.ZERO, knots);
  }
}
