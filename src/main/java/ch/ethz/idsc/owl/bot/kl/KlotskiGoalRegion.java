// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiGoalRegion implements Region<Tensor> {
  private final Tensor stone;

  /** Example: for Huarong Tensors.vector(0, 4, 2)
   * 
   * @param stone */
  public KlotskiGoalRegion(Tensor stone) {
    this.stone = stone;
  }

  @Override // from Region
  public boolean isMember(Tensor x) {
    return x.get(0).equals(stone);
  }
}
