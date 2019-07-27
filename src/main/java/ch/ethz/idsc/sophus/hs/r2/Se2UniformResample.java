// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.util.Objects;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.itp.UniformResample;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Scalar;

public enum Se2UniformResample {
  ;
  /** @param spacing non-null
   * @return */
  public static CurveSubdivision of(Scalar spacing) {
    return new UniformResample( //
        Se2ParametricDistance.INSTANCE, //
        Se2Geodesic.INSTANCE, //
        Objects.requireNonNull(spacing));
  }
}
