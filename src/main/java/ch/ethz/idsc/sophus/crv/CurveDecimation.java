// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** Generalization of the Ramer-Douglas-Peucker algorithm
 * 
 * Quote: "The Ramer-Douglas-Peucker algorithm decimates a curve composed of line segments
 * to a similar curve with fewer points. [...] The algorithm defines 'dissimilar' based
 * on the maximum distance between the original curve and the simplified curve. [...]
 * The expected complexity of this algorithm can be described by the linear recurrence
 * T(n) = 2 * T(​n/2) + O(n), which has the well-known solution O(n * log n). However, the
 * worst-case complexity is O(n^2)."
 * 
 * https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm */
public interface CurveDecimation extends TensorUnaryOperator {
  /** @param lieGroup
   * @param log map from group to tangent space
   * @param epsilon non-negative
   * @return
   * @throws Exception if either input parameter is null */
  public static CurveDecimation of(LieGroup lieGroup, TensorUnaryOperator log, Scalar epsilon) {
    return new LieGroupCurveDecimation( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(log), //
        Sign.requirePositiveOrZero(epsilon));
  }

  /** @param lineDistance
   * @param epsilon non-negative
   * @return */
  public static CurveDecimation of(LineDistance lineDistance, Scalar epsilon) {
    return new SpaceCurveDecimation(lineDistance, Sign.requirePositiveOrZero(epsilon));
  }

  /***************************************************/
  public static interface Result {
    /** @return */
    Tensor result();

    /** @return */
    Tensor errors();
  }

  /***************************************************/
  /** @param tensor
   * @return */
  Result evaluate(Tensor tensor);
}
