// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;

/** various norms for curve decimation */
public enum LieGroupCurveDecimation {
  STANDARD() {
    @Override
    public CurveDecimation of(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon) {
      return CurveDecimation.of(lieGroup, lieExponential::log, epsilon);
    }
  }, //
  MIDPOINT() {
    @Override
    public CurveDecimation of(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon) {
      return CurveDecimation.midpoint(lieGroup, lieExponential, epsilon);
    }
  }, //
  SYMMETRIZED() {
    @Override
    public CurveDecimation of(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon) {
      return CurveDecimation.symmetric(lieGroup, lieExponential::log, epsilon);
    }
  }, //
  ;
  public abstract CurveDecimation of(LieGroup lieGroup, LieExponential lieExponential, Scalar epsilon);
}
