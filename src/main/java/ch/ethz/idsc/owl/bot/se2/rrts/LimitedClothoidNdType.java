// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.adapter.NdType;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
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
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new LimitedClothoidNdCenter(center, clip) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return ClothoidBuilders.SE2_ANALYTIC.curve(other, center);
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new LimitedClothoidNdCenter(center, clip) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return ClothoidBuilders.SE2_ANALYTIC.curve(center, other);
      }
    };
  }
}
