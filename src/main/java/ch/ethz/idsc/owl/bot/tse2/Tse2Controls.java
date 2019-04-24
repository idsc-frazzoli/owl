// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

public enum Tse2Controls {
  ;
  /** @param flows
   * @return max rate per meter driven in unit "s^-1" */
  // TODO JPH max effective turning rate/angular rate depends on tangent speed
  public static Scalar maxTurning(Collection<Flow> flows) {
    return flows.stream() //
        .map(Flow::getU) //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_STEER).abs()) //
        .reduce(Max::of).get();
  }

  /** @param flows
   * @return min acceleration (max deceleration) with unit "m*s^-2" */
  public static Scalar minAcc(Collection<Flow> flows) {
    return flows.stream() //
        .map(Flow::getU) //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL)) //
        .reduce(Min::of).get();
  }

  /** @param flows
   * @return max acceleration with unit "m*s^-2" */
  public static Scalar maxAcc(Collection<Flow> flows) {
    return flows.stream() //
        .map(Flow::getU) //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL)) //
        .reduce(Max::of).get();
  }
}
