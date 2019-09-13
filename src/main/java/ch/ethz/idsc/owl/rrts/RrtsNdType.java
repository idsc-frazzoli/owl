// code by jph
package ch.ethz.idsc.owl.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface RrtsNdType {
  /** @param tensor
   * @return tensor in right format */
  Tensor convert(Tensor tensor);

  /** @param tensor
   * @return */
  NdCenterInterface ndCenterInterfaceBeg(Tensor tensor);

  NdCenterInterface ndCenterInterfaceEnd(Tensor tensor);
}
