// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.N;

public class Se2CarFlows implements FlowsInterface, Serializable {
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param speed, positive for forward, and negative for backward, unit [m/s]
   * @param ratio of turning, unit [m^-1]
   * @return flow with u == {speed[m*s^-1], 0.0[m*s^-1], (ratio*speed)[s^-1]} */
  public static Tensor singleton(Scalar speed, Scalar ratio) {
    return N.DOUBLE.of(Tensors.of(speed, speed.zero(), ratio.multiply(speed)));
  }

  /** @param speed with unit [m*s^-1]
   * @param rate_max with unit [m^-1], i.e. the amount of rotation [] performed per distance [m^-1]
   * @return */
  public static FlowsInterface standard(Scalar speed, Scalar rate_max) {
    return new Se2CarFlows(Tensors.of(speed, speed.negate()), rate_max);
  }

  /** @param speed with unit [m*s^-1]
   * @param rate_max with unit [m^-1], i.e. the amount of rotation [] performed per distance [m^-1]
   * @return */
  public static FlowsInterface forward(Scalar speed, Scalar rate_max) {
    return new Se2CarFlows(Tensors.of(speed), rate_max);
  }

  /** @param speeds vector with unit [m*s^-1]
   * @param rate_max with unit [m^-1], i.e. the amount of rotation [] performed per distance [m^-1]
   * @return */
  public static FlowsInterface of(Tensor speeds, Scalar rate_max) {
    return new Se2CarFlows(speeds, rate_max);
  }

  /***************************************************/
  private final Tensor speeds;
  private final Scalar rate_max;

  private Se2CarFlows(Tensor speeds, Scalar rate_max) {
    this.speeds = VectorQ.require(speeds);
    this.rate_max = rate_max;
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    resolution += resolution & 1;
    List<Tensor> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(rate_max.negate(), rate_max, resolution))
      for (Tensor speed : speeds)
        list.add(singleton((Scalar) speed, (Scalar) angle));
    return Collections.unmodifiableList(list);
  }
}
