// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.planar.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorMetric;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Mod;

public enum Se2CoveringParametricDistance implements TensorMetric {
  INSTANCE;
  // ---
  private static final Scalar HALF = RealScalar.of(0.5);
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());

  /** @param p element in SE2 of the form {px, py, p_heading}
   * @param q element in SE2 of the form {qx, qy, q_heading}
   * @return length of geodesic between p and q when projected to R^2 including the number of windings */
  @Override
  public Scalar distance(Tensor p, Tensor q) {
    // Distance procjected on S2
    Scalar distance = Se2ParametricDistance.INSTANCE.distance(p, q);
    // change of heading w/o modulo
    Scalar alphaCovering = p.Get(2).subtract(q.get(2)).multiply(HALF);
    // number of windings
    Scalar windings = Floor.FUNCTION.apply(alphaCovering.divide(RealScalar.of(2 * Math.PI)));
    // alpha from se2ParametricDistance
    Scalar alpha = MOD_DISTANCE.apply(p.Get(2).subtract(q.get(2))).multiply(HALF);
    // length of one winding
    Scalar circleDistance = alpha.divide(RealScalar.of(2 * Math.PI)).multiply(distance);
    return distance.add(windings.multiply(circleDistance));
  }
}
