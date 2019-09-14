// code by gjoel
package ch.ethz.idsc.owl.rrts;

import ch.ethz.idsc.owl.data.nd.EuclideanNdCenter;
import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Euclidean2dType implements NdType {
  INSTANCE;
  // ---
  @Override // from NdType
  public Tensor convert(Tensor tensor) {
    return Extract2D.FUNCTION.apply(tensor);
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceBeg(Tensor tensor) {
    return EuclideanNdCenter.of(convert(tensor));
  }

  @Override // from NdType
  public NdCenterInterface ndCenterInterfaceEnd(Tensor tensor) {
    return EuclideanNdCenter.of(convert(tensor));
  }
}
