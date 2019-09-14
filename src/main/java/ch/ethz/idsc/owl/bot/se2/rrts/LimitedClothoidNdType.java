// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.NdType;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class LimitedClothoidNdType implements NdType, Serializable {
  /** @param max non-negative
   * @return */
  public static LimitedClothoidNdType with(Scalar max) {
    return new LimitedClothoidNdType(Clips.absolute(max));
  }

  // ---
  private final Clip clip;

  /** @param clip non-null */
  private LimitedClothoidNdType(Clip clip) {
    this.clip = clip;
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor center) {
    return new LimitedClothoidNdCenter(center, clip) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return new Clothoid(center, other);
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor center) {
    return new LimitedClothoidNdCenter(center, clip) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return new Clothoid(other, center);
      }
    };
  }
}
