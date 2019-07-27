// code by ob
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Floor;

// TODO JPH OWL 049 move to package hs.r2
public enum Se2CoveringParametricDistance implements TensorMetric {
  INSTANCE;
  // ---
  private static final Scalar HALF = RealScalar.of(0.5);

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2 including the number of windings */
  @Override
  public Scalar distance(Tensor p, Tensor q) {
    // Distance projected on SE2
    Scalar distance = Se2ParametricDistance.INSTANCE.distance(p, q);
    // change of heading w/o modulo
    Scalar alphaCovering = q.Get(2).subtract(p.get(2)).multiply(HALF);
    // number of windings
    Scalar windings = Floor.FUNCTION.apply(alphaCovering.divide(RealScalar.of(2 * Math.PI)));
    // alpha from Se2ParametricDistance
    Scalar alpha = So2.MOD.apply(q.Get(2).subtract(p.get(2))).multiply(HALF);
    // length of one winding
    Scalar circleDistance = alpha.divide(RealScalar.of(2 * Math.PI)).multiply(distance);
    return distance.add(windings.multiply(circleDistance));
  }
}
