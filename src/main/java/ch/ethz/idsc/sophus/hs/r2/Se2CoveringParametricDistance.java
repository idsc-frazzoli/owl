// code by ob
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sin;

public enum Se2CoveringParametricDistance implements TensorMetric {
  INSTANCE;
  // ---
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2 including the number of windings */
  @Override
  public Scalar distance(Tensor p, Tensor q) {
    Scalar norm = Norm._2.between(p.extract(0, 2), q.extract(0, 2));
    Scalar alpha = q.Get(2).subtract(p.Get(2));
    if (Scalars.isZero(alpha))
      return norm;
    Scalar ahalf = alpha.multiply(HALF);
    Scalar radius = norm.multiply(HALF).divide(Sin.FUNCTION.apply(ahalf));
    return radius.multiply(alpha).abs();
  }
}
