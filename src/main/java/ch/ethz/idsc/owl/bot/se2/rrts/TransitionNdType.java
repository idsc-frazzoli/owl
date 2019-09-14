// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.NdType;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** tested with DubinsTransitionSpace */
/* package */ class TransitionNdType implements NdType, Serializable {
  private final TransitionSpace transitionSpace;

  /** @param transitionSpace non-null */
  public TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = Objects.requireNonNull(transitionSpace);
  }

  @Override // from NdType
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from NdCenterInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(center, other).length();
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from NdCenterInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(other, center).length();
      }
    };
  }
}
