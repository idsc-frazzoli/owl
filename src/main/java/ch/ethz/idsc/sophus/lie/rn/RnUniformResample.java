// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.tensor.Scalar;

public enum RnUniformResample {
  ;
  /** @param spacing non-null
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return UniformResample.of(RnMetric.INSTANCE, RnGeodesic.INSTANCE, spacing);
  }
}
