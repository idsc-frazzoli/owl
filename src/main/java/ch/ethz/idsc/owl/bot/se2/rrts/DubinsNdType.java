// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.NdType;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class DubinsNdType implements NdType, Serializable {
  private final Scalar radius;

  public DubinsNdType(Scalar radius) {
    this.radius = radius;
  }

  @Override // from NdType
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor center) {
    return new DubinsNdCenter(center) {
      @Override
      protected DubinsPath dubinsPath(Tensor other) {
        return FixedRadiusDubins.of(center, other, radius).allValid().min(DubinsPathComparator.LENGTH).get();
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor center) {
    return new DubinsNdCenter(center) {
      @Override
      protected DubinsPath dubinsPath(Tensor other) {
        return FixedRadiusDubins.of(other, center, radius).allValid().min(DubinsPathComparator.LENGTH).get();
      }
    };
  }
}
