// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.math.var.ExponentialVariogram;
import ch.ethz.idsc.sophus.math.var.GaussianVariogram;
import ch.ethz.idsc.sophus.math.var.InverseMultiquadricVariogram;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.sophus.math.var.MultiquadricVariogram;
import ch.ethz.idsc.sophus.math.var.PowerVariogram;
import ch.ethz.idsc.sophus.math.var.SphericalVariogram;
import ch.ethz.idsc.sophus.math.var.ThinPlateSplineVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** Quote from Numerical Recipes:
 * To use kriging, you must be able to estimate the mean square variation of
 * your function y(x) as a function of offset distance r, a so-called variogram
 * <pre>
 * v(r) == 1/2 E[ (y(x+r) - y(x)) ^ 2 ]
 * </pre> */
public enum Variograms {
  INVERSE_POWER {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return InversePowerVariogram.of(param);
    }
  },
  POWER {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return PowerVariogram.of(RealScalar.ONE, param);
    }
  },
  THIN_PLATE_SPLINE {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return ThinPlateSplineVariogram.of(param);
    }
  },
  EXPONENTIAL {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return ExponentialVariogram.of(param, RealScalar.ONE);
    }
  },
  GAUSSIAN {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return GaussianVariogram.of(param);
    }
  },
  SPHERICAL {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return SphericalVariogram.of(param, RealScalar.ONE);
    }
  },
  MULTI_QUADRATIC {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return MultiquadricVariogram.of(param);
    }
  },
  INVERSE_MULTI_QUADRADIC {
    @Override
    public ScalarUnaryOperator of(Scalar param) {
      return InverseMultiquadricVariogram.of(param);
    }
  }, //
  ;

  /** @param param
   * @return variogram */
  public abstract ScalarUnaryOperator of(Scalar param);
}
