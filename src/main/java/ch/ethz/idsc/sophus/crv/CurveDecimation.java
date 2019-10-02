// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** http://vixra.org/abs/1909.0174 */
public interface CurveDecimation extends TensorUnaryOperator {
  /** @param lieGroup
   * @param log map from group to tangent space
   * @param epsilon non-negative
   * @return
   * @throws Exception if either input parameter is null */
  public static CurveDecimation of(LieGroup lieGroup, TensorUnaryOperator log, Scalar epsilon) {
    return new RamerDouglasPeucker(new LieGroupLineDistance(lieGroup, log), epsilon);
  }

  /** @param lieGroup
   * @param lieExponential
   * @param epsilon
   * @return */
  public static CurveDecimation midpoint(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon) {
    return new RamerDouglasPeucker(new LieMidpointLineDistance(lieGroup, lieExponential), epsilon);
  }

  /** @param lieGroup
   * @param log
   * @param epsilon non-negative
   * @return */
  public static CurveDecimation symmetric(LieGroup lieGroup, TensorUnaryOperator log, Scalar epsilon) {
    return new RamerDouglasPeucker( //
        new SymmetricLineDistance(new LieGroupLineDistance(lieGroup, log)), //
        epsilon);
  }

  /** @param lieGroup
   * @param lieExponential
   * @param epsilon
   * @return */
  public static CurveDecimation projected(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon) {
    return new RamerDouglasPeucker( //
        new LieProjectedLineDistance(lieGroup, lieExponential), //
        epsilon);
  }

  /** @param lineDistance
   * @param epsilon non-negative
   * @return */
  public static CurveDecimation of(LineDistance lineDistance, Scalar epsilon) {
    return new RamerDouglasPeucker(lineDistance, epsilon);
  }

  /***************************************************/
  public static interface Result {
    /** @return points in the decimated sequence */
    Tensor result();

    /** @return vector with length of the original sequence */
    Tensor errors();
  }

  /***************************************************/
  /** @param tensor
   * @return */
  Result evaluate(Tensor tensor);
}
