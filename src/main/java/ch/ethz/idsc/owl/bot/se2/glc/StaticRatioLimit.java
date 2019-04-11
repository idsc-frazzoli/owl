package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class StaticRatioLimit implements DynamicRatioLimit {
  private final Scalar maxTurningRate;

  /** static turning ratio limit
   * @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public StaticRatioLimit(Scalar maxTurningRate) {
    this.maxTurningRate = maxTurningRate;
  }

  @Override // from DynamicRatioLimit
  public Clip at(Tensor state, Scalar speed) {
    return Clips.interval(maxTurningRate.negate(), maxTurningRate);
  }
}
