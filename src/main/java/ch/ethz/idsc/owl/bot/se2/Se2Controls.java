// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Max;

/* package */ enum Se2Controls {
  ;
  /** @param flows
   * @return max speed with unit "m*s^-1" */
  public static Scalar maxSpeed(Collection<Flow> flows) {
    return flows.stream().map(Flow::getU).map(u -> u.Get(0).abs()).reduce(Max::of).get();
  }

  /** @param flows
   * @return TODO max rate per meter driven in unit "s^-1" */
  public static Scalar maxTurning(Collection<Flow> flows) {
    return flows.stream().map(Flow::getU).map(u -> u.Get(2).abs()).reduce(Max::of).get();
  }
}
