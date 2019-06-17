// code by jph
package ch.ethz.idsc.owl.data.nd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.VectorNormInterface;

public interface NdCenterInterface extends VectorNormInterface {
  /** @param center
   * @return */
  static NdCenterInterface euclidean(Tensor center) {
    return new EuclideanNdCenter(center);
  }

  /** @param center
   * @return */
  static NdCenterInterface clothoid(Tensor center) {
    return new ClothoidNdCenter(center);
  }

  /** @return center */
  Tensor center();
}