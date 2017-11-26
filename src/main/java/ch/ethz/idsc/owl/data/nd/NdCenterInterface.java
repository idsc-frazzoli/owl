// code by jph
package ch.ethz.idsc.owl.data.nd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.VectorNormInterface;

public interface NdCenterInterface extends VectorNormInterface {
  static NdCenterInterface euclidean(Tensor center) {
    return new NdCenterInterface() {
      @Override
      public Scalar ofVector(Tensor vector) {
        return Norm._2.between(vector, center);
      }

      @Override
      public Tensor center() {
        return center;
      }
    };
  }
  // ---

  /** @return center */
  Tensor center();
}