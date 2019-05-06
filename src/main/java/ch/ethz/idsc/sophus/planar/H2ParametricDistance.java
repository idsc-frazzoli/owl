// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcCosh;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

// Source: https://en.wikipedia.org/wiki/Poincar%C3%A9_half-plane_model#Distance_calculation
public enum H2ParametricDistance implements TensorMetric {
  INSTANCE;
  /** @param p element in H2 of the form {px, py}
   * @param q element in SE2 of the form {qx, qy}
   * @return length of geodesic between p and q */
  @Override
  public Scalar distance(Tensor p, Tensor q) {
    Sign.requirePositive(p.Get(1));
    Sign.requirePositive(q.Get(1));
    Scalar denominator = RealScalar.of(2).multiply(p.Get(1).multiply(q.Get(1)));
    Scalar result = ArcCosh.of(RealScalar.ONE.add(Power.of(Norm._2.between(q, p), 2).divide(denominator)));
    return result;
  }
}