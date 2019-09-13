// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.RrtsNdType;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class LimitedClothoidRrtsNdType implements RrtsNdType {
  public static LimitedClothoidRrtsNdType with(Scalar max) {
    return new LimitedClothoidRrtsNdType(Clips.absolute(max));
  }

  private final Clip clip;

  private LimitedClothoidRrtsNdType(Clip clip) {
    this.clip = clip;
  }

  @Override
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override
  public NdCenterInterface getNdCenterInterface(Tensor tensor) {
    ClothoidNdCenter center = new ClothoidNdCenter(tensor);
    center.limitCurvature(clip);
    return center;
  }
}
