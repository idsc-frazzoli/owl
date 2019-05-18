// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** static turning ratio limit */
public class StaticRatioLimit implements DynamicRatioLimit {
  private final Clip clipTurningRate;

  /** @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public StaticRatioLimit(Scalar maxTurningRate) {
    this.clipTurningRate = Clips.absolute(maxTurningRate);
  }

  @Override // from DynamicRatioLimit
  public Clip at(Tensor state, Scalar speed) {
    return clipTurningRate;
  }
}
