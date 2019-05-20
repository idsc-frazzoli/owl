// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorMetric;

public enum CentripetalKnotSpacingHelper {
  ;
  public static CentripetalKnotSpacing uniform(TensorMetric tensorMetric) {
    return of(tensorMetric, RealScalar.ZERO);
  }

  public static CentripetalKnotSpacing chordal(TensorMetric tensorMetric) {
    return of(tensorMetric, RealScalar.ONE);
  }

  public static CentripetalKnotSpacing centripetal(TensorMetric tensorMetric, Scalar exponent) {
    return of(tensorMetric, exponent);
  }

  public static CentripetalKnotSpacing of(TensorMetric tensorMetric, Scalar exponent) {
    return new CentripetalKnotSpacing(tensorMetric, exponent);
  }
}