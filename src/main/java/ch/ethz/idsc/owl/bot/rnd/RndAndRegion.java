// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.tensor.Tensor;

/** 
 * 
 */
public class RndAndRegion implements Region<Tensor> {
  public static Region<Tensor> trivial_1(Region<Tensor> region) {
    return new RndAndRegion(region, Regions.completeRegion());
  }

  // ---
  private final Region<Tensor> region1;
  private final Region<Tensor> region2;

  private RndAndRegion(Region<Tensor> region1, Region<Tensor> region2) {
    this.region1 = region1;
    this.region2 = region2;
  }

  @Override
  public boolean isMember(Tensor tensor) {
    RndState rndState = RndState.of(tensor);
    return region1.isMember(rndState.x1) //
        && region2.isMember(rndState.x2);
  }
}
