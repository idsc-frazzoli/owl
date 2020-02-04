// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinates;
import ch.ethz.idsc.sophus.math.win.AffineCoordinates;
import ch.ethz.idsc.sophus.math.win.RnNullCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum R2Barycentrics {
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
  AFFINE_COORDS() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return AffineCoordinates.of(polygon);
    }
  }, //
  NULL2_COORDS() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return RnNullCoordinates.of(Norm._2::ofVector, polygon);
    }
  }, //
  NULL1_COORDS() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return RnNullCoordinates.of(Norm._1::ofVector, polygon);
    }
  }, //
  ;

  public abstract TensorUnaryOperator span(Tensor polygon);
}
