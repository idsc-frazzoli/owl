// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** two wheel drive embedded as robot in se2 with capability to turn on the spot
 * the implementation of the twd flows assumes that the two wheels are independent
 * from each other: both wheels can be commanded forward and reverse at maximum rates.
 * 
 * Example: duckie bots
 * 
 * ^ +y
 * |
 * WL speedL
 * --
 * |
 * |
 * |------> +x
 * |
 * |
 * --
 * WR speedR */
public class TwdDuckieFlows extends TwdFlows {
  /** @param maxSpeed [m*s^-1]
   * @param halfWidth [m*rad^-1] */
  public TwdDuckieFlows(Scalar maxSpeed, Scalar halfWidth) {
    super(maxSpeed, halfWidth);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    List<Tensor> list = new ArrayList<>();
    Tensor range = Subdivide.of(-1, 1, resolution).extract(0, resolution); // [-1, ..., 1)
    for (Tensor _omega : range) {
      Scalar omega = (Scalar) _omega;
      list.add(singleton(RealScalar.ONE, omega));
      list.add(singleton(omega.negate(), RealScalar.ONE));
      list.add(singleton(RealScalar.ONE.negate(), omega.negate()));
      list.add(singleton(omega, RealScalar.ONE.negate()));
    }
    return list;
  }
}
