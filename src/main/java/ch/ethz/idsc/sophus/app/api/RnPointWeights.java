// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinates;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public enum RnPointWeights {
  INVERSE_DISTANCE_WEIGHTING() {
    @Override
    public TensorUnaryOperator of(Tensor points) {
      return new InverseDistanceWeighting(RnMetric.INSTANCE).of(points);
    }
  }, //
  INVERSE_DISTANCE_WEIGHTING2() {
    @Override
    public TensorUnaryOperator of(Tensor points) {
      return new InverseDistanceWeighting(Norm2Squared::between).of(points);
    }
  }, //
  INVERSE_DISTANCE_COORDINATES() {
    @Override
    public TensorUnaryOperator of(Tensor points) {
      return p -> RnInverseDistanceCoordinates.INSTANCE.weights(points, p);
    }
  }, //
  ;

  /** @param points
   * @return */
  public abstract TensorUnaryOperator of(Tensor points);
}
