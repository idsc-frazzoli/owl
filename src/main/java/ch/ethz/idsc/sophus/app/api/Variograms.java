// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.krg.ExponentialVariogram;
import ch.ethz.idsc.sophus.krg.GaussianVariogram;
import ch.ethz.idsc.sophus.krg.InverseMultiquadricVariogram;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.sophus.krg.MultiquadricVariogram;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.krg.SphericalVariogram;
import ch.ethz.idsc.sophus.krg.ThinPlateSplineVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum Variograms {
  INVERSE_POWER() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return InversePowerVariogram.of(RealScalar.ONE, param);
    }
  }, //
  POWER() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return PowerVariogram.of(RealScalar.ONE, param);
    }
  }, //
  THIN_PLATE_SPLINE() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return ThinPlateSplineVariogram.of(param);
    }
  }, //
  EXPONENTIAL() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return ExponentialVariogram.of(param, RealScalar.ONE);
    }
  },
  GAUSSIAN() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return GaussianVariogram.of(param);
    }
  }, //
  SPHERICAL() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return SphericalVariogram.of(param, RealScalar.ONE);
    }
  },
  MULTI_QUADRATIC() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return new MultiquadricVariogram(param);
    }
  }, //
  INVERSE_MULTI_QUADRADIC() {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return new InverseMultiquadricVariogram(param);
    }
  }, //
  ;

  public abstract ScalarUnaryOperator of(Scalar param);
}
