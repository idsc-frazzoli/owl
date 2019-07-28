// code by gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.rrts.RrtsNdType;
import ch.ethz.idsc.tensor.Tensor;

public enum RnRrtsNdType implements RrtsNdType {
  INSTANCE;
  // ---
  @Override
  public Tensor convert(Tensor tensor) {
    return tensor;
  }

  @Override
  public NdCenterInterface getNdCenterInterface(Tensor tensor) {
    return NdCenterInterface.euclidean(tensor);
  }
}
