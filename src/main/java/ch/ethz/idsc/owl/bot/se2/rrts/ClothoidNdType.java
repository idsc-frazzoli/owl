// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.NdType;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidNdType implements NdType {
  INSTANCE;
  // ---
  @Override // from RrtsNdType
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override // from RrtsNdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor center) {
    return new ClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return new Clothoid(center, other);
      }
    };
  }

  @Override // from RrtsNdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor center) {
    return new ClothoidNdCenter(center) {
      @Override
      protected Clothoid clothoid(Tensor other) {
        return new Clothoid(other, center);
      }
    };
  }
}
