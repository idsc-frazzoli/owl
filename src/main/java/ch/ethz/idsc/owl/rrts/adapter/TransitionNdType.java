// code by gjoel, jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.data.nd.AbstractNdCenter;
import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Remark: TensorDifference would be sufficient but that would result in an inconvenience. */
public class TransitionNdType implements NdType, Serializable {
  private final TransitionSpace transitionSpace;

  /** @param transitionSpace non-null */
  public TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = Objects.requireNonNull(transitionSpace);
  }

  @Override // from NdType
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from VectorNormInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(other, center).length();
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from VectorNormInterface
      public Scalar ofVector(Tensor other) {
        return transitionSpace.connect(center, other).length();
      }
    };
  }
}
