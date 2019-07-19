// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.sophus.math.Metric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.Log;

/* package */ enum LogUnitQuaternionDistance implements Metric<Quaternion> {
  INSTANCE;
  // ---
  @Override
  public Scalar distance(Quaternion p, Quaternion q) {
    return Log.of(p.reciprocal().multiply(q)).abs();
  }
}
