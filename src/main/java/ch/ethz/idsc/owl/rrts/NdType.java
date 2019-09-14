// code by jph
package ch.ethz.idsc.owl.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface NdType {
  /** @param tensor
   * @return */
  NdCenterInterface ndCenterInterfaceBeg(Tensor tensor);

  /** @param tensor
   * @return */
  NdCenterInterface ndCenterInterfaceEnd(Tensor tensor);
}
