// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.adapter.NdType;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class TransitionNdType implements NdType, Serializable {
  private final TransitionSpace transitionSpace;

  /** @param transitionSpace non-null */
  public TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = Objects.requireNonNull(transitionSpace);
  }

  @Override // from NdType
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from NdCenterInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(other, center).length();
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from NdCenterInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(center, other).length();
      }
    };
  }
}
