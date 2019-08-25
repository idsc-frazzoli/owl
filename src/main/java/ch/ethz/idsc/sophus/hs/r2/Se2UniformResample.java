// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;

public enum Se2UniformResample {
  ;
  /** @param spacing positive
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return UniformResample.of(Se2ParametricDistance.INSTANCE, Se2Geodesic.INSTANCE, spacing);
  }
}
