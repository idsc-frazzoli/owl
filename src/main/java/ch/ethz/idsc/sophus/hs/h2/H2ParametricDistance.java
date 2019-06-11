// code by ob
package ch.ethz.idsc.sophus.hs.h2;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.ArcCosh;
import ch.ethz.idsc.tensor.sca.Sign;

/** Source:
 * https://en.wikipedia.org/wiki/Poincar%C3%A9_half-plane_model#Distance_calculation */
public enum H2ParametricDistance implements TensorMetric {
  INSTANCE;
  // ---
  /** @param p element in H2 of the form {px, py}
   * @param q element in H2 of the form {qx, qy}
   * @return length of geodesic between p and q
   * @throws Exception if the y component of p or q is not strictly positive */
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    Scalar pqy = Sign.requirePositive(p.Get(1)).multiply(Sign.requirePositive(q.Get(1)));
    return ArcCosh.FUNCTION.apply(Norm2Squared.between(p, q).divide(pqy.add(pqy)).add(RealScalar.ONE));
  }
}