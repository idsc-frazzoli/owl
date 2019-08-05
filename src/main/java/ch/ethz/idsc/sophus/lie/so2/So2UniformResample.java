// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.tensor.Scalar;

public enum So2UniformResample {
  ;
  /** @param spacing positive
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return UniformResample.of(So2Metric.INSTANCE, So2Geodesic.INSTANCE, spacing);
  }
}
