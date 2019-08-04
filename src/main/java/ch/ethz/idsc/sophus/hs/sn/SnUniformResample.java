// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.tensor.Scalar;

public enum SnUniformResample {
  ;
  /** @param spacing positive
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return UniformResample.of(SnMetric.INSTANCE, SnGeodesic.INSTANCE, spacing);
  }
}
