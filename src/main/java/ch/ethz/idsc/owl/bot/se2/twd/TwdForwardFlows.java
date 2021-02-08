// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** two wheel drive embedded as robot in se2 with capability to turn on the spot,
 * but is only permitted to drive forwards, i.e. the longitudinal speed is non-negative.
 * 
 * the implementation of the twd flows assumes that the two wheels are independent
 * from each other. */
public class TwdForwardFlows extends TwdFlows {
  /** @param maxSpeed [m*s^-1]
   * @param halfWidth [m*rad^-1] */
  public TwdForwardFlows(Scalar maxSpeed, Scalar halfWidth) {
    super(maxSpeed, halfWidth);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    List<Tensor> list = new ArrayList<>();
    Tensor range = Subdivide.of(-1, 1, resolution).extract(0, resolution); // [-1, ..., 1)
    for (Tensor _omega : range) {
      Scalar omega = (Scalar) _omega;
      list.add(singleton(RealScalar.ONE, omega));
      list.add(singleton(omega, RealScalar.ONE));
    }
    list.add(singleton(RealScalar.ONE, RealScalar.ONE));
    return list;
  }
}
