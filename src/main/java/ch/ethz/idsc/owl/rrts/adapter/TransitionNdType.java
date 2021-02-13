// code by gjoel, jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.nd.NdCenterInterface;

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
      @Override // from NdCenterInterface
      public Scalar distance(Tensor other) {
        return transitionSpace.connect(other, center).length();
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new AbstractNdCenter(center) {
      @Override // from NdCenterInterface
      public Scalar distance(Tensor other) {
        return transitionSpace.connect(center, other).length();
      }
    };
  }
}
