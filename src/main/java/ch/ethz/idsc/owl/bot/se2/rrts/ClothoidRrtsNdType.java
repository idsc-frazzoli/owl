// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.RrtsNdType;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidRrtsNdType implements RrtsNdType {
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
      public Clothoid clothoid(Tensor p) {
        return new Clothoid(center, p);
      }
    };
  }

  @Override // from RrtsNdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor center) {
    return new ClothoidNdCenter(center) {
      @Override
      public Clothoid clothoid(Tensor p) {
        return new Clothoid(p, center);
      }
    };
  }
}
