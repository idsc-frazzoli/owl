// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;

public class RndLostRegion implements Region<Tensor> {
  private final Clip clip;

  /** @param clip */
  public RndLostRegion(Clip clip) {
    this.clip = clip;
  }

  @Override
  public boolean isMember(Tensor tensor) {
    RndState rndState = RndState.of(tensor);
    return clip.isOutside(Norm._2.between(rndState.x1, rndState.x2));
  }
}
