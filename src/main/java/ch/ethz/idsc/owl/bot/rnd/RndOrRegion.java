// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/** 
 * 
 */
public class RndOrRegion implements Region<Tensor> {
  public static Region<Tensor> common(Region<Tensor> region) {
    return new RndOrRegion(region, region);
  }

  // ---
  private final Region<Tensor> region1;
  private final Region<Tensor> region2;

  private RndOrRegion(Region<Tensor> region1, Region<Tensor> region2) {
    this.region1 = region1;
    this.region2 = region2;
  }

  @Override
  public boolean isMember(Tensor tensor) {
    RndState rndState = RndState.of(tensor);
    return region1.isMember(rndState.x1) //
        || region2.isMember(rndState.x2);
  }
}
