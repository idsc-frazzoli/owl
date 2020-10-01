// code by gjoel, jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.sophus.data.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.Tensor;

public interface NdType {
  /** @param tensor
   * @return nd center that measures distance to given tensor */
  NdCenterInterface ndCenterTo(Tensor tensor);

  /** @param tensor
   * @return nd center that measures distance from given tensor */
  NdCenterInterface ndCenterFrom(Tensor tensor);
}
