// code by jph
package ch.ethz.idsc.sophus.hs.s3;

import ch.ethz.idsc.sophus.math.Metric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Clips;

/** distance between two quaternions of unit length
 * 
 * <p>Reference:
 * https://en.wikipedia.org/wiki/Quaternion */
public enum UnitQuaternionDistance implements Metric<Quaternion> {
  INSTANCE;
  // ---
  private static final Scalar HALF = RealScalar.of(0.5);

  @Override // from Metric
  public Scalar distance(Quaternion p, Quaternion q) {
    Scalar dot = p.w().multiply(q.w()).add(p.xyz().dot(q.xyz()));
    Scalar dot2 = dot.multiply(dot);
    Scalar ratio = dot2.add(dot2).subtract(RealScalar.ONE);
    if (ratio instanceof RealScalar) // similar to VectorAngle
      ratio = Clips.absoluteOne().apply(ratio);
    return ArcCos.FUNCTION.apply(ratio).multiply(HALF);
  }
}
