// code by jph
package ch.ethz.idsc.sophus.crv;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** http://vixra.org/abs/1909.0174 */
public interface CurveDecimation extends TensorUnaryOperator {
  /** @param lieGroup
   * @param log map from group to tangent space
   * @param epsilon non-negative
   * @return
   * @throws Exception if either input parameter is null */
  public static CurveDecimation of(LieGroup lieGroup, TensorUnaryOperator log, Scalar epsilon) {
    return new RamerDouglasPeucker( //
        new LieGroupLineDistance(Objects.requireNonNull(lieGroup), Objects.requireNonNull(log)), //
        Sign.requirePositiveOrZero(epsilon));
  }

  /** @param lineDistance
   * @param epsilon non-negative
   * @return */
  public static CurveDecimation of(LineDistance lineDistance, Scalar epsilon) {
    return new RamerDouglasPeucker(lineDistance, Sign.requirePositiveOrZero(epsilon));
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
