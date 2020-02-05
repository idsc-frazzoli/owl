// code by jph
package ch.ethz.idsc.sophus.app.jph;

import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.R2BarycentricCoordinates;
import ch.ethz.idsc.sophus.math.win.AffineCoordinates;
import ch.ethz.idsc.sophus.math.win.IdfCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;

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
  AFFINE() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return AffineCoordinates.of(polygon);
    }
  }, //
  INVERSE_DISTANCE_FITTED() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return IdfCoordinates.of(Norm._2::ofVector, polygon);
    }
  }, //
  NULL2S_COORDS() {
    @Override
    public TensorUnaryOperator span(Tensor polygon) {
      return IdfCoordinates.of(Norm2Squared::ofVector, polygon);
    }
  }, //
  ;

  public abstract TensorUnaryOperator span(Tensor polygon);
}
