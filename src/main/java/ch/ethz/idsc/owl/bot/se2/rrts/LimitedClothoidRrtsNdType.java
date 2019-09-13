// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.RrtsNdType;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class LimitedClothoidRrtsNdType implements RrtsNdType, Serializable {
  /** @param max non-negative
   * @return */
  public static LimitedClothoidRrtsNdType with(Scalar max) {
    return new LimitedClothoidRrtsNdType(Clips.absolute(max));
  }

  // ---
  private final Clip clip;

  /** @param clip non-null */
  private LimitedClothoidRrtsNdType(Clip clip) {
    this.clip = clip;
  }

  @Override // from RrtsNdType
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override // from RrtsNdType
  public NdCenterInterface getNdCenterInterface(Tensor tensor) {
    return new LimitedClothoidNdCenter(tensor, clip);
  }
}
