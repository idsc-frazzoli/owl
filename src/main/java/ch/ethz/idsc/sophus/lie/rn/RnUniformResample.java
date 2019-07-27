// code by jph
package ch.ethz.idsc.sophus.lie.rn;

import java.util.Objects;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.tensor.Scalar;

public enum RnUniformResample {
  ;
  /** @param spacing non-null
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return new UniformResample( //
        RnMetric.INSTANCE, //
        RnGeodesic.INSTANCE, //
        Objects.requireNonNull(spacing));
  }
}
