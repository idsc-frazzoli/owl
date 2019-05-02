// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Sinc;

public enum Se2ParametricDistance implements TensorMetric {
  INSTANCE;
  // ---
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2
   * the projection is a circle segment */
  @Override
  public Scalar distance(Tensor p, Tensor q) {
    Scalar alpha = MOD_DISTANCE.apply(p.Get(2).subtract(q.get(2))).multiply(HALF);
    return Norm._2.between(p.extract(0, 2), q.extract(0, 2)).divide(Sinc.FUNCTION.apply(alpha));
  }

  public Scalar norm(Tensor q) {
    return distance(q.map(Scalar::zero), q);
  }
}