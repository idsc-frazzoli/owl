// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.rn.RnInverseDistanceCoordinates;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.lie.rn.RnMetricSquared;
import ch.ethz.idsc.sophus.math.win.AffineCoordinates;
import ch.ethz.idsc.sophus.math.win.InverseDistanceWeighting;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum PolygonWeights {
  WACHSPRESS() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return new R2BarycentricCoordinates(Barycenter.WACHSPRESS).of(polygon);
    }
  }, //
  MEAN_VALUE() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return new R2BarycentricCoordinates(Barycenter.MEAN_VALUE).of(polygon);
    }
  }, //
  DISCRETE_HARMONIC() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return new R2BarycentricCoordinates(Barycenter.DISCRETE_HARMONIC).of(polygon);
    }
  }, //
  INVERSE_DISTANCE() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return p -> RnInverseDistanceCoordinates.INSTANCE.weights(polygon, p);
    }
  }, //
  INVERSE_DISTANCE2() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return p -> RnInverseDistanceCoordinates.SQUARED.weights(polygon, p);
    }
  }, //
  AFFINE() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return AffineCoordinates.of(polygon);
    }
  }, //
  SHEPARD() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return new InverseDistanceWeighting(RnMetric.INSTANCE).of(polygon);
    }
  }, //
  SHEPARD2() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return new InverseDistanceWeighting(RnMetricSquared.INSTANCE).of(polygon);
    }
  }, //
  ;

  public abstract TensorUnaryOperator span(Tensor polygon);
}
