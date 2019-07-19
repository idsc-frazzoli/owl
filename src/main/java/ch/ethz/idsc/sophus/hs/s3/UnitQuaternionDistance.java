// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.sophus.math.Metric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.ArcCos;

/** Reference:
 * https://en.wikipedia.org/wiki/Quaternion */
public enum UnitQuaternionDistance implements Metric<Quaternion> {
  INSTANCE;
  // ---
  @Override
  public Scalar distance(Quaternion p, Quaternion q) {
    Scalar dot = p.w().multiply(q.w()).add(p.xyz().dot(q.xyz()));
    Scalar dot2 = dot.multiply(dot);
    Scalar add = dot2.add(dot2).subtract(RealScalar.ONE);
    return ArcCos.FUNCTION.apply(add);
  }
}
