// code by ob, jph
package ch.ethz.idsc.sophus.math;

import java.util.Objects;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.FoldList;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Reference:
 * B-Spline Interpolation and Approximation
 * Hongxin Zhang and Jieqing Feng
 * http://www.cad.zju.edu.cn/home/zhx/GM/009/00-bsia.pdf */
public class CentripetalKnotSpacing implements TensorUnaryOperator {
  /** @param tensorMetric for instance Se2ParametricDistance::of
   * @param exponent typically in the interval [0, 1] */
  public static TensorUnaryOperator of(TensorMetric tensorMetric, Scalar exponent) {
    return new CentripetalKnotSpacing(Objects.requireNonNull(tensorMetric), Power.function(exponent));
  }

  /** @param tensorMetric for instance Se2ParametricDistance.INSTANCE
   * @param exponent in the interval [0, 1] */
  public static TensorUnaryOperator of(TensorMetric tensorMetric, Number exponent) {
    return of(tensorMetric, RealScalar.of(exponent));
  }

  /** @param tensorMetric
   * @return */
  public static TensorUnaryOperator uniform(TensorMetric tensorMetric) {
    return tensor -> Range.of(0, tensor.length());
  }

  /** @param tensorMetric
   * @return */
  public static TensorUnaryOperator chordal(TensorMetric tensorMetric) {
    return new CentripetalKnotSpacing(Objects.requireNonNull(tensorMetric), scalar -> scalar);
  }

  // ---
  private final TensorMetric tensorMetric;
  private final ScalarUnaryOperator distanceFunction;

  private CentripetalKnotSpacing(TensorMetric tensorMetric, ScalarUnaryOperator distanceFunction) {
    this.tensorMetric = tensorMetric;
    this.distanceFunction = distanceFunction;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor control) {
    Tensor knots = Unprotect.empty(control.length() - 1);
    Tensor prev = control.get(0);
    for (int index = 1; index < control.length(); ++index)
      knots.append(distanceFunction.apply(tensorMetric.distance(prev, prev = control.get(index))));
    return FoldList.of(Tensor::add, RealScalar.ZERO, knots);
  }
}
