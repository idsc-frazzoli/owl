// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.sophus.math.Metric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.Log;

/** implementation for evaluation or {@link UnitQuaternionDistance}
 * 
 * <p>Reference:
 * https://en.wikipedia.org/wiki/Quaternion */
/* package */ enum LogUnitQuaternionDistance implements Metric<Quaternion> {
  INSTANCE;
  // ---
  @Override // from Metric
  public Scalar distance(Quaternion p, Quaternion q) {
    return Log.of(p.reciprocal().multiply(q)).abs();
  }
}
