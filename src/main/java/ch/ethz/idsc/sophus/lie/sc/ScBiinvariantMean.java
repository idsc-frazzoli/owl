// code by jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.sophus.lie.ScalarBiinvariantMean;
import ch.ethz.idsc.sophus.lie.st.StBiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

/** 1-parameter Scaling Group (R+, *)
 * 
 * @see StBiinvariantMean */
public enum ScBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    return Exp.FUNCTION.apply(AffineQ.require(weights).dot(sequence.map(Log.FUNCTION)).Get());
  }
}
